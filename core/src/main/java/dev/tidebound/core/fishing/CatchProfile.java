package dev.tidebound.core.fishing;

import java.util.Locale;
import java.util.Objects;

/** Server-side balance profile for one fish species. */
public record CatchProfile(
        String speciesId,
        int minWeightGrams,
        int maxWeightGrams,
        int referenceWeightGrams,
        int baseValueTides
) {
    public CatchProfile {
        speciesId = validId(speciesId);
        if (minWeightGrams < 1 || maxWeightGrams < minWeightGrams
                || maxWeightGrams > CatchData.MAX_WEIGHT_GRAMS) {
            throw new IllegalArgumentException("Invalid weight range for " + speciesId);
        }
        if (referenceWeightGrams < minWeightGrams || referenceWeightGrams > maxWeightGrams) {
            throw new IllegalArgumentException("Reference weight must be inside the species range");
        }
        if (baseValueTides < 1 || baseValueTides > 1_000_000) {
            throw new IllegalArgumentException("Invalid base value for " + speciesId);
        }
    }

    private static String validId(String value) {
        String id = Objects.requireNonNull(value, "speciesId").strip().toLowerCase(Locale.ROOT);
        if (!id.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")) {
            throw new IllegalArgumentException("Invalid species id: " + value);
        }
        return id;
    }
}
