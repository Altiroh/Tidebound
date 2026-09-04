package dev.tidebound.core.content;

import java.util.Objects;

public record MilestoneDefinition(String id, String title, String trigger, RewardDefinition reward) {
    public MilestoneDefinition {
        id = requireText(id, "id", 128);
        title = requireText(title, "title", 96);
        trigger = requireText(trigger, "trigger", 64);
        reward = Objects.requireNonNull(reward, "reward");
    }

    private static String requireText(String value, String field, int maximum) {
        String normalized = Objects.requireNonNull(value, field).strip();
        if (normalized.isBlank() || normalized.length() > maximum) {
            throw new IllegalArgumentException("Invalid milestone " + field);
        }
        return normalized;
    }
}
