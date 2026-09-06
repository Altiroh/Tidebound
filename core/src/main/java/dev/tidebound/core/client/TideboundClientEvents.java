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
    private static final int BIOME_FRAME_PADDING = 4;
    private static final int COLOR_DANGEROUS = 0xFFFF5555;
    private static final int COLOR_NORMAL = 0xFF55FFFF;
    private static final int COLOR_FRAME_FILL = 0xA0101018;
    private static final int COLOR_NAME_TEXT = 0xFFFFFFFF;

    private static ResourceKey<Biome> lastBiome;
    private static Component biomeDisplayText;
    private static Component biomeStatusText;
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
                biomeStatusText = Component.translatable(biomeDisplayDangerous
                        ? "hud.tidebound.biome.dangerous"
                        : "hud.tidebound.biome.safe");
                biomeDisplayUntilMillis = System.currentTimeMillis() + BIOME_NAME_DISPLAY_MILLIS;
            }
        }

        if (biomeDisplayText != null && System.currentTimeMillis() < biomeDisplayUntilMillis) {
            GuiGraphics graphics = event.getGuiGraphics();
            int statusColor = biomeDisplayDangerous ? COLOR_DANGEROUS : COLOR_NORMAL;
            int lineHeight = minecraft.font.lineHeight;
            int nameWidth = minecraft.font.width(biomeDisplayText);
            int statusWidth = minecraft.font.width(biomeStatusText);
            int boxWidth = Math.max(nameWidth, statusWidth) + BIOME_FRAME_PADDING * 2;
            int boxHeight = lineHeight * 2 + BIOME_FRAME_PADDING * 3;
            int centerX = graphics.guiWidth() / 2;
            int boxLeft = centerX - boxWidth / 2;
            int boxRight = centerX + boxWidth / 2;
            int boxTop = BIOME_NAME_TOP_MARGIN;
            int boxBottom = boxTop + boxHeight;

            graphics.fill(boxLeft - 1, boxTop - 1, boxRight + 1, boxBottom + 1, statusColor);
            graphics.fill(boxLeft, boxTop, boxRight, boxBottom, COLOR_FRAME_FILL);
            graphics.drawCenteredString(minecraft.font, biomeDisplayText,
                    centerX, boxTop + BIOME_FRAME_PADDING, COLOR_NAME_TEXT);
            graphics.drawCenteredString(minecraft.font, biomeStatusText,
                    centerX, boxTop + BIOME_FRAME_PADDING * 2 + lineHeight, statusColor);
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
