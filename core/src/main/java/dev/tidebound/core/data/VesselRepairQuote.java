package dev.tidebound.core.data;

/** Price for restoring a damaged physical vessel at a harbour. */
public record VesselRepairQuote(float damage, long tideCost, String materialItemId, int materialCount) {
    public VesselRepairQuote {
        if (!Float.isFinite(damage) || damage <= 0) {
            throw new IllegalArgumentException("Repair damage must be positive");
        }
        if (tideCost <= 0 || materialCount <= 0) {
            throw new IllegalArgumentException("Repair costs must be positive");
        }
        if (materialItemId == null || materialItemId.isBlank()) {
            throw new IllegalArgumentException("Repair material is required");
        }
    }

    public static VesselRepairQuote forDamage(float damage) {
        if (!Float.isFinite(damage) || damage <= 0) {
            throw new IllegalArgumentException("Le navire n'a pas besoin de réparation");
        }
        int workUnits = Math.max(1, (int) Math.ceil(damage / 5.0F));
        int kits = Math.max(1, (int) Math.ceil(damage / 10.0F));
        return new VesselRepairQuote(damage, workUnits * 10L, "tidebound:repair_kit", kits);
    }
}
