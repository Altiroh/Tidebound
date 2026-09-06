package dev.tidebound.core.event;

import dev.tidebound.core.service.HarborPlacementService;
import dev.tidebound.core.world.HarborRegistry;
import dev.tidebound.core.world.PortPlan;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Extends the single lucky starter port ({@link StarterPortEvents}) with a broader rule so a
 * player never has to sail thousands of blocks to find one: every 512-block region has roughly a
 * one-in-three chance of holding a real port, checked as players explore into it. Across a handful
 * of regions that puts the nearest port within roughly 500-1000 blocks on average while keeping the
 * placement a matter of luck rather than a rigid guarantee.
 */
public final class RegionalPortEvents {
    private static final int REGION_SIZE = 512;
    private static final long CHECK_INTERVAL_TICKS = 600L;
    private static final long MATERIALIZE_ODDS = 3L;

    private RegionalPortEvents() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(RegionalPortEvents::onPlayerTick);
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
        PortPlan plan = PortPlan.at(level.getSeed(), regionX, regionZ);
        if (Math.floorMod(plan.siteId(), MATERIALIZE_ODDS) != 0L) {
            return;
        }
        if (HarborRegistry.get(level).contains(plan.siteId())) {
            return;
        }

        HarborPlacementService.placeNear(level, player.blockPosition(), plan);
    }
}
