package dev.tidebound.core.fishing;

import java.util.Objects;

/** Pure server valuation shared by tooltips, commands and the future fishmonger. */
public final class CatchValuation {
    public static final long MAX_VALUE_TIDES = 1_000_000L;

    private CatchValuation() {
    }

    public static long value(CatchProfile profile, CatchData data, long currentGameTime) {
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(data, "data");
        if (!profile.speciesId().equals(data.speciesId())) {
            throw new IllegalArgumentException("Catch profile does not match species " + data.speciesId());
        }

        long value = Math.max(1L, divideRounded(
                (long) profile.baseValueTides() * data.weightGrams(),
                profile.referenceWeightGrams()));
        value = multiplyPermille(value, data.quality().valuePermille());
        value = multiplyPermille(value, data.anomaly().valuePermille());
        value = multiplyPermille(value, data.freshness(currentGameTime).valuePermille());
        return Math.max(1L, Math.min(MAX_VALUE_TIDES, value));
    }

    private static long multiplyPermille(long value, int multiplier) {
        return divideRounded(value * multiplier, 1_000L);
    }

    private static long divideRounded(long numerator, long denominator) {
        return (numerator + denominator / 2L) / denominator;
    }
}
