package dev.tidebound.core.vessel;

import dev.tidebound.core.data.PlayerVessel;

/** Compact visual state copied from PlayerVessel to the physical entity. */
public record VesselVisualProfile(int hullTier, int motorTier, int holdTier, int moduleSlots) {
    public VesselVisualProfile {
        requireRange("hullTier", hullTier, 1, PlayerVessel.MAX_TIER);
        requireRange("motorTier", motorTier, 1, PlayerVessel.MAX_TIER);
        requireRange("holdTier", holdTier, 1, PlayerVessel.MAX_TIER);
        requireRange("moduleSlots", moduleSlots, 1, PlayerVessel.MAX_MODULE_SLOTS);
    }

    public static VesselVisualProfile from(PlayerVessel vessel) {
        if (!vessel.unlocked()) {
            throw new IllegalArgumentException("A locked vessel has no visual profile");
        }
        return new VesselVisualProfile(
                vessel.hullTier(), vessel.motorTier(), vessel.holdTier(), vessel.moduleSlots());
    }

    public boolean reinforcedHull() {
        return hullTier >= 2;
    }

    public boolean enclosedHold() {
        return holdTier >= 2;
    }

    public boolean poweredEngine() {
        return motorTier >= 2;
    }

    private static void requireRange(String field, int value, int minimum, int maximum) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(field + " must be between " + minimum + " and " + maximum);
        }
    }
}
