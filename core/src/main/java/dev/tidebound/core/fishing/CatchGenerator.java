package dev.tidebound.core.fishing;

import java.util.Objects;

/** Pure deterministic generator fed by a server-side random seed. */
public final class CatchGenerator {
    private CatchGenerator() {
    }

    public static CatchData generate(
            CatchProfile profile,
            String originBiomeId,
            long gameTime,
            long randomSeed,
            int fishingLevel,
            boolean eerieWater
    ) {
        Objects.requireNonNull(profile, "profile");
        if (gameTime < 0) {
            throw new IllegalArgumentException("Game time cannot be negative");
        }
        if (fishingLevel < 1 || fishingLevel > 10) {
            throw new IllegalArgumentException("Fishing level must be between 1 and 10");
        }

        int span = profile.maxWeightGrams() - profile.minWeightGrams() + 1;
        int weight = profile.minWeightGrams() + bounded(mix(randomSeed), span);
        int qualityRoll = bounded(mix(randomSeed ^ 0x5DEECE66DL), 10_000);
        int adjustedQualityRoll = Math.min(9_999, qualityRoll + (fishingLevel - 1) * 8);
        CatchQuality quality = qualityFor(adjustedQualityRoll);
        CatchAnomaly anomaly = anomalyFor(mix(randomSeed ^ 0x6A09E667F3BCC909L), eerieWater);
        return new CatchData(profile.speciesId(), weight, quality, gameTime, originBiomeId, anomaly);
    }

    private static CatchQuality qualityFor(int roll) {
        if (roll >= 9_990) {
            return CatchQuality.LEGENDARY;
        }
        if (roll >= 9_650) {
            return CatchQuality.EXCEPTIONAL;
        }
        if (roll >= 7_200) {
            return CatchQuality.FINE;
        }
        return CatchQuality.COMMON;
    }

    private static CatchAnomaly anomalyFor(long seed, boolean eerieWater) {
        int roll = bounded(seed, 10_000);
        int threshold = eerieWater ? 35 : 8;
        if (roll >= threshold) {
            return CatchAnomaly.NONE;
        }
        return switch (bounded(mix(seed), 3)) {
            case 0 -> CatchAnomaly.ASHEN;
            case 1 -> CatchAnomaly.HOLLOW_EYED;
            default -> CatchAnomaly.INK_VEINED;
        };
    }

    private static int bounded(long value, int bound) {
        return (int) Math.floorMod(value, (long) bound);
    }

    private static long mix(long value) {
        long mixed = value + 0x9E3779B97F4A7C15L;
        mixed = (mixed ^ (mixed >>> 30)) * 0xBF58476D1CE4E5B9L;
        mixed = (mixed ^ (mixed >>> 27)) * 0x94D049BB133111EBL;
        return mixed ^ (mixed >>> 31);
    }
}
