package dev.tidebound.core.fishing;

import dev.tidebound.core.content.TideboundContentManager;
import java.util.Map;
import java.util.Optional;

/** Facade over the datapack-backed catch profile catalogue (`data/tidebound/tidebound/catch_profiles`). */
public final class CatchProfiles {
    private CatchProfiles() {
    }

    public static Optional<CatchProfile> find(String speciesId) {
        return TideboundContentManager.catchProfile(speciesId);
    }

    public static Map<String, CatchProfile> all() {
        return TideboundContentManager.catchProfiles();
    }
}
