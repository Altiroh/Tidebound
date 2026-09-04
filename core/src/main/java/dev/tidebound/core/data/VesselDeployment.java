package dev.tidebound.core.data;

import java.util.Objects;
import java.util.UUID;

/** Last known physical entity and chunk for a player's vessel. */
public record VesselDeployment(String entityId, String dimensionId, int chunkX, int chunkZ) {
    public VesselDeployment {
        entityId = Objects.requireNonNull(entityId, "entityId").strip();
        dimensionId = Objects.requireNonNull(dimensionId, "dimensionId").strip();
        if (entityId.isBlank() != dimensionId.isBlank()) {
            throw new IllegalArgumentException("A deployment must be completely empty or completely active");
        }
        if (!entityId.isBlank()) {
            UUID.fromString(entityId);
            if (!dimensionId.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")) {
                throw new IllegalArgumentException("Invalid vessel dimension: " + dimensionId);
            }
        }
    }

    public static VesselDeployment docked() {
        return new VesselDeployment("", "", 0, 0);
    }

    public static VesselDeployment active(UUID entityId, String dimensionId, int chunkX, int chunkZ) {
        return new VesselDeployment(entityId.toString(), dimensionId, chunkX, chunkZ);
    }

    public boolean active() {
        return !entityId.isBlank();
    }
}
