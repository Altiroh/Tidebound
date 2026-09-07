package dev.tidebound.core.world;

/** Deterministic identity for one candidate shipwreck site, independent from port siting. */
public record WreckPlan(long siteId) {
    public static WreckPlan at(long worldSeed, int regionX, int regionZ) {
        long siteId = mix(worldSeed ^ (long) regionX * 0xC2B2AE3D27D4EB4FL
                ^ (long) regionZ * 0x165667B19E3779F9L ^ 0x53A6A2F1B4C8D3E7L);
        return new WreckPlan(siteId);
    }

    private static long mix(long value) {
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        return value ^ value >>> 31;
    }
}
