package dev.tidebound.core.data;

public enum VesselUpgrade {
    HULL,
    MOTOR,
    HOLD,
    MODULE_SLOT;

    public String id() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
}
