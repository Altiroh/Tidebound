package dev.tidebound.core.event;

import dev.tidebound.core.fishing.CatchProfiles;
import dev.tidebound.core.progression.SkillProgression;
import dev.tidebound.core.registry.TideboundAttachments;
import dev.tidebound.core.service.CatchService;
import dev.tidebound.core.service.ProgressionService;
import dev.tidebound.core.vessel.TideboundVesselEntity;
import dev.tidebound.core.vessel.VesselModule;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Modules v1 (TB-CORE-005C): the slots already sold by the shipwright activate a fixed set of
 * effects, one per module ({@link VesselModule}). None of these ever act on an unattended vessel
 * (Spotlight requires an actual nearby spawn attempt, the rest require the owner online and close by),
 * so automated setups are never boosted.
 */
public final class VesselModuleEvents {
    private static final double NEARBY_VESSEL_RANGE = 12.0;
    private static final double SPOTLIGHT_RANGE = 24.0;
    private static final double WINCH_RANGE = 6.0;
    private static final long SONAR_INTERVAL_TICKS = 200L;
    private static final long SONAR_ADVANCED_INTERVAL_TICKS = 1_200L;
    private static final long PASSIVE_FISHING_INTERVAL_TICKS = 1_200L;
    private static final int SONAR_ADVANCED_NAVIGATION_LEVEL = 5;
    private static final int STRUCTURE_SEARCH_RADIUS_CHUNKS = 32;
    private static final double STATIONARY_SPEED_SQR = 0.0025;
    /** Chance for the Net module to add a bonus catch of the same species; read from TideboundGameplayEvents. */
    public static final float NET_MULTI_CATCH_CHANCE = 0.30F;

