package dev.tidebound.core.service;

import dev.tidebound.core.data.PlayerVessel;
import dev.tidebound.core.data.VesselDeployment;
import dev.tidebound.core.data.VesselDeploymentState;
import dev.tidebound.core.navigation.WakeBearing;
import dev.tidebound.core.registry.TideboundItems;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Issues and resolves the player-facing Wake Compass. */
public final class WakeCompassService {
    private WakeCompassService() {
    }

    public static boolean hasCompass(ServerPlayer player) {
        return player.getInventory().countItem(TideboundItems.WAKE_COMPASS.get()) > 0;
    }

    public static boolean giveIfMissing(ServerPlayer player) {
        if (hasCompass(player)) {
            return false;
        }
        ItemStack compass = new ItemStack(TideboundItems.WAKE_COMPASS.get());
        if (!player.getInventory().add(compass)) {
            player.drop(compass, false);
        }
        return true;
    }

    public static void read(ServerPlayer player) {
        PlayerVessel vessel = VesselService.vessel(player);
        if (!vessel.unlocked()) {
            message(player, "Le compas ne reconnaît encore aucun navire.", ChatFormatting.RED);
            return;
        }

        VesselDeploymentService.findActive(player);
        VesselDeployment deployment = VesselDeploymentService.deployment(player);
        if (!deployment.hasKnownPosition()) {
            message(player, "Aucun sillage connu. Enregistrez ou mettez une barque à l'eau au port.",
                    ChatFormatting.GRAY);
            return;
        }

        String currentDimension = player.serverLevel().dimension().location().toString();
        if (!deployment.dimensionId().equals(currentDimension)) {
            message(player, "Le sillage de " + vessel.name() + " vient de " + deployment.dimensionId() + ".",
                    ChatFormatting.LIGHT_PURPLE);
            return;
        }

        double deltaX = deployment.blockX() + 0.5 - player.getX();
        double deltaZ = deployment.blockZ() + 0.5 - player.getZ();
        String direction = WakeBearing.direction(deltaX, deltaZ);
        int distance = WakeBearing.distance(deltaX, deltaZ);
        String prefix = switch (deployment.state()) {
            case DESTROYED -> "Dernier sillage avant destruction";
            case MISSING -> "Sillage interrompu — dernière trace";
            default -> vessel.name();
        };
        ChatFormatting color = deployment.state() == VesselDeploymentState.DESTROYED
                ? ChatFormatting.RED
                : deployment.state() == VesselDeploymentState.MISSING
                        ? ChatFormatting.YELLOW
                        : ChatFormatting.AQUA;
        message(player, prefix + " : " + direction + ", environ " + distance
                + " blocs — " + deployment.blockX() + ", " + deployment.blockZ() + ".", color);
    }

    /** World position the needle should point toward, or empty if none is known from here. */
    public static Optional<GlobalPos> resolveTarget(ServerPlayer player) {
        PlayerVessel vessel = VesselService.vessel(player);
        if (!vessel.unlocked()) {
            return Optional.empty();
        }
        VesselDeployment deployment = VesselDeploymentService.deployment(player);
        if (!deployment.hasKnownPosition()) {
            return Optional.empty();
        }
        if (!deployment.dimensionId().equals(player.serverLevel().dimension().location().toString())) {
            return Optional.empty();
        }
        return Optional.of(GlobalPos.of(player.serverLevel().dimension(),
                new BlockPos(deployment.blockX(), deployment.blockY(), deployment.blockZ())));
    }

    private static void message(ServerPlayer player, String text, ChatFormatting color) {
        player.displayClientMessage(Component.literal(text).withStyle(color), true);
        player.sendSystemMessage(Component.literal(text).withStyle(color));
    }
}
