package dev.tidebound.core.service;

import dev.tidebound.core.data.PlayerProgress;
import dev.tidebound.core.registry.TideboundAttachments;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;

public final class ProgressionService {
    private ProgressionService() {
    }

    public static PlayerProgress progress(ServerPlayer player) {
        return player.getData(TideboundAttachments.PLAYER_PROGRESS);
    }

    static void set(ServerPlayer player, PlayerProgress progress) {
        player.setData(TideboundAttachments.PLAYER_PROGRESS, progress);
    }

    public static PlayerProgress addSkillXp(ServerPlayer player, String skillId, long amount) {
        PlayerProgress updated = progress(player).addSkillXp(Map.of(skillId, amount));
        set(player, updated);
        return updated;
    }
}
