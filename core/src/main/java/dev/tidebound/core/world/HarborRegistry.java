package dev.tidebound.core.world;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** World-wide index of generated Tidebound harbours, stored on the always-loaded Overworld. */
public final class HarborRegistry extends SavedData {
    private static final String DATA_NAME = "tidebound_harbors";
    private static final Factory<HarborRegistry> FACTORY =
            new Factory<>(HarborRegistry::new, HarborRegistry::load);

    private final Map<Long, HarborSite> sites = new LinkedHashMap<>();

    public static HarborRegistry get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public boolean contains(long siteId) {
        return sites.containsKey(siteId);
    }

    public void register(HarborSite site) {
        HarborSite previous = sites.put(site.siteId(), site);
        if (!site.equals(previous)) {
            setDirty();
        }
    }

    public Optional<HarborSite> nearestIntendant(String dimensionId, BlockPos origin) {
        return sites.values().stream()
                .filter(HarborSite::hasIntendant)
                .filter(site -> site.dimensionId().equals(dimensionId))
                .min(Comparator.comparingDouble(site -> site.position().distSqr(origin)));
    }

    public List<HarborSite> sites() {
        return List.copyOf(sites.values());
    }

    private static HarborRegistry load(CompoundTag tag, HolderLookup.Provider registries) {
        HarborRegistry registry = new HarborRegistry();
        ListTag encodedSites = tag.getList("sites", Tag.TAG_COMPOUND);
        for (Tag encoded : encodedSites) {
            if (!(encoded instanceof CompoundTag entry)) {
                continue;
            }
            try {
                PortArchetype archetype = PortArchetype.valueOf(entry.getString("archetype"));
                EnumSet<PortService> services = EnumSet.noneOf(PortService.class);
                ListTag encodedServices = entry.getList("services", Tag.TAG_STRING);
                for (Tag value : encodedServices) {
                    services.add(PortService.valueOf(value.getAsString()));
                }
                HarborSite site = new HarborSite(
                        entry.getLong("site_id"),
                        entry.getString("dimension"),
                        new BlockPos(entry.getInt("x"), entry.getInt("y"), entry.getInt("z")),
                        archetype,
                        services
                );
                registry.sites.put(site.siteId(), site);
            } catch (IllegalArgumentException ignored) {
                // Ignore one malformed legacy entry rather than losing the whole world index.
            }
        }
        return registry;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag encodedSites = new ListTag();
        for (HarborSite site : sites.values()) {
            CompoundTag entry = new CompoundTag();
            entry.putLong("site_id", site.siteId());
            entry.putString("dimension", site.dimensionId());
            entry.putInt("x", site.position().getX());
            entry.putInt("y", site.position().getY());
            entry.putInt("z", site.position().getZ());
            entry.putString("archetype", site.archetype().name());
            ListTag encodedServices = new ListTag();
            site.services().stream().sorted().map(service -> StringTag.valueOf(service.name()))
                    .forEach(encodedServices::add);
            entry.put("services", encodedServices);
            encodedSites.add(entry);
        }
        tag.put("sites", encodedSites);
        return tag;
    }
}
