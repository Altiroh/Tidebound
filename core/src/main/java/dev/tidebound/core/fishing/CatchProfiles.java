package dev.tidebound.core.fishing;

import java.util.Map;
import java.util.Optional;

/** Initial vanilla profiles. A datapack-backed catalogue can replace this facade later. */
public final class CatchProfiles {
    private static final Map<String, CatchProfile> PROFILES = Map.of(
            "minecraft:cod", new CatchProfile("minecraft:cod", 500, 5_000, 1_800, 12),
            "minecraft:salmon", new CatchProfile("minecraft:salmon", 1_200, 10_000, 4_500, 20),
            "minecraft:tropical_fish", new CatchProfile("minecraft:tropical_fish", 80, 700, 250, 24),
            "minecraft:pufferfish", new CatchProfile("minecraft:pufferfish", 250, 1_800, 700, 18)
    );

    private CatchProfiles() {
    }

    public static Optional<CatchProfile> find(String speciesId) {
        return Optional.ofNullable(PROFILES.get(speciesId));
    }

    public static Map<String, CatchProfile> all() {
        return PROFILES;
    }
}
