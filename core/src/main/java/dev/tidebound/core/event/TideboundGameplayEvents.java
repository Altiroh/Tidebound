package dev.tidebound.core.event;

import dev.tidebound.core.progression.ProgressionResult;
import dev.tidebound.core.progression.SkillProgression;
import dev.tidebound.core.service.MilestoneService;
import dev.tidebound.core.service.ProgressionService;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.vehicle.Boat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Server-authoritative sandbox triggers. */
public final class TideboundGameplayEvents {
    private static final int NAVIGATION_INTERVAL_TICKS = 400;

    private TideboundGameplayEvents() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(TideboundGameplayEvents::onItemFished);
        gameBus.addListener(TideboundGameplayEvents::onPlayerTick);
    }

    private static void onItemFished(ItemFishedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.getDrops().isEmpty()) {
            return;
        }

        long before = ProgressionService.progress(player).skillXp("fishing");
        notifyMilestone(player, MilestoneService.complete(player, "tidebound:first_catch"));
        ProgressionService.addSkillXp(player, "fishing", 5);
        notifySkillProgress(player, "fishing", before);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        long gameTime = player.serverLevel().getGameTime();
        if (Math.floorMod(gameTime, NAVIGATION_INTERVAL_TICKS)
                != Math.floorMod(player.getId(), NAVIGATION_INTERVAL_TICKS)) {
            return;
        }
        if (!(player.getVehicle() instanceof Boat)
                || !player.serverLevel().getBiome(player.blockPosition()).is(BiomeTags.IS_OCEAN)) {
            return;
        }

        long before = ProgressionService.progress(player).skillXp("navigation");
        notifyMilestone(player, MilestoneService.complete(player, "tidebound:open_water"));
        ProgressionService.addSkillXp(player, "navigation", 2);
        notifySkillProgress(player, "navigation", before);
    }

    private static void notifyMilestone(ServerPlayer player, ProgressionResult result) {
        if (result.success()) {
            player.sendSystemMessage(Component.literal("Palier Tidebound — " + result.message())
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    private static void notifySkillProgress(ServerPlayer player, String skill, long previousXp) {
        long currentXp = ProgressionService.progress(player).skillXp(skill);
        int previousLevel = SkillProgression.levelForXp(previousXp);
        int currentLevel = SkillProgression.levelForXp(currentXp);
        if (currentLevel > previousLevel) {
            player.sendSystemMessage(Component.literal("Niveau " + currentLevel + " en " + skill + " !")
                    .withStyle(ChatFormatting.AQUA));
        } else {
            player.displayClientMessage(Component.literal("+" + (currentXp - previousXp)
                    + " XP " + skill).withStyle(ChatFormatting.AQUA), true);
        }
    }
}
