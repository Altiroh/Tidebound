package dev.tidebound.core.service;

import dev.tidebound.core.navigation.WakeBearing;
import dev.tidebound.core.world.HarborRegistry;
import dev.tidebound.core.world.HarborSite;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** Resolves the nearest indexed harbour containing an Intendant without loading its chunks. */
public final class HavenCompassService {
    private HavenCompassService() {
    }

    public static void read(ServerPlayer player) {
        String dimensionId = player.serverLevel().dimension().location().toString();
        HarborSite site = HarborRegistry.get(player.serverLevel())
                .nearestIntendant(dimensionId, player.blockPosition())
                .orElse(null);
        if (site == null) {
            message(player, "Aucun havre doté d'un Intendant n'est encore répertorié.", ChatFormatting.GRAY);
            return;
        }

        double deltaX = site.position().getX() + 0.5 - player.getX();
        double deltaZ = site.position().getZ() + 0.5 - player.getZ();
        int distance = WakeBearing.distance(deltaX, deltaZ);
        if (distance <= 24) {
            message(player, "Le havre recherché est ici.", ChatFormatting.GREEN);
            return;
        }
        String direction = WakeBearing.direction(deltaX, deltaZ);
        message(player, "Un Intendant se trouve vers " + direction + ", à environ " + distance
                + " blocs.", ChatFormatting.GOLD);
    }

    /** World position the needle should point toward, or empty if no harbour is indexed yet. */
    public static Optional<GlobalPos> resolveTarget(ServerPlayer player) {
        return HarborRegistry.get(player.serverLevel())
                .nearestIntendant(player.serverLevel().dimension().location().toString(), player.blockPosition())
                .map(site -> GlobalPos.of(player.serverLevel().dimension(), site.position()));
    }

    private static void message(ServerPlayer player, String text, ChatFormatting color) {
        player.displayClientMessage(Component.literal(text).withStyle(color), true);
        player.sendSystemMessage(Component.literal(text).withStyle(color));
    }
}
