package dev.tidebound.core.world;

/** Aggregated measurements used to validate a generated Tidebound spawn. */
public record ArchipelagoSurvey(
        int totalSamples,
        int landSamples,
        int waterSamples,
        int shoreSamples,
        int logSamples) {
    public ArchipelagoSurvey {
        if (totalSamples <= 0 || landSamples < 0 || waterSamples < 0
                || shoreSamples < 0 || logSamples < 0
                || landSamples + waterSamples > totalSamples) {
            throw new IllegalArgumentException("Invalid archipelago survey");
        }
    }

    public double landRatio() {
        return (double) landSamples / totalSamples;
    }

    public double waterRatio() {
        return (double) waterSamples / totalSamples;
    }

    public boolean playable() {
        int minimumLand = Math.max(8, (int) Math.ceil(totalSamples * 0.04));
        return landSamples >= minimumLand
                && waterRatio() >= 0.35
                && shoreSamples >= 2
                && logSamples > 0;
    }

    public boolean continentLike() {
        return landRatio() > 0.55;
    }
}
