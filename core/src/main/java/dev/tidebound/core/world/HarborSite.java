package dev.tidebound.core.world;

import java.util.Set;
import net.minecraft.core.BlockPos;

/** Immutable location record stored independently from loaded port entities. */
public record HarborSite(
        long siteId,
        String dimensionId,
        BlockPos position,
        PortArchetype archetype,
        Set<PortService> services
) {
    public HarborSite {
        if (dimensionId == null || dimensionId.isBlank() || position == null || archetype == null
                || services == null || services.isEmpty()) {
            throw new IllegalArgumentException("Invalid harbour site");
        }
        services = Set.copyOf(services);
    }

    public boolean hasIntendant() {
        return services.contains(PortService.INTENDANT);
    }
}
