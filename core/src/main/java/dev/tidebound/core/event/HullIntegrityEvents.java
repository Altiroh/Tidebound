package dev.tidebound.core.event;

import dev.tidebound.core.registry.TideboundAttachments;
import dev.tidebound.core.service.VesselService;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.Boat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Sailing an under-upgraded vessel into a {@code #tidebound:dangerous} biome (abyssal/frozen waters
 * for now) wears down the hull over time. Below {@link #SAFE_HULL_TIER}, this applies to both the
 * Barque de fortune (permanently tier 1) and a Tidebound vessel that hasn't upgraded its hull yet —
 * matching the idea that some waters simply aren't safe without a proper refit. The existing red
 * biome-change announcement ({@link BiomeAwarenessEvents}) already warns the player on entry; this is
 * the mechanical consequence of ignoring it.
 */
public final class HullIntegrityEvents {
    private static final long CHECK_INTERVAL_TICKS = 40L;
    private static final int SAFE_HULL_TIER = 3;
    private static final float DAMAGE_PER_INTERVAL = 2.0F;

    private HullIntegrityEvents() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(HullIntegrityEvents::onPlayerTick);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !(player.getVehicle() instanceof Boat boat)) {
            return;
        }
        if (!boat.hasData(TideboundAttachments.VESSEL_ENTITY_LINK)
                || !boat.getData(TideboundAttachments.VESSEL_ENTITY_LINK).belongsTo(player.getUUID())) {
            return;
        }

        ServerLevel level = player.serverLevel();
        long gameTime = level.getGameTime();
        if (Math.floorMod(gameTime, CHECK_INTERVAL_TICKS) != Math.floorMod(player.getId(), CHECK_INTERVAL_TICKS)) {
            return;
        }
        if (!level.getBiome(boat.blockPosition()).is(BiomeAwarenessEvents.DANGEROUS)) {
            return;
        }
        if (VesselService.vessel(player).hullTier() >= SAFE_HULL_TIER) {
            return;
        }

        boat.setDamage(boat.getDamage() + DAMAGE_PER_INTERVAL);
    }
}
