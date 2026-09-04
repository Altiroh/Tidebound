package dev.tidebound.core.service;

import dev.tidebound.core.data.PlayerVessel;
import dev.tidebound.core.data.VesselUpgrade;
import dev.tidebound.core.registry.TideboundAttachments;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;

public final class VesselService {
    private VesselService() {
    }

    public static PlayerVessel vessel(ServerPlayer player) {
        return player.getData(TideboundAttachments.PLAYER_VESSEL);
    }

    /**
     * Idempotent unlock entry point for a future harbourmaster NPC or milestone reward.
     */
    public static PlayerVessel unlock(ServerPlayer player, String name) {
        PlayerVessel current = vessel(player);
        if (current.unlocked()) {
            return current;
        }
        PlayerVessel unlocked = PlayerVessel.unlock(name, UUID.randomUUID());
        player.setData(TideboundAttachments.PLAYER_VESSEL, unlocked);
        return unlocked;
    }

    public static PlayerVessel upgrade(ServerPlayer player, VesselUpgrade upgrade) {
        PlayerVessel current = vessel(player);
        PlayerVessel updated = switch (upgrade) {
            case HULL -> current.upgradeHull();
            case MOTOR -> current.upgradeMotor();
            case HOLD -> current.upgradeHold();
            case MODULE_SLOT -> current.addModuleSlot();
        };
        player.setData(TideboundAttachments.PLAYER_VESSEL, updated);
        VesselDeploymentService.syncLoaded(player);
        return updated;
    }

    public static PlayerVessel rename(ServerPlayer player, String name) {
        PlayerVessel updated = vessel(player).rename(name);
        player.setData(TideboundAttachments.PLAYER_VESSEL, updated);
        VesselDeploymentService.syncLoaded(player);
        return updated;
    }
}
