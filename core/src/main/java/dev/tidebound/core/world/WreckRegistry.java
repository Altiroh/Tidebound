package dev.tidebound.core.world;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** World-wide index of already-materialized shipwrecks, stored on the always-loaded Overworld. */
public final class WreckRegistry extends SavedData {
    private static final String DATA_NAME = "tidebound_wrecks";
    private static final Factory<WreckRegistry> FACTORY =
            new Factory<>(WreckRegistry::new, WreckRegistry::load);

    private final Set<Long> siteIds = new HashSet<>();

    public static WreckRegistry get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public boolean contains(long siteId) {
        return siteIds.contains(siteId);
    }

    public void register(long siteId) {
        if (siteIds.add(siteId)) {
            setDirty();
        }
    }

    private static WreckRegistry load(CompoundTag tag, HolderLookup.Provider registries) {
        WreckRegistry registry = new WreckRegistry();
        ListTag encoded = tag.getList("sites", Tag.TAG_LONG);
        for (Tag value : encoded) {
            if (value instanceof LongTag longTag) {
                registry.siteIds.add(longTag.getAsLong());
            }
        }
        return registry;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag encoded = new ListTag();
        siteIds.forEach(id -> encoded.add(LongTag.valueOf(id)));
        tag.put("sites", encoded);
        return tag;
    }
}