    private VesselModuleEvents() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(VesselModuleEvents::onPlayerTick);
        gameBus.addListener(VesselModuleEvents::onMobSpawnPositionCheck);
    }

    /** Whether the player currently has a nearby owned vessel with the given module active. */
    public static boolean isModuleActive(ServerPlayer player, VesselModule module) {
        return nearbyOwnedVessel(player)
                .map(vessel -> module.active(vessel.visualProfile().moduleSlots()))
                .orElse(false);
    }

    private static void onMobSpawnPositionCheck(MobSpawnEvent.PositionCheck event) {
        if (!(event.getEntity() instanceof Enemy)) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level) || level.isDay()) {
            return;
        }

        AABB range = AABB.ofSize(new Vec3(event.getX(), event.getY(), event.getZ()),
                SPOTLIGHT_RANGE * 2, SPOTLIGHT_RANGE * 2, SPOTLIGHT_RANGE * 2);
        boolean shielded = !level.getEntitiesOfClass(TideboundVesselEntity.class, range,
                        vessel -> VesselModule.SPOTLIGHT.active(vessel.visualProfile().moduleSlots()))
                .isEmpty();
        if (shielded) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        }
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Optional<TideboundVesselEntity> nearbyVessel = nearbyOwnedVessel(player);
        if (nearbyVessel.isEmpty()) {
            return;
        }
        TideboundVesselEntity vessel = nearbyVessel.get();
        int moduleSlots = vessel.visualProfile().moduleSlots();
        long gameTime = player.serverLevel().getGameTime();

        if (VesselModule.WINCH.active(moduleSlots)) {
            pullFloatingItems(player, vessel);
        }
        if (VesselModule.SONAR.active(moduleSlots) && onInterval(gameTime, player, SONAR_INTERVAL_TICKS)) {
            reportSonarHints(player, vessel);
        }
        if (VesselModule.SONAR.active(moduleSlots)
                && navigationLevel(player) >= SONAR_ADVANCED_NAVIGATION_LEVEL
                && onInterval(gameTime, player, SONAR_ADVANCED_INTERVAL_TICKS)) {
            reportSonarAdvancedHints(player, vessel);
        }
        if (VesselModule.NET.active(moduleSlots)
                && onInterval(gameTime, player, PASSIVE_FISHING_INTERVAL_TICKS)
                && vessel.getDeltaMovement().horizontalDistanceSqr() < STATIONARY_SPEED_SQR) {
            passiveFish(player);
        }
    }

    /** True on the tick that lands on this player's slot in the interval, spreading players across ticks. */
    private static boolean onInterval(long gameTime, ServerPlayer player, long intervalTicks) {
        return Math.floorMod(gameTime, intervalTicks) == Math.floorMod(player.getId(), intervalTicks);
    }

    private static Optional<TideboundVesselEntity> nearbyOwnedVessel(ServerPlayer player) {
        List<TideboundVesselEntity> nearby = player.serverLevel().getEntitiesOfClass(
                TideboundVesselEntity.class,
                player.getBoundingBox().inflate(NEARBY_VESSEL_RANGE),
                vessel -> vessel.hasData(TideboundAttachments.VESSEL_ENTITY_LINK)
                        && vessel.getData(TideboundAttachments.VESSEL_ENTITY_LINK).belongsTo(player.getUUID()));
        return nearby.stream().min((a, b) -> Double.compare(player.distanceToSqr(a), player.distanceToSqr(b)));
    }

    private static void pullFloatingItems(ServerPlayer player, TideboundVesselEntity vessel) {
        Vec3 center = vessel.position();
        for (ItemEntity item : player.serverLevel().getEntitiesOfClass(
                ItemEntity.class, vessel.getBoundingBox().inflate(WINCH_RANGE))) {
            if (!item.isAlive() || !item.isInWater()) {
                continue;
            }
            Vec3 pull = center.subtract(item.position());
            double distance = pull.length();
            if (distance < 0.5) {
                continue;
            }
            item.setDeltaMovement(item.getDeltaMovement().add(pull.scale(0.08 / distance)));
        }
    }

    private static void reportSonarHints(ServerPlayer player, TideboundVesselEntity vessel) {
        ServerLevel level = player.serverLevel();
        BlockPos pos = vessel.blockPosition();
        long timeOfDay = Math.floorMod(level.getDayTime(), 24_000L);
        boolean eerieWater = level.getBiome(pos).is(BiomeTags.IS_OCEAN)
                && timeOfDay >= 13_000L && timeOfDay <= 23_000L;

        boolean dangerNearby = !level.getEntitiesOfClass(Mob.class,
                        vessel.getBoundingBox().inflate(SPOTLIGHT_RANGE),
                        mob -> mob instanceof Enemy && mob.isInWaterOrBubble())
                .isEmpty();

        if (dangerNearby) {
            player.displayClientMessage(
                    Component.translatable("message.tidebound.sonar.danger").withStyle(ChatFormatting.RED), true);
        } else if (eerieWater) {
            player.displayClientMessage(
                    Component.translatable("message.tidebound.sonar.promising").withStyle(ChatFormatting.AQUA), true);
        }
    }

    private static void reportSonarAdvancedHints(ServerPlayer player, TideboundVesselEntity vessel) {
        ServerLevel level = player.serverLevel();
        BlockPos structure = level.findNearestMapStructure(
                StructureTags.SHIPWRECK, vessel.blockPosition(), STRUCTURE_SEARCH_RADIUS_CHUNKS, false);
        int floatingItems = level.getEntitiesOfClass(
                ItemEntity.class, vessel.getBoundingBox().inflate(SPOTLIGHT_RANGE)).size();

        if (structure != null) {
            player.sendSystemMessage(Component.translatable("message.tidebound.sonar.structure")
                    .withStyle(ChatFormatting.GOLD));
        }
        if (floatingItems > 0) {
            player.sendSystemMessage(Component.translatable("message.tidebound.sonar.items", floatingItems)
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    private static int navigationLevel(ServerPlayer player) {
        return SkillProgression.levelForXp(ProgressionService.progress(player).skillXp("navigation"));
    }

    private static void passiveFish(ServerPlayer player) {
        List<String> species = CatchProfiles.all().keySet().stream().sorted().toList();
        String speciesId = species.get(player.getRandom().nextInt(species.size()));
        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(speciesId)));

        CatchService.stampFishedItem(player, stack, player.blockPosition()).ifPresent(catchData -> {
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
            ProgressionService.addSkillXp(player, "fishing", 1);
            player.displayClientMessage(
                    Component.translatable("message.tidebound.net.passive_catch").withStyle(ChatFormatting.AQUA),
                    true);
        });
    }
}
