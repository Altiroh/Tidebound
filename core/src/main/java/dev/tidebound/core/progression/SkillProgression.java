package dev.tidebound.core.progression;

import java.util.List;

/**
 * Shared level curve for Tidebound professions. Levels are derived from persistent XP.
 */
public final class SkillProgression {
    public static final List<String> CORE_SKILLS = List.of("fishing", "navigation", "trade", "salvage");
    private static final long[] LEVEL_THRESHOLDS = {
            0, 100, 250, 500, 900, 1_400, 2_000, 2_750, 3_650, 4_700
    };

    private SkillProgression() {
    }

    public static int levelForXp(long xp) {
        if (xp < 0) {
            throw new IllegalArgumentException("Skill XP cannot be negative");
        }
        int level = 1;
        for (int index = 1; index < LEVEL_THRESHOLDS.length; index++) {
            if (xp < LEVEL_THRESHOLDS[index]) {
                break;
            }
            level = index + 1;
        }
        return level;
    }

    public static long thresholdForLevel(int level) {
        if (level < 1 || level > LEVEL_THRESHOLDS.length) {
            throw new IllegalArgumentException("Skill level must be between 1 and " + LEVEL_THRESHOLDS.length);
        }
        return LEVEL_THRESHOLDS[level - 1];
    }

    public static long xpUntilNextLevel(long xp) {
        int level = levelForXp(xp);
        if (level == LEVEL_THRESHOLDS.length) {
            return 0;
        }
        return LEVEL_THRESHOLDS[level] - xp;
    }

    public static int maximumLevel() {
        return LEVEL_THRESHOLDS.length;
    }
}
