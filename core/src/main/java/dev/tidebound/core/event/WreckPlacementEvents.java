package dev.tidebound.core.event;

import dev.tidebound.core.service.WreckPlacementService;
import dev.tidebound.core.world.WreckPlan;
import dev.tidebound.core.world.WreckRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * "Plateau des Épaves" first pass: every 512-block region independently has roughly a one-in-five
 * chance of holding a shipwreck, checked as players explore into it — same cadence idiom as
 * {@link RegionalPortEvents}, but with its own odds and registry so wrecks and ports don't always
 * coincide.
 */
public final class WreckPlacementEvents {
    private static final int REGION_SIZE = 512;
    private static final long CHECK_INTERVAL_TICKS = 900L;
    private static final long MATERIALIZE_ODDS = 5L;

    private WreckPlacementEvents() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(WreckPlacementEvents::onPlayerTick);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.level().dimension() != Level.OVERWORLD) {
            return;
        }
        ServerLevel level = player.serverLevel();
        long gameTime = level.getGameTime();
        if (Math.floorMod(gameTime, CHECK_INTERVAL_TICKS) != Math.floorMod(player.getId(), CHECK_INTERVAL_TICKS)) {
            return;
        }

        int regionX = Math.floorDiv(player.getBlockX(), REGION_SIZE);
        int regionZ = Math.floorDiv(player.getBlockZ(), REGION_SIZE);
        WreckPlan plan = WreckPlan.at(level.getSeed(), regionX, regionZ);
        if (Math.floorMod(plan.siteId(), MATERIALIZE_ODDS) != 0L) {
            return;
        }
        if (WreckRegistry.get(level).contains(plan.siteId())) {
            return;
        }

        WreckPlacementService.placeNear(level, player.blockPosition(), plan);
    }
}
