package dev.tidebound.core.world;

/** Stable one-in-three starter-port roll. TB-PORT-001 will consume this decision. */
public final class StarterPortPlan {
    private StarterPortPlan() {
    }

    public static boolean shouldGenerate(long seed) {
        long mixed = seed ^ 0x9E3779B97F4A7C15L;
        mixed ^= mixed >>> 30;
        mixed *= 0xBF58476D1CE4E5B9L;
        mixed ^= mixed >>> 27;
        mixed *= 0x94D049BB133111EBL;
        mixed ^= mixed >>> 31;
        return Math.floorMod(mixed, 3L) == 0L;
    }
}
