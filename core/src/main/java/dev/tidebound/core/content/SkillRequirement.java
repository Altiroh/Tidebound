package dev.tidebound.core.content;

import dev.tidebound.core.progression.SkillProgression;
import java.util.Locale;
import java.util.Objects;

public record SkillRequirement(String skillId, int level) {
    public SkillRequirement {
        skillId = Objects.requireNonNull(skillId, "skillId").strip().toLowerCase(Locale.ROOT);
        if (skillId.isBlank() || !skillId.matches("[a-z0-9_.:/-]+")) {
            throw new IllegalArgumentException("Invalid required skill id: " + skillId);
        }
        if (level < 1 || level > SkillProgression.maximumLevel()) {
            throw new IllegalArgumentException("Invalid required skill level: " + level);
        }
    }
}
