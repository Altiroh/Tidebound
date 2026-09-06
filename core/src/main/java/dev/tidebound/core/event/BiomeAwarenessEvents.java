package dev.tidebound.core.event;

import dev.tidebound.core.TideboundCore;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Announces the biome a player just entered, flagging it if tagged as dangerous. */
public final class BiomeAwarenessEvents {
    public static final TagKey<Biome> DANGEROUS = TagKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(TideboundCore.MOD_ID, "dangerous"));

    private static final Map<UUID, ResourceKey<Biome>> LAST_BIOME = new ConcurrentHashMap<>();

    private BiomeAwarenessEvents() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(BiomeAwarenessEvents::onPlayerTick);
        gameBus.addListener(BiomeAwarenessEvents::onPlayerLoggedOut);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ServerLevel level = player.serverLevel();
        Holder<Biome> biome = level.getBiome(player.blockPosition());
        Optional<ResourceKey<Biome>> key = biome.unwrapKey();
        if (key.isEmpty()) {
            return;
        }

        ResourceKey<Biome> previous = LAST_BIOME.put(player.getUUID(), key.get());
        if (previous == null || previous.equals(key.get())) {
            return;
        }

        Component name = Component.translatable(Util.makeDescriptionId("biome", key.get().location()));
        ChatFormatting color = biome.is(DANGEROUS) ? ChatFormatting.RED : ChatFormatting.AQUA;
        player.displayClientMessage(name.copy().withStyle(color), true);
    }

    private static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_BIOME.remove(event.getEntity().getUUID());
    }
}
