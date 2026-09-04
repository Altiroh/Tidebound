package dev.tidebound.core.content;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record RewardDefinition(long tides, Map<String, Long> skillXp, List<ItemAmount> items) {
    public RewardDefinition {
        if (tides < 0 || tides > 1_000_000_000L) {
            throw new IllegalArgumentException("Reward Tides must be between 0 and 1,000,000,000");
        }
        skillXp = Map.copyOf(Objects.requireNonNull(skillXp, "skillXp"));
        items = List.copyOf(Objects.requireNonNull(items, "items"));
        skillXp.forEach((skill, amount) -> {
            if (skill == null || !skill.matches("[a-z0-9_.:/-]+") || amount == null || amount <= 0) {
                throw new IllegalArgumentException("Invalid skill XP reward");
            }
        });
        if (tides == 0 && skillXp.isEmpty() && items.isEmpty()) {
            throw new IllegalArgumentException("A reward cannot be empty");
        }
    }

    public static RewardDefinition tidesOnly(long amount) {
        return new RewardDefinition(amount, Map.of(), List.of());
    }
}
