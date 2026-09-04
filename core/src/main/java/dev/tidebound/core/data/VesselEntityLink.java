package dev.tidebound.core.data;

import java.util.Objects;
import java.util.UUID;

/** Persistent identity placed on the physical boat entity. */
public record VesselEntityLink(String ownerId, String vesselId) {
    public VesselEntityLink {
        ownerId = Objects.requireNonNull(ownerId, "ownerId").strip();
        vesselId = Objects.requireNonNull(vesselId, "vesselId").strip();
        if (ownerId.isBlank() != vesselId.isBlank()) {
            throw new IllegalArgumentException("A vessel link must be completely empty or completely linked");
        }
        if (!ownerId.isBlank()) {
            UUID.fromString(ownerId);
            UUID.fromString(vesselId);
        }
    }

    public static VesselEntityLink unlinked() {
        return new VesselEntityLink("", "");
    }

    public static VesselEntityLink linked(UUID ownerId, String vesselId) {
        return new VesselEntityLink(ownerId.toString(), vesselId);
    }

    public boolean linked() {
        return !ownerId.isBlank();
    }

    public boolean belongsTo(UUID playerId) {
        return linked() && ownerId.equals(playerId.toString());
    }
}
