package dev.tidebound.core.event;

import dev.tidebound.core.fishing.CatchAnomaly;
import dev.tidebound.core.fishing.CatchData;
import dev.tidebound.core.fishing.CatchQuality;
import dev.tidebound.core.progression.ProgressionResult;
import dev.tidebound.core.progression.SkillProgression;
import dev.tidebound.core.service.CatchService;
import dev.tidebound.core.service.MilestoneService;
import dev.tidebound.core.service.ProgressionService;
import dev.tidebound.core.vessel.VesselModule;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Server-authoritative sandbox triggers. */
public final class TideboundGameplayEvents {
    private static final int NAVIGATION_INTERVAL_TICKS = 400;

    private TideboundGameplayEvents() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(TideboundGameplayEvents::onItemFished);
        gameBus.addListener(TideboundGameplayEvents::onItemTooltip);
        gameBus.addListener(TideboundGameplayEvents::onPlayerTick);
    }

    private static void onItemFished(ItemFishedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.getDrops().isEmpty()) {
            return;
        }

        List<CatchData> catches = new ArrayList<>();
        ItemStack displayedStack = ItemStack.EMPTY;
        boolean netActive = VesselModuleEvents.isModuleActive(player, VesselModule.NET);
        for (ItemStack drop : List.copyOf(event.getDrops())) {
            var stamped = CatchService.stampFishedItem(player, drop, event.getHookEntity().blockPosition());
            if (stamped.isPresent()) {
                catches.add(stamped.orElseThrow());
                if (displayedStack.isEmpty()) {
                    displayedStack = drop;
                }
                if (netActive && player.getRandom().nextFloat() < VesselModuleEvents.NET_MULTI_CATCH_CHANCE) {
                    ItemStack bonus = new ItemStack(drop.getItem());
                    CatchService.stampFishedItem(player, bonus, event.getHookEntity().blockPosition())
                            .ifPresent(catches::add);
                    event.getDrops().add(bonus);
                }
            }
        }
        if (catches.isEmpty()) {
            return;
        }

        long before = ProgressionService.progress(player).skillXp("fishing");
        notifyMilestone(player, MilestoneService.complete(player, "tidebound:first_catch"));
        long xp = 5L * catches.size();
        ProgressionService.addSkillXp(player, "fishing", xp);
        notifySkillProgress(player, "fishing", before);
        notifyCatch(player, displayedStack, catches.getFirst(), xp);
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        CatchData data = CatchService.data(event.getItemStack()).orElse(null);
        if (data == null) {
            return;
        }

        long gameTime = event.getEntity() == null
                ? data.caughtAtGameTime()
                : event.getEntity().level().getGameTime();
        long value = CatchService.value(event.getItemStack(), gameTime).orElse(0L);
        event.getToolTip().add(Component.translatable("tooltip.tidebound.catch.weight", data.weightGrams())
                .withStyle(ChatFormatting.GRAY));
        event.getToolTip().add(Component.translatable("tooltip.tidebound.catch.quality",
                        Component.translatable("catch_quality.tidebound." + data.quality().id()))
                .withStyle(qualityColor(data.quality())));
        event.getToolTip().add(Component.translatable("tooltip.tidebound.catch.freshness",
                        Component.translatable("catch_freshness.tidebound." + data.freshness(gameTime).id()))
                .withStyle(ChatFormatting.AQUA));
        event.getToolTip().add(Component.translatable("tooltip.tidebound.catch.origin", data.originBiomeId())
                .withStyle(ChatFormatting.DARK_AQUA));
        if (value > 0) {
            event.getToolTip().add(Component.translatable("tooltip.tidebound.catch.value", value)
                    .withStyle(ChatFormatting.GOLD));
        }
        if (data.anomalous()) {
            event.getToolTip().add(Component.translatable("tooltip.tidebound.catch.anomaly",
                            Component.translatable("catch_anomaly.tidebound." + data.anomaly().id()))
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
        if (event.getFlags().isAdvanced()) {
            event.getToolTip().add(Component.literal(data.speciesId() + " @ " + data.caughtAtGameTime())
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
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

    private static void notifyCatch(ServerPlayer player, ItemStack stack, CatchData data, long xp) {
        long gameTime = player.getServer().overworld().getGameTime();
        long value = CatchService.value(stack, gameTime).orElse(0L);
        player.displayClientMessage(Component.translatable("message.tidebound.catch.caught",
                stack.getHoverName(), data.weightGrams(),
                Component.translatable("catch_quality.tidebound." + data.quality().id()), value, xp)
                .withStyle(qualityColor(data.quality())), true);
        if (data.anomaly() != CatchAnomaly.NONE) {
            player.sendSystemMessage(Component.translatable("message.tidebound.catch.anomalous",
                    stack.getHoverName()).withStyle(ChatFormatting.DARK_PURPLE));
        } else if (data.quality() == CatchQuality.LEGENDARY) {
            player.sendSystemMessage(Component.translatable("message.tidebound.catch.legendary",
                    stack.getHoverName(), data.weightGrams()).withStyle(ChatFormatting.GOLD));
        }
    }

    private static ChatFormatting qualityColor(CatchQuality quality) {
        return switch (quality) {
            case COMMON -> ChatFormatting.WHITE;
            case FINE -> ChatFormatting.GREEN;
            case EXCEPTIONAL -> ChatFormatting.AQUA;
            case LEGENDARY -> ChatFormatting.GOLD;
        };
    }
}
