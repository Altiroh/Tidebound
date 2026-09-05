package dev.tidebound.core.fishing;

import java.util.Locale;
import java.util.Objects;

/** Immutable data persisted directly on a vanilla fish ItemStack. */
public record CatchData(
        String speciesId,
        int weightGrams,
        CatchQuality quality,
        long caughtAtGameTime,
        String originBiomeId,
        CatchAnomaly anomaly
) {
    public static final int MAX_WEIGHT_GRAMS = 100_000;

    public CatchData {
        speciesId = validId(speciesId, "speciesId");
        originBiomeId = validId(originBiomeId, "originBiomeId");
        quality = Objects.requireNonNull(quality, "quality");
        anomaly = Objects.requireNonNull(anomaly, "anomaly");
        if (weightGrams < 1 || weightGrams > MAX_WEIGHT_GRAMS) {
            throw new IllegalArgumentException("Catch weight must be between 1 and " + MAX_WEIGHT_GRAMS + " grams");
        }
        if (caughtAtGameTime < 0) {
            throw new IllegalArgumentException("Catch time cannot be negative");
        }
    }

    public long ageTicks(long currentGameTime) {
        return Math.max(0, currentGameTime - caughtAtGameTime);
    }

    public CatchFreshness freshness(long currentGameTime) {
        return CatchFreshness.forAge(ageTicks(currentGameTime));
    }

    public boolean anomalous() {
        return anomaly != CatchAnomaly.NONE;
    }

    private static String validId(String value, String label) {
        String id = Objects.requireNonNull(value, label).strip().toLowerCase(Locale.ROOT);
        if (id.isBlank() || id.length() > 128 || !id.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")) {
            throw new IllegalArgumentException("Invalid " + label + ": " + value);
        }
        return id;
    }
}
