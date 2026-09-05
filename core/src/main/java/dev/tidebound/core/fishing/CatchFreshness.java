package dev.tidebound.core.fishing;

/** Freshness is derived from world time so no inventory-wide ticking is required. */
public enum CatchFreshness {
    FRESH("fresh", 1_000),
    AGED("aged", 850),
    STALE("stale", 500),
    SPOILED("spoiled", 150);

    public static final long FRESH_TICKS = 24_000L;
    public static final long AGED_TICKS = 72_000L;
    public static final long STALE_TICKS = 144_000L;

    private final String id;
    private final int valuePermille;

    CatchFreshness(String id, int valuePermille) {
        this.id = id;
        this.valuePermille = valuePermille;
    }

    public String id() {
        return id;
    }

    public int valuePermille() {
        return valuePermille;
    }

    public static CatchFreshness forAge(long ageTicks) {
        long age = Math.max(0, ageTicks);
        if (age < FRESH_TICKS) {
            return FRESH;
        }
        if (age < AGED_TICKS) {
            return AGED;
        }
        if (age < STALE_TICKS) {
            return STALE;
        }
        return SPOILED;
    }
}
