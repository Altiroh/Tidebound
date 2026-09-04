package dev.tidebound.core.service;

import dev.tidebound.core.content.ContractDefinition;
import dev.tidebound.core.content.ItemAmount;
import dev.tidebound.core.content.TideboundContentManager;
import dev.tidebound.core.data.ContractProgress;
import dev.tidebound.core.data.PlayerProgress;
import dev.tidebound.core.progression.ProgressionResult;
import dev.tidebound.core.progression.ProgressionStatus;
import dev.tidebound.core.progression.SkillProgression;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ContractService {
    private ContractService() {
    }

    public static ProgressionResult complete(ServerPlayer player, String contractId) {
        ContractDefinition definition = TideboundContentManager.contract(contractId).orElse(null);
        if (definition == null) {
            return ProgressionResult.failure(ProgressionStatus.UNKNOWN_DEFINITION,
                    "Unknown contract: " + contractId);
        }

        long gameTime = player.getServer().overworld().getGameTime();
        PlayerProgress current = ProgressionService.progress(player);
        if (definition.skillRequirement().isPresent()) {
            var requirement = definition.skillRequirement().orElseThrow();
            int level = SkillProgression.levelForXp(current.skillXp(requirement.skillId()));
            if (level < requirement.level()) {
                return ProgressionResult.failure(ProgressionStatus.LOCKED_SKILL,
                        "Requires " + requirement.skillId() + " level " + requirement.level());
            }
        }
        ContractProgress state = current.contract(definition.id());
        if (!state.isAvailable(gameTime)) {
            return ProgressionResult.failure(ProgressionStatus.ON_COOLDOWN,
                    "Contract available in " + (state.nextAvailableAt() - gameTime) + " ticks");
        }

        try {
            ItemAmount requirement = definition.requirement();
            Item requiredItem = RewardService.resolveItem(requirement);
            if (player.getInventory().countItem(requiredItem) < requirement.count()) {
                return ProgressionResult.failure(ProgressionStatus.MISSING_ITEMS,
                        "Missing items: " + requirement.count() + " x " + requirement.itemId());
            }

            PlayerProgress completed = current.completeContract(
                    definition.id(), gameTime, definition.cooldownTicks());
            RewardService.apply(player, definition.reward(), completed,
                    () -> consume(player.getInventory(), requiredItem, requirement.count()));
            ProgressionResult firstSale = MilestoneService.complete(player, "tidebound:first_sale");
            String bonus = firstSale.success() ? " — premier échange récompensé" : "";
            return ProgressionResult.completed("Contract completed: " + definition.title() + bonus);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ProgressionResult.failure(ProgressionStatus.INVALID_REWARD, exception.getMessage());
        }
    }

    private static void consume(Inventory inventory, Item item, int requested) {
        int remaining = requested;
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.is(item)) {
                continue;
            }
            int removed = Math.min(stack.getCount(), remaining);
            stack.shrink(removed);
            remaining -= removed;
        }
        if (remaining != 0) {
            throw new IllegalStateException("Inventory changed while completing the contract");
        }
        inventory.setChanged();
    }
}
