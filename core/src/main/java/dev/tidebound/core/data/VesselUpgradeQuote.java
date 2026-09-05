package dev.tidebound.core.data;

import java.util.Objects;

/** Immutable price and progression requirement for the next vessel tier. */
public record VesselUpgradeQuote(
        VesselUpgrade upgrade,
        int targetTier,
        long tideCost,
        String materialItemId,
        int materialCount,
        String requiredSkill,
        int requiredSkillLevel
) {
    public VesselUpgradeQuote {
        upgrade = Objects.requireNonNull(upgrade, "upgrade");
        int maximum = upgrade == VesselUpgrade.MODULE_SLOT
                ? PlayerVessel.MAX_MODULE_SLOTS
                : PlayerVessel.MAX_TIER;
        if (targetTier < 2 || targetTier > maximum) {
            throw new IllegalArgumentException("Invalid target vessel tier: " + targetTier);
        }
        if (tideCost <= 0 || materialCount <= 0) {
            throw new IllegalArgumentException("Upgrade costs must be positive");
        }
        materialItemId = validId(materialItemId, "material item");
        requiredSkill = validId(requiredSkill, "required skill");
        if (requiredSkillLevel < 1 || requiredSkillLevel > 10) {
            throw new IllegalArgumentException("Required skill level must be between 1 and 10");
        }
    }

    public static VesselUpgradeQuote next(PlayerVessel vessel, VesselUpgrade upgrade) {
        Objects.requireNonNull(vessel, "vessel");
        Objects.requireNonNull(upgrade, "upgrade");
        if (!vessel.unlocked()) {
            throw new IllegalStateException("Vessel is not unlocked");
        }
        int target = currentTier(vessel, upgrade) + 1;
        int maximum = upgrade == VesselUpgrade.MODULE_SLOT
                ? PlayerVessel.MAX_MODULE_SLOTS
                : PlayerVessel.MAX_TIER;
        if (target > maximum) {
            throw new IllegalStateException(upgrade.id() + " is already at maximum");
        }
        return switch (upgrade) {
            case HULL -> switch (target) {
                case 2 -> quote(upgrade, target, 120, "tidebound:hull_plate", 4, "navigation", 2);
                case 3 -> quote(upgrade, target, 300, "tidebound:hull_plate", 8, "navigation", 4);
                case 4 -> quote(upgrade, target, 650, "tidebound:hull_plate", 12, "navigation", 6);
                case 5 -> quote(upgrade, target, 1_200, "tidebound:hull_plate", 16, "navigation", 8);
                default -> throw unsupported(target);
            };
            case MOTOR -> switch (target) {
                case 2 -> quote(upgrade, target, 150, "tidebound:engine_parts", 2, "navigation", 2);
                case 3 -> quote(upgrade, target, 375, "tidebound:engine_parts", 4, "navigation", 4);
                case 4 -> quote(upgrade, target, 800, "tidebound:engine_parts", 6, "navigation", 6);
                case 5 -> quote(upgrade, target, 1_500, "tidebound:engine_parts", 10, "navigation", 9);
                default -> throw unsupported(target);
            };
            case HOLD -> switch (target) {
                case 2 -> quote(upgrade, target, 100, "tidebound:hold_fittings", 2, "trade", 2);
                case 3 -> quote(upgrade, target, 275, "tidebound:hold_fittings", 4, "trade", 4);
                case 4 -> quote(upgrade, target, 600, "tidebound:hold_fittings", 8, "trade", 6);
                case 5 -> quote(upgrade, target, 1_100, "tidebound:hold_fittings", 12, "trade", 8);
                default -> throw unsupported(target);
            };
            case MODULE_SLOT -> switch (target) {
                case 2 -> quote(upgrade, target, 180, "minecraft:copper_ingot", 6, "salvage", 2);
                case 3 -> quote(upgrade, target, 500, "minecraft:iron_ingot", 8, "salvage", 5);
                case 4 -> quote(upgrade, target, 950, "minecraft:prismarine_crystals", 8, "salvage", 8);
                default -> throw unsupported(target);
            };
        };
    }

    private static int currentTier(PlayerVessel vessel, VesselUpgrade upgrade) {
        return switch (upgrade) {
            case HULL -> vessel.hullTier();
            case MOTOR -> vessel.motorTier();
            case HOLD -> vessel.holdTier();
            case MODULE_SLOT -> vessel.moduleSlots();
        };
    }

    private static VesselUpgradeQuote quote(VesselUpgrade upgrade, int target, long tides,
                                             String material, int count, String skill, int level) {
        return new VesselUpgradeQuote(upgrade, target, tides, material, count, skill, level);
    }

    private static IllegalStateException unsupported(int target) {
        return new IllegalStateException("No vessel upgrade quote for tier " + target);
    }

    private static String validId(String value, String label) {
        String id = Objects.requireNonNull(value, label).strip().toLowerCase(java.util.Locale.ROOT);
        if (id.isBlank() || !id.matches("[a-z0-9_.-]+:[a-z0-9_./-]+|[a-z0-9_.-]+")) {
            throw new IllegalArgumentException("Invalid " + label + ": " + value);
        }
        return id;
    }
}
