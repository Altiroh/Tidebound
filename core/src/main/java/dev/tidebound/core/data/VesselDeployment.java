package dev.tidebound.core.data;

import java.util.Objects;
import java.util.UUID;

/** Last known physical entity, lifecycle state and position for a player's vessel. */
public record VesselDeployment(
        String entityId,
        String dimensionId,
        int blockX,
        int blockY,
        int blockZ,
        int chunkX,
        int chunkZ,
        VesselDeploymentState state
) {
    public VesselDeployment {
        entityId = Objects.requireNonNull(entityId, "entityId").strip();
        dimensionId = Objects.requireNonNull(dimensionId, "dimensionId").strip();
        state = Objects.requireNonNull(state, "state");
        if (state == VesselDeploymentState.LEGACY) {
            state = entityId.isBlank() ? VesselDeploymentState.DOCKED : VesselDeploymentState.DEPLOYED;
        }
        if (!dimensionId.isBlank() && !dimensionId.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")) {
            throw new IllegalArgumentException("Invalid vessel dimension: " + dimensionId);
        }

        switch (state) {
            case DOCKED -> {
                if (!entityId.isBlank() || !dimensionId.isBlank()) {
                    throw new IllegalArgumentException("A docked deployment cannot retain a physical position");
                }
            }
            case DEPLOYED -> {
                if (entityId.isBlank() || dimensionId.isBlank()) {
                    throw new IllegalArgumentException("A deployed vessel needs an entity and dimension");
                }
                UUID.fromString(entityId);
            }
            case MISSING, DESTROYED -> {
                if (!entityId.isBlank() || dimensionId.isBlank()) {
                    throw new IllegalArgumentException("A last-known position needs a dimension but no entity");
                }
            }
            case LEGACY -> throw new IllegalStateException("Legacy deployment state was not normalized");
        }
    }

    public static VesselDeployment docked() {
        return new VesselDeployment("", "", 0, 0, 0, 0, 0, VesselDeploymentState.DOCKED);
    }

    public static VesselDeployment active(
            UUID entityId,
            String dimensionId,
            int blockX,
            int blockY,
            int blockZ,
            int chunkX,
            int chunkZ
    ) {
        return new VesselDeployment(entityId.toString(), dimensionId, blockX, blockY, blockZ,
                chunkX, chunkZ, VesselDeploymentState.DEPLOYED);
    }

    /** Backward-compatible construction for the pre-005 chunk-only model. */
    public static VesselDeployment active(UUID entityId, String dimensionId, int chunkX, int chunkZ) {
        return active(entityId, dimensionId, chunkX * 16 + 8, 64, chunkZ * 16 + 8, chunkX, chunkZ);
    }

    public boolean active() {
        return state == VesselDeploymentState.DEPLOYED;
    }

    public boolean hasKnownPosition() {
        return !dimensionId.isBlank() && state != VesselDeploymentState.DOCKED;
    }

    public VesselDeployment markMissing() {
        if (!hasKnownPosition()) {
            return this;
        }
        return new VesselDeployment("", dimensionId, blockX, blockY, blockZ,
                chunkX, chunkZ, VesselDeploymentState.MISSING);
    }

    public VesselDeployment markDestroyed() {
        if (!hasKnownPosition()) {
            return this;
        }
        return new VesselDeployment("", dimensionId, blockX, blockY, blockZ,
                chunkX, chunkZ, VesselDeploymentState.DESTROYED);
    }
}
