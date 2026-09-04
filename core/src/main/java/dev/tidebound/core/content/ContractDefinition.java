package dev.tidebound.core.content;

import java.util.Objects;
import java.util.Optional;

public record ContractDefinition(
        String id,
        String title,
        long cooldownTicks,
        ItemAmount requirement,
        Optional<SkillRequirement> skillRequirement,
        RewardDefinition reward
) {
    public static final long MAX_COOLDOWN_TICKS = 5_184_000L;

    public ContractDefinition {
        id = requireText(id, "id", 128);
        title = requireText(title, "title", 96);
        if (cooldownTicks < 0 || cooldownTicks > MAX_COOLDOWN_TICKS) {
            throw new IllegalArgumentException("Contract cooldown must be between 0 and " + MAX_COOLDOWN_TICKS);
        }
        requirement = Objects.requireNonNull(requirement, "requirement");
        skillRequirement = Objects.requireNonNull(skillRequirement, "skillRequirement");
        reward = Objects.requireNonNull(reward, "reward");
    }

    private static String requireText(String value, String field, int maximum) {
        String normalized = Objects.requireNonNull(value, field).strip();
        if (normalized.isBlank() || normalized.length() > maximum) {
            throw new IllegalArgumentException("Invalid contract " + field);
        }
        return normalized;
    }
}
