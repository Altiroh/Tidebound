package dev.tidebound.core.data;

/** Usable vanilla chest-boat slots for each hold tier. */
public final class VesselHoldPolicy {
    public static final int VANILLA_CHEST_BOAT_SLOTS = 27;

    private VesselHoldPolicy() {
    }

    public static int usableSlots(int holdTier) {
        if (holdTier < 1 || holdTier > PlayerVessel.MAX_TIER) {
            throw new IllegalArgumentException("Hold tier must be between 1 and " + PlayerVessel.MAX_TIER);
        }
        return switch (holdTier) {
            case 1 -> 9;
            case 2 -> 18;
            default -> VANILLA_CHEST_BOAT_SLOTS;
        };
    }
}
