package dev.tidebound.core.event;

import dev.tidebound.core.service.HarborPlacementService;
import dev.tidebound.core.world.PortPlan;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

/**
 * Materializes the seed-reserved starter port near spawn automatically, instead of requiring an
 * admin to run {@code /tidebound world port-place}. {@link HarborPlacementService#placeNear} is
 * already idempotent (it checks for a matching site tag nearby before building), so running this
 * on every server start is safe: it only actually builds once per world.
 */
public final class StarterPortEvents {
    private StarterPortEvents() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(StarterPortEvents::onServerStarted);
    }

    private static void onServerStarted(ServerStartedEvent event) {
        ServerLevel overworld = event.getServer().overworld();
        PortPlan.starter(overworld.getSeed()).ifPresent(plan -> {
            BlockPos spawn = overworld.getSharedSpawnPos();
            HarborPlacementService.placeNear(overworld, spawn, plan);
        });
    }
}
