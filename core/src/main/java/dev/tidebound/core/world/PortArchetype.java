package dev.tidebound.core.world;

import java.util.EnumSet;
import java.util.Set;

/** Small, visually readable port families; none contains every service. */
public enum PortArchetype {
    FISHING_HAMLET(34, PortService.FISHMONGER, PortService.CONTRACT_BOARD),
    SHIPYARD_QUAY(20, PortService.SHIPWRIGHT, PortService.STORAGE),
    LIGHTHOUSE_OUTPOST(16, PortService.LIGHTHOUSE_KEEPER),
    MARKET_HARBOR(18, PortService.INTENDANT, PortService.FISHMONGER, PortService.CONTRACT_BOARD),
    FIELD_STATION(12, PortService.NATURALIST, PortService.STORAGE);

    private final int weight;
    private final Set<PortService> baseServices;

    PortArchetype(int weight, PortService first, PortService... rest) {
        this.weight = weight;
        EnumSet<PortService> services = EnumSet.of(first, rest);
        this.baseServices = Set.copyOf(services);
    }

    public int weight() {
        return weight;
    }

    public Set<PortService> baseServices() {
        return baseServices;
    }
}
