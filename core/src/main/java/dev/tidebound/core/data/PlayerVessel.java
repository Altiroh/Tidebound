package dev.tidebound.core.data;

import java.util.Objects;
import java.util.UUID;

/**
 * Persistent, player-owned vessel identity and its main progression axes.
 */
public record PlayerVessel(
        String vesselId,
        String name,
        boolean unlocked,
        int hullTier,
        int motorTier,
        int holdTier,
        int moduleSlots
) {
    public static final int MAX_TIER = 5;
    public static final int MAX_MODULE_SLOTS = 4;
    public static final int MAX_NAME_LENGTH = 32;

    public PlayerVessel {
        vesselId = Objects.requireNonNull(vesselId, "vesselId").strip();
        name = normalizeName(name);
        validateRange("hullTier", hullTier, 0, MAX_TIER);
        validateRange("motorTier", motorTier, 0, MAX_TIER);
        validateRange("holdTier", holdTier, 0, MAX_TIER);
        validateRange("moduleSlots", moduleSlots, 0, MAX_MODULE_SLOTS);

        if (unlocked) {
            if (vesselId.isBlank()) {
                throw new IllegalArgumentException("An unlocked vessel needs an id");
            }
            UUID.fromString(vesselId);
            if (hullTier == 0 || motorTier == 0 || holdTier == 0 || moduleSlots == 0) {
                throw new IllegalArgumentException("An unlocked vessel starts at tier 1");
            }
        } else if (!vesselId.isBlank() || hullTier != 0 || motorTier != 0 || holdTier != 0 || moduleSlots != 0) {
            throw new IllegalArgumentException("A locked vessel cannot contain progression");
        }
    }

    public static PlayerVessel locked() {
        return new PlayerVessel("", "Barque sans nom", false, 0, 0, 0, 0);
    }

    public static PlayerVessel unlock(String name, UUID vesselId) {
        return new PlayerVessel(vesselId.toString(), name, true, 1, 1, 1, 1);
    }

    public PlayerVessel rename(String newName) {
        requireUnlocked();
        return new PlayerVessel(vesselId, newName, true, hullTier, motorTier, holdTier, moduleSlots);
    }

    public PlayerVessel upgradeHull() {
        requireUnlocked();
        return new PlayerVessel(vesselId, name, true, increment("hull", hullTier, MAX_TIER), motorTier, holdTier, moduleSlots);
    }

    public PlayerVessel upgradeMotor() {
        requireUnlocked();
        return new PlayerVessel(vesselId, name, true, hullTier, increment("motor", motorTier, MAX_TIER), holdTier, moduleSlots);
    }

    public PlayerVessel upgradeHold() {
        requireUnlocked();
        return new PlayerVessel(vesselId, name, true, hullTier, motorTier, increment("hold", holdTier, MAX_TIER), moduleSlots);
    }

    public PlayerVessel addModuleSlot() {
        requireUnlocked();
        return new PlayerVessel(vesselId, name, true, hullTier, motorTier, holdTier,
                increment("module slots", moduleSlots, MAX_MODULE_SLOTS));
    }

    private void requireUnlocked() {
        if (!unlocked) {
            throw new IllegalStateException("Vessel is not unlocked");
        }
    }

    private static int increment(String field, int value, int maximum) {
        if (value >= maximum) {
            throw new IllegalStateException(field + " is already at maximum");
        }
        return value + 1;
    }

    private static String normalizeName(String value) {
        String normalized = Objects.requireNonNull(value, "name").strip();
        if (normalized.isBlank() || normalized.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Vessel name must contain 1 to " + MAX_NAME_LENGTH + " characters");
        }
        return normalized;
    }

    private static void validateRange(String field, int value, int minimum, int maximum) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(field + " must be between " + minimum + " and " + maximum);
        }
    }
}
