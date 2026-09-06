package dev.tidebound.core.client;

import dev.tidebound.core.TideboundCore;
import dev.tidebound.core.event.BiomeAwarenessEvents;
import dev.tidebound.core.registry.TideboundAttachments;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = TideboundCore.MOD_ID, value = Dist.CLIENT)
public final class TideboundClientEvents {
    private static final long BIOME_NAME_DISPLAY_MILLIS = 3000L;
    private static final int BIOME_NAME_TOP_MARGIN = 10;
    private static final int COLOR_DANGEROUS = 0xFFFF5555;
    private static final int COLOR_NORMAL = 0xFF55FFFF;

    private static ResourceKey<Biome> lastBiome;
    private static Component biomeDisplayText;
    private static boolean biomeDisplayDangerous;
    private static long biomeDisplayUntilMillis;

    private TideboundClientEvents() {
    }

    /**
     * Purely client-side: biome data (including tags like #tidebound:dangerous) is already synced
     * to the client, so there is no need for a server round-trip just to show a name on screen.
     */
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || minecraft.screen != null) {
            return;
        }

        Holder<Biome> biome = minecraft.level.getBiome(minecraft.player.blockPosition());
        Optional<ResourceKey<Biome>> key = biome.unwrapKey();
        if (key.isPresent() && !key.get().equals(lastBiome)) {
            boolean firstReading = lastBiome == null;
            lastBiome = key.get();
            if (!firstReading) {
                biomeDisplayText = Component.translatable(Util.makeDescriptionId("biome", key.get().location()));
                biomeDisplayDangerous = biome.is(BiomeAwarenessEvents.DANGEROUS);
                biomeDisplayUntilMillis = System.currentTimeMillis() + BIOME_NAME_DISPLAY_MILLIS;
            }
        }

        if (biomeDisplayText != null && System.currentTimeMillis() < biomeDisplayUntilMillis) {
            GuiGraphics graphics = event.getGuiGraphics();
            int color = biomeDisplayDangerous ? COLOR_DANGEROUS : COLOR_NORMAL;
            graphics.drawCenteredString(minecraft.font, biomeDisplayText,
                    graphics.guiWidth() / 2, BIOME_NAME_TOP_MARGIN, color);
        }
    }

    @SubscribeEvent
    public static void addLogbookButton(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen inventory)) {
            return;
        }
        long balance = Minecraft.getInstance().player == null
                ? 0L
                : Minecraft.getInstance().player.getData(TideboundAttachments.TIDE_WALLET).balance();
        int width = 46;
        int x = inventory.width / 2 + 68;
        int y = inventory.height / 2 - 82;
        Button button = Button.builder(Component.translatable("menu.tidebound.tides", balance), ignored ->
                        Minecraft.getInstance().setScreen(new CaptainsLogScreen(inventory)))
                .bounds(x, y, width, 18)
                .tooltip(Tooltip.create(Component.translatable("screen.tidebound.logbook.tooltip")))
                .build();
        event.addListener(button);
    }
}
