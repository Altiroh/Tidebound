package dev.tidebound.core.data;

import java.util.Locale;

/** Persistent lifecycle state of the physical player vessel. */
public enum VesselDeploymentState {
    DOCKED("docked"),
    DEPLOYED("deployed"),
    MISSING("missing"),
    DESTROYED("destroyed"),
    LEGACY("legacy");

    private final String id;

    VesselDeploymentState(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static VesselDeploymentState fromId(String value) {
        String normalized = value == null ? "legacy" : value.strip().toLowerCase(Locale.ROOT);
        for (VesselDeploymentState state : values()) {
            if (state.id.equals(normalized)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown vessel deployment state: " + value);
    }
}
