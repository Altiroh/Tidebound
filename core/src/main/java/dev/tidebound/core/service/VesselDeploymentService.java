package dev.tidebound.core.service;

import dev.tidebound.core.data.PlayerVessel;
import dev.tidebound.core.data.VesselDeployment;
import dev.tidebound.core.data.VesselEntityLink;
import dev.tidebound.core.data.VesselHoldPolicy;
import dev.tidebound.core.registry.TideboundAttachments;
import dev.tidebound.core.registry.TideboundEntities;
import dev.tidebound.core.vessel.TideboundVesselEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Materializes PlayerVessel as Tidebound's dedicated physical vessel and migrates legacy boats. */
public final class VesselDeploymentService {
    public static final String PHYSICAL_VESSEL_TAG = "tidebound_owned_vessel";
    private static final int WATER_SEARCH_RADIUS = 6;
    private static final int TRACK_INTERVAL_TICKS = 20;

    private VesselDeploymentService() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(VesselDeploymentService::onPlayerTick);
        gameBus.addListener(VesselDeploymentService::onEntityInteract);
        gameBus.addListener(VesselDeploymentService::onEntityLeaveLevel);
    }

    public static VesselDeployment deployment(ServerPlayer player) {
        return player.getData(TideboundAttachments.VESSEL_DEPLOYMENT);
    }

    public static TideboundVesselEntity deploy(ServerPlayer player) {
        if (!HarborBoardService.isNearBoard(player)) {
            throw new IllegalStateException("Approchez-vous d'un intendant de port pour déployer le bateau");
        }
        PlayerVessel vessel = VesselService.vessel(player);
        if (!vessel.unlocked()) {
            throw new IllegalStateException("Réclamez d'abord votre bateau auprès de l'intendant");
        }
        Optional<Entity> existing = findActive(player);
        if (existing.isPresent()) {
            throw new IllegalStateException("Votre bateau est déjà déployé en "
                    + position(existing.orElseThrow()));
        }

        ServerLevel level = player.serverLevel();
        BlockPos water = findWater(level, player.blockPosition())
                .orElseThrow(() -> new IllegalStateException("Aucune eau libre trouvée dans un rayon de "
                        + WATER_SEARCH_RADIUS + " blocs"));
        TideboundVesselEntity boat = createVessel(level);
        boat.moveTo(water.getX() + 0.5, water.getY() + 1.0, water.getZ() + 0.5);
        boat.setVariant(Boat.Type.OAK);
        configureOwnedBoat(player, boat, vessel);
        if (!level.addFreshEntity(boat)) {
            throw new IllegalStateException("Le bateau n'a pas pu être déployé");
        }
        setDeployment(player, deploymentFor(boat));
        return boat;
    }

    public static Boat registerNearbyVanillaBoat(ServerPlayer player, String name) {
        if (!HarborBoardService.isNearBoard(player)) {
            throw new IllegalStateException("Approchez-vous d'un intendant de port pour enregistrer la barque");
        }
        if (VesselService.vessel(player).unlocked()) {
            throw new IllegalStateException("Vous possédez déjà un navire Tidebound");
        }

        Boat boat = nearestUnownedBoat(player).orElseThrow(() -> new IllegalStateException(
                "Aucune barque vanilla libre trouvée dans un rayon de 8 blocs"));
        PlayerVessel before = VesselService.vessel(player);
        PlayerVessel vessel = VesselService.unlock(player, name);
        try {
            configureOwnedBoat(player, boat, vessel);
            setDeployment(player, deploymentFor(boat));
            WakeCompassService.giveIfMissing(player);
            return boat;
        } catch (RuntimeException exception) {
            player.setData(TideboundAttachments.PLAYER_VESSEL, before);
            throw exception;
        }
    }

    public static Optional<Entity> findActive(ServerPlayer player) {
        VesselDeployment deployment = deployment(player);
        if (!deployment.active()) {
            return Optional.empty();
        }
        ServerLevel level = levelFor(player, deployment.dimensionId());
        if (level == null) {
            return Optional.empty();
        }

        for (int x = deployment.chunkX() - 1; x <= deployment.chunkX() + 1; x++) {
            for (int z = deployment.chunkZ() - 1; z <= deployment.chunkZ() + 1; z++) {
                level.getChunk(x, z);
            }
        }
        Entity entity = level.getEntity(UUID.fromString(deployment.entityId()));
        if (!(entity instanceof Boat) || !isOwnedBy(entity, player.getUUID())) {
            setDeployment(player, deployment.markMissing());
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    public static String locate(ServerPlayer player) {
        Optional<Entity> entity = findActive(player);
        if (entity.isPresent()) {
            return position(entity.orElseThrow());
        }
        VesselDeployment deployment = deployment(player);
        if (deployment.hasKnownPosition()) {
            return deployment.state().id() + " — " + deployment.dimensionId() + " — "
                    + deployment.blockX() + ", " + deployment.blockY() + ", " + deployment.blockZ();
        }
        return "aucune position de bateau connue";
    }

    public static void syncLoaded(ServerPlayer player) {
        findActive(player).ifPresent(entity -> syncEntity(player, entity));
    }

    /** Replaces a legacy registered boat at the harbour while retaining its persistent identity. */
    public static TideboundVesselEntity refitAtHarbor(ServerPlayer player) {
        if (!HarborBoardService.isNearShipwright(player)) {
            throw new IllegalStateException("Approchez-vous du charpentier naval pour construire le navire");
        }
        PlayerVessel vessel = VesselService.vessel(player);
        if (!vessel.unlocked()) {
            throw new IllegalStateException("Enregistrez d'abord une barque");
        }
        Entity entity = findActive(player)
                .orElseThrow(() -> new IllegalStateException("Amenez votre navire au quai avant la transformation"));
        if (entity.distanceToSqr(player) > 12.0 * 12.0) {
            throw new IllegalStateException("Amenez votre navire au quai avant la transformation");
        }
        if (entity instanceof TideboundVesselEntity tidebound) {
            tidebound.syncVisuals(vessel);
            return tidebound;
        }
        if (!(entity instanceof Boat boat)) {
            throw new IllegalStateException("Le navire physique ne peut pas être transformé");
        }
        return migrate(player, boat, vessel);
    }

    /** Ensures the physical vessel provides a cargo inventory after legacy-save migration. */
    public static ChestBoat ensureCargoVessel(ServerPlayer player, PlayerVessel vessel) {
        Entity entity = findActive(player)
                .orElseThrow(() -> new IllegalStateException("Le navire physique est introuvable"));
        if (entity instanceof ChestBoat chestBoat) {
            if (chestBoat instanceof TideboundVesselEntity tidebound) {
                tidebound.syncVisuals(vessel);
            }
            enforceHoldCapacity(player, chestBoat);
            return chestBoat;
        }
        if (!(entity instanceof Boat boat)) {
            throw new IllegalStateException("Le navire ne peut pas recevoir de cale");
        }
        ChestBoat cargoBoat = migrate(player, boat, vessel);
        enforceHoldCapacity(player, cargoBoat);
        return cargoBoat;
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.tickCount % TRACK_INTERVAL_TICKS == 0) {
            enforceNearbyHoldCapacity(player);
        }
        if (!(player.getVehicle() instanceof Boat boat) || !isOwnedBy(boat, player.getUUID())) {
            return;
        }
        applyRuntimeUpgrades(player, boat);
        if (player.tickCount % TRACK_INTERVAL_TICKS != 0) {
            return;
        }
        syncEntity(player, boat);
        VesselDeployment current = deployment(player);
        VesselDeployment updated = deploymentFor(boat);
        if (!current.equals(updated)) {
            setDeployment(player, updated);
        }
    }

    private static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        if (!(target instanceof Boat) || !target.hasData(TideboundAttachments.VESSEL_ENTITY_LINK)) {
            return;
        }
        VesselEntityLink link = target.getData(TideboundAttachments.VESSEL_ENTITY_LINK);
        if (!link.linked() || link.belongsTo(event.getEntity().getUUID())) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);
        if (event.getEntity() instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.literal("Ce bateau appartient à un autre capitaine.")
                    .withStyle(ChatFormatting.RED));
        }
    }

    private static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof Boat boat)
                || boat.getRemovalReason() != Entity.RemovalReason.KILLED
                || !boat.hasData(TideboundAttachments.VESSEL_ENTITY_LINK)) {
            return;
        }
        VesselEntityLink link = boat.getData(TideboundAttachments.VESSEL_ENTITY_LINK);
        if (!link.linked() || !(boat.level() instanceof ServerLevel level)) {
            return;
        }
        ServerPlayer owner = level.getServer().getPlayerList().getPlayer(UUID.fromString(link.ownerId()));
        if (owner == null) {
            return;
        }
        VesselDeployment current = deployment(owner);
        if (current.entityId().equals(boat.getUUID().toString())) {
            setDeployment(owner, deploymentFor(boat).markDestroyed());
            owner.sendSystemMessage(Component.literal("Votre navire a été détruit. Le Compas de sillage conserve "
                    + "sa dernière position.").withStyle(ChatFormatting.RED));
        }
    }

    private static boolean isOwnedBy(Entity entity, UUID playerId) {
        return entity.hasData(TideboundAttachments.VESSEL_ENTITY_LINK)
                && entity.getData(TideboundAttachments.VESSEL_ENTITY_LINK).belongsTo(playerId);
    }

    private static Optional<Boat> nearestUnownedBoat(ServerPlayer player) {
        if (player.getVehicle() instanceof Boat ridden && isRegistrationCandidate(ridden)) {
            return Optional.of(ridden);
        }
        return player.serverLevel().getEntitiesOfClass(
                        Boat.class,
                        player.getBoundingBox().inflate(8.0),
                        VesselDeploymentService::isRegistrationCandidate
                ).stream()
                .min(Comparator.comparingDouble(player::distanceToSqr));
    }

    private static boolean isRegistrationCandidate(Boat boat) {
        if (!boat.isAlive()) {
            return false;
        }
        return !boat.hasData(TideboundAttachments.VESSEL_ENTITY_LINK)
                || !boat.getData(TideboundAttachments.VESSEL_ENTITY_LINK).linked();
    }

    private static void configureOwnedBoat(ServerPlayer player, Boat boat, PlayerVessel vessel) {
        boat.addTag(PHYSICAL_VESSEL_TAG);
        boat.setCustomName(Component.literal(displayName(vessel)).withStyle(ChatFormatting.AQUA));
        boat.setCustomNameVisible(true);
        boat.setData(TideboundAttachments.VESSEL_ENTITY_LINK,
                VesselEntityLink.linked(player.getUUID(), vessel.vesselId()));
        if (boat instanceof TideboundVesselEntity tidebound) {
            tidebound.syncVisuals(vessel);
        }
    }

    private static void syncEntity(ServerPlayer player, Entity entity) {
        PlayerVessel vessel = VesselService.vessel(player);
        if (vessel.unlocked()) {
            entity.setCustomName(Component.literal(displayName(vessel)).withStyle(ChatFormatting.AQUA));
            entity.setCustomNameVisible(true);
            if (entity instanceof TideboundVesselEntity tidebound) {
                tidebound.syncVisuals(vessel);
            }
        }
    }

    private static TideboundVesselEntity migrate(ServerPlayer player, Boat source, PlayerVessel vessel) {
        if (!(source.level() instanceof ServerLevel level)) {
            throw new IllegalStateException("Le navire ne peut être transformé que côté serveur");
        }
        TideboundVesselEntity target = createVessel(level);
        target.moveTo(source.getX(), source.getY(), source.getZ(), source.getYRot(), source.getXRot());
        target.setVariant(source.getVariant());
        target.setDeltaMovement(source.getDeltaMovement());
        target.setDamage(source.getDamage());
        configureOwnedBoat(player, target, vessel);

        var passengers = new ArrayList<>(source.getPassengers());
        passengers.forEach(Entity::stopRiding);
        if (!level.addFreshEntity(target)) {
            passengers.forEach(passenger -> passenger.startRiding(source, true));
            throw new IllegalStateException("Le chantier naval n'a pas pu mettre le nouveau navire à l'eau");
        }
        if (source instanceof ChestBoat cargo) {
            for (int slot = 0; slot < Math.min(cargo.getContainerSize(), target.getContainerSize()); slot++) {
                target.setItem(slot, cargo.getItem(slot).copy());
                cargo.setItem(slot, ItemStack.EMPTY);
            }
        }
        passengers.forEach(passenger -> passenger.startRiding(target, true));
        source.discard();
        setDeployment(player, deploymentFor(target));
        enforceHoldCapacity(player, target);
        return target;
    }

    private static TideboundVesselEntity createVessel(ServerLevel level) {
        TideboundVesselEntity vessel = TideboundEntities.VESSEL.get().create(level);
        if (vessel == null) {
            throw new IllegalStateException("Le type de navire Tidebound n'a pas pu être créé");
        }
        return vessel;
    }

    private static void applyRuntimeUpgrades(ServerPlayer player, Boat boat) {
        if (!(boat instanceof TideboundVesselEntity)) {
            return;
        }
        PlayerVessel vessel = VesselService.vessel(player);
        Vec3 movement = boat.getDeltaMovement();
        if (movement.horizontalDistanceSqr() > 0.0001) {
            double motorFactor = 1.0 + (vessel.motorTier() - 1) * 0.0025;
            boat.setDeltaMovement(movement.x * motorFactor, movement.y, movement.z * motorFactor);
        }
        if (vessel.hullTier() > 1 && boat.getDamage() > 0) {
            float recovery = (vessel.hullTier() - 1) * 0.05F;
            boat.setDamage(Math.max(0, boat.getDamage() - recovery));
        }
    }

    private static void enforceNearbyHoldCapacity(ServerPlayer player) {
        player.serverLevel().getEntitiesOfClass(
                ChestBoat.class,
                player.getBoundingBox().inflate(12.0),
                boat -> isOwnedBy(boat, player.getUUID())
        ).forEach(boat -> enforceHoldCapacity(player, boat));
    }

    private static void enforceHoldCapacity(ServerPlayer player, ChestBoat boat) {
        int allowed = VesselHoldPolicy.usableSlots(VesselService.vessel(player).holdTier());
        if (allowed >= boat.getContainerSize()) {
            return;
        }
        boolean moved = false;
        boolean dropped = false;
        for (int slot = allowed; slot < boat.getContainerSize(); slot++) {
            ItemStack existing = boat.getItem(slot);
            if (existing.isEmpty()) {
                continue;
            }
            ItemStack overflow = existing.copy();
            boat.setItem(slot, ItemStack.EMPTY);
            moved = true;
            player.getInventory().add(overflow);
            if (!overflow.isEmpty()) {
                boat.spawnAtLocation(overflow);
                dropped = true;
            }
        }
        if (moved) {
            boat.setChanged();
            String suffix = dropped ? " Le surplus a été posé près du navire." : " Le surplus vous a été rendu.";
            player.sendSystemMessage(Component.literal("Cale niveau "
                    + VesselService.vessel(player).holdTier() + " : " + allowed
                    + " emplacements utilisables." + suffix).withStyle(ChatFormatting.YELLOW));
        }
    }

    private static Optional<BlockPos> findWater(ServerLevel level, BlockPos origin) {
        for (int radius = 0; radius <= WATER_SEARCH_RADIUS; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    for (int dy = 1; dy >= -2; dy--) {
                        BlockPos candidate = origin.offset(dx, dy, dz);
                        if (level.getFluidState(candidate).is(FluidTags.WATER)
                                && level.getBlockState(candidate.above()).isAir()) {
                            return Optional.of(candidate);
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static ServerLevel levelFor(ServerPlayer player, String dimensionId) {
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimensionId));
        return player.getServer().getLevel(key);
    }

    private static VesselDeployment deploymentFor(Entity entity) {
        BlockPos position = entity.blockPosition();
        return VesselDeployment.active(
                entity.getUUID(),
                entity.level().dimension().location().toString(),
                position.getX(),
                position.getY(),
                position.getZ(),
                entity.chunkPosition().x,
                entity.chunkPosition().z
        );
    }

    private static void setDeployment(ServerPlayer player, VesselDeployment deployment) {
        player.setData(TideboundAttachments.VESSEL_DEPLOYMENT, deployment);
    }

    private static String displayName(PlayerVessel vessel) {
        return vessel.name() + "  [Coque " + vessel.hullTier()
                + " • Moteur " + vessel.motorTier()
                + " • Cale " + vessel.holdTier()
                + " • Modules " + vessel.moduleSlots() + "]";
    }

    private static String position(Entity entity) {
        return entity.level().dimension().location() + " — "
                + entity.blockPosition().getX() + ", "
                + entity.blockPosition().getY() + ", "
                + entity.blockPosition().getZ();
    }
}
