package dev.tidebound.core.world;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/** Deterministic port identity and service roster for one candidate island. */
public record PortPlan(long siteId, PortArchetype archetype, Set<PortService> services) {
    public PortPlan {
        if (archetype == null || services == null || services.isEmpty()) {
            throw new IllegalArgumentException("A port needs an archetype and at least one service");
        }
        services = Set.copyOf(services);
        if (!services.containsAll(archetype.baseServices())) {
            throw new IllegalArgumentException("A port is missing an archetype service");
        }
        long npcCount = services.stream().filter(PortPlan::isNpc).count();
        if (npcCount == 0 || npcCount >= 5) {
            throw new IllegalArgumentException("A port must expose between one and four NPC roles");
        }
    }

    public static Optional<PortPlan> starter(long worldSeed) {
        return StarterPortPlan.shouldGenerate(worldSeed)
                ? Optional.of(at(worldSeed, 0, 0))
                : Optional.empty();
    }

    public static PortPlan at(long worldSeed, int regionX, int regionZ) {
        long siteId = mix(worldSeed ^ (long) regionX * 0x632BE59BD9B4E019L
                ^ (long) regionZ * 0x9E3779B97F4A7C15L);
        PortArchetype archetype = selectArchetype(siteId);
        EnumSet<PortService> services = EnumSet.copyOf(archetype.baseServices());

        long choices = mix(siteId + 0xD1B54A32D192ED03L);
        if ((choices & 3L) == 0 && archetype != PortArchetype.MARKET_HARBOR) {
            services.add(PortService.CONTRACT_BOARD);
        }
        if (((choices >>> 3) & 3L) == 0) {
            services.add(PortService.STORAGE);
        }
        if (((choices >>> 6) & 7L) == 0
                && (archetype == PortArchetype.SHIPYARD_QUAY
                || archetype == PortArchetype.LIGHTHOUSE_OUTPOST
                || archetype == PortArchetype.MARKET_HARBOR)) {
            services.add(PortService.CREATE_MECHANISM);
        }
        if (((choices >>> 10) & 7L) == 0 && npcCount(services) < 4) {
            services.add(secondaryNpc(archetype));
        }
        return new PortPlan(siteId, archetype, services);
    }

    private static PortArchetype selectArchetype(long value) {
        int roll = (int) Math.floorMod(value, 100L);
        int threshold = 0;
        for (PortArchetype archetype : PortArchetype.values()) {
            threshold += archetype.weight();
            if (roll < threshold) {
                return archetype;
            }
        }
        throw new IllegalStateException("Port archetype weights must total 100");
    }

    private static PortService secondaryNpc(PortArchetype archetype) {
        return switch (archetype) {
            case FISHING_HAMLET -> PortService.LIGHTHOUSE_KEEPER;
            case SHIPYARD_QUAY -> PortService.INTENDANT;
            case LIGHTHOUSE_OUTPOST -> PortService.NATURALIST;
            case MARKET_HARBOR -> PortService.SHIPWRIGHT;
            case FIELD_STATION -> PortService.FISHMONGER;
        };
    }

    private static long npcCount(Set<PortService> services) {
        return services.stream().filter(PortPlan::isNpc).count();
    }

    private static boolean isNpc(PortService service) {
        return switch (service) {
            case INTENDANT, SHIPWRIGHT, FISHMONGER, NATURALIST, LIGHTHOUSE_KEEPER -> true;
            case CONTRACT_BOARD, STORAGE, CREATE_MECHANISM -> false;
        };
    }

    private static long mix(long value) {
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        return value ^ value >>> 31;
    }
}
