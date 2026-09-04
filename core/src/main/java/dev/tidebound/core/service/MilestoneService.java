package dev.tidebound.core.service;

import dev.tidebound.core.content.MilestoneDefinition;
import dev.tidebound.core.content.TideboundContentManager;
import dev.tidebound.core.data.PlayerProgress;
import dev.tidebound.core.progression.ProgressionResult;
import dev.tidebound.core.progression.ProgressionStatus;
import net.minecraft.server.level.ServerPlayer;

public final class MilestoneService {
    private MilestoneService() {
    }

    public static ProgressionResult complete(ServerPlayer player, String milestoneId) {
        MilestoneDefinition definition = TideboundContentManager.milestone(milestoneId).orElse(null);
        if (definition == null) {
            return ProgressionResult.failure(ProgressionStatus.UNKNOWN_DEFINITION,
                    "Unknown milestone: " + milestoneId);
        }

        PlayerProgress current = ProgressionService.progress(player);
        if (current.hasCompletedMilestone(definition.id())) {
            return ProgressionResult.failure(ProgressionStatus.ALREADY_COMPLETED,
                    "Milestone already completed: " + definition.title());
        }

        try {
            PlayerProgress completed = current.completeMilestone(definition.id());
            RewardService.apply(player, definition.reward(), completed, () -> { });
            return ProgressionResult.completed("Milestone completed: " + definition.title());
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ProgressionResult.failure(ProgressionStatus.INVALID_REWARD, exception.getMessage());
        }
    }
}
