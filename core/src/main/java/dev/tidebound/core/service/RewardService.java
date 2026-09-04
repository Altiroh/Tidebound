package dev.tidebound.core.service;

import dev.tidebound.core.content.ItemAmount;
import dev.tidebound.core.content.RewardDefinition;
import dev.tidebound.core.data.PlayerProgress;
import dev.tidebound.core.data.TideWallet;
import dev.tidebound.core.progression.ProgressionResult;
import dev.tidebound.core.progression.ProgressionStatus;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class RewardService {
    private RewardService() {
    }

    public static ProgressionResult grantOnce(ServerPlayer player, String receiptId, RewardDefinition reward) {
        try {
            PlayerProgress current = ProgressionService.progress(player);
            if (current.hasReceipt(receiptId)) {
                return ProgressionResult.failure(ProgressionStatus.ALREADY_REWARDED,
                        "Reward already claimed: " + receiptId);
            }
            PlayerProgress next = current.claimReceipt(receiptId);
            apply(player, reward, next, () -> { });
            return ProgressionResult.completed("Reward claimed: " + receiptId);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ProgressionResult.failure(ProgressionStatus.INVALID_REWARD, exception.getMessage());
        }
    }

    static void apply(ServerPlayer player, RewardDefinition reward, PlayerProgress baseProgress,
                      Runnable beforeCommit) {
        List<ResolvedItem> resolvedItems = resolveItems(reward.items());
        TideWallet currentWallet = TideEconomy.wallet(player);
        TideWallet nextWallet = reward.tides() == 0 ? currentWallet : currentWallet.credit(reward.tides());
        PlayerProgress nextProgress = reward.skillXp().isEmpty()
                ? baseProgress
                : baseProgress.addSkillXp(reward.skillXp());

        beforeCommit.run();
        if (nextWallet != currentWallet) {
            player.setData(dev.tidebound.core.registry.TideboundAttachments.TIDE_WALLET, nextWallet);
        }
        ProgressionService.set(player, nextProgress);
        giveItems(player, resolvedItems);
    }

    static Item resolveItem(ItemAmount amount) {
        ResourceLocation id = ResourceLocation.parse(amount.itemId());
        if (!BuiltInRegistries.ITEM.containsKey(id)) {
            throw new IllegalArgumentException("Unknown item: " + amount.itemId());
        }
        return BuiltInRegistries.ITEM.get(id);
    }

    private static List<ResolvedItem> resolveItems(List<ItemAmount> amounts) {
        List<ResolvedItem> resolved = new ArrayList<>();
        for (ItemAmount amount : amounts) {
            resolved.add(new ResolvedItem(resolveItem(amount), amount.count()));
        }
        return List.copyOf(resolved);
    }

    private static void giveItems(ServerPlayer player, List<ResolvedItem> rewards) {
        for (ResolvedItem reward : rewards) {
            int remaining = reward.count();
            int maximum = reward.item().getDefaultInstance().getMaxStackSize();
            while (remaining > 0) {
                int count = Math.min(remaining, maximum);
                ItemStack stack = new ItemStack(reward.item(), count);
                player.getInventory().add(stack);
                if (!stack.isEmpty()) {
                    player.drop(stack, false);
                }
                remaining -= count;
            }
        }
    }

    private record ResolvedItem(Item item, int count) {
    }
}
