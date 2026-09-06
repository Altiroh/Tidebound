package dev.tidebound.core.vessel;

/**
 * Fixed, cumulative activation order for the module slots sold by the shipwright
 * ({@code PlayerVessel#moduleSlots}). Buying slot N activates module N; there is no separate
 * equip/select step in v1.
 */
public enum VesselModule {
    SPOTLIGHT(1),
    SONAR(2),
    WINCH(3),
    NET(4);

    private final int requiredSlots;

    VesselModule(int requiredSlots) {
        this.requiredSlots = requiredSlots;
    }

    public boolean active(int moduleSlots) {
        return moduleSlots >= requiredSlots;
    }
}
