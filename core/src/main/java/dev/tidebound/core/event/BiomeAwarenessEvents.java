package dev.tidebound.core.event;

import dev.tidebound.core.TideboundCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.registries.Registries;

/**
 * Holds the {@code #tidebound:dangerous} biome tag, consulted by {@link HullIntegrityEvents}
 * (server-side hull damage) and {@code dev.tidebound.core.client.BiomeHudEvents} (client-side HUD
 * announcement — biome tags sync to the client, so that display needs no networking of its own).
 */
public final class BiomeAwarenessEvents {
    public static final TagKey<Biome> DANGEROUS = TagKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(TideboundCore.MOD_ID, "dangerous"));

    private BiomeAwarenessEvents() {
    }
}
