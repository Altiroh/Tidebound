package dev.tidebound.core.service;

import dev.tidebound.core.data.PlayerVessel;
import dev.tidebound.core.data.VesselDeployment;
import dev.tidebound.core.data.VesselEntityLink;
import dev.tidebound.core.registry.TideboundAttachments;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Materializes the persistent PlayerVessel as one owned vanilla chest boat. */
public final class VesselDeploymentService {
    public static final String PHYSICAL_VESSEL_TAG = "tidebound_owned_vessel";
    private static final int WATER_SEARCH_RADIUS = 6;
    private static final int TRACK_INTERVAL_TICKS = 20;

    private VesselDeploymentService() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(VesselDeploymentService::onPlayerTick);
        gameBus.addListener(VesselDeploymentService::onEntityInteract);
    }

    public static VesselDeployment deployment(ServerPlayer player) {
        return player.getData(TideboundAttachments.VESSEL_DEPLOYMENT);
    }

    public static ChestBoat deploy(ServerPlayer player) {
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
        ChestBoat boat = new ChestBoat(level, water.getX() + 0.5, water.getY() + 1.0, water.getZ() + 0.5);
        boat.setVariant(Boat.Type.OAK);
        boat.addTag(PHYSICAL_VESSEL_TAG);
        boat.setCustomName(Component.literal(displayName(vessel)).withStyle(ChatFormatting.AQUA));
        boat.setCustomNameVisible(true);
        boat.setData(TideboundAttachments.VESSEL_ENTITY_LINK,
                VesselEntityLink.linked(player.getUUID(), vessel.vesselId()));
        if (!level.addFreshEntity(boat)) {
            throw new IllegalStateException("Le bateau n'a pas pu être déployé");
        }
        setDeployment(player, deploymentFor(boat));
        return boat;
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
            setDeployment(player, VesselDeployment.docked());
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    public static String locate(ServerPlayer player) {
        Optional<Entity> entity = findActive(player);
        return entity.map(VesselDeploymentService::position)
                .orElse("aucun bateau actuellement déployé");
    }

    public static void syncLoaded(ServerPlayer player) {
        findActive(player).ifPresent(entity -> syncEntity(player, entity));
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(player.getVehicle() instanceof Boat boat)
                || !isOwnedBy(boat, player.getUUID())) {
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

    private static boolean isOwnedBy(Entity entity, UUID playerId) {
        return entity.hasData(TideboundAttachments.VESSEL_ENTITY_LINK)
                && entity.getData(TideboundAttachments.VESSEL_ENTITY_LINK).belongsTo(playerId);
    }

    private static void syncEntity(ServerPlayer player, Entity entity) {
        PlayerVessel vessel = VesselService.vessel(player);
        if (vessel.unlocked()) {
            entity.setCustomName(Component.literal(displayName(vessel)).withStyle(ChatFormatting.AQUA));
            entity.setCustomNameVisible(true);
        }
    }

    private static void applyRuntimeUpgrades(ServerPlayer player, Boat boat) {
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
        return VesselDeployment.active(
                entity.getUUID(),
                entity.level().dimension().location().toString(),
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
