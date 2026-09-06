package dev.tidebound.core.client;

import dev.tidebound.core.TideboundCore;
import dev.tidebound.core.event.BiomeAwarenessEvents;
import dev.tidebound.core.registry.TideboundAttachments;
import java.util.Optional;
import net.minecraft.ChatFormatting;
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
    private static final long BIOME_NAME_FADE_MILLIS = 500L;
    private static final int BIOME_NAME_TOP_MARGIN = 10;
    private static final int BIOME_FRAME_PADDING = 5;
    private static final int COLOR_DANGEROUS = 0xFFFF5D5D;
    private static final int COLOR_NORMAL = 0xFF6BE7FF;
    private static final int COLOR_FRAME_FILL_TOP = 0xC8101018;
    private static final int COLOR_FRAME_FILL_BOTTOM = 0x90101018;
    private static final int COLOR_FRAME_SHADOW = 0x50000000;
    private static final int COLOR_NAME_TEXT = 0xFFF4F4F4;

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

        long now = System.currentTimeMillis();
        if (biomeDisplayText != null && now < biomeDisplayUntilMillis) {
            GuiGraphics graphics = event.getGuiGraphics();
            long remaining = biomeDisplayUntilMillis - now;
            float fade = remaining < BIOME_NAME_FADE_MILLIS ? remaining / (float) BIOME_NAME_FADE_MILLIS : 1.0F;

            int statusColor = withAlphaFactor(biomeDisplayDangerous ? COLOR_DANGEROUS : COLOR_NORMAL, fade);
            Component name = biomeDisplayText.copy().withStyle(ChatFormatting.BOLD);
            Component status = biomeStatusText.copy().withStyle(ChatFormatting.ITALIC);

            int lineHeight = minecraft.font.lineHeight;
            int nameWidth = minecraft.font.width(name);
            int statusWidth = minecraft.font.width(status);
            int boxWidth = Math.max(nameWidth, statusWidth) + BIOME_FRAME_PADDING * 2;
            int boxHeight = lineHeight * 2 + BIOME_FRAME_PADDING * 3;
            int centerX = graphics.guiWidth() / 2;
            int boxLeft = centerX - boxWidth / 2;
            int boxRight = centerX + boxWidth / 2;
            int boxTop = BIOME_NAME_TOP_MARGIN;
            int boxBottom = boxTop + boxHeight;

            graphics.fill(boxLeft - 2, boxTop - 2, boxRight + 2, boxBottom + 2, withAlphaFactor(COLOR_FRAME_SHADOW, fade));
            graphics.fill(boxLeft - 1, boxTop - 1, boxRight + 1, boxBottom + 1, statusColor);
            graphics.fillGradient(boxLeft, boxTop, boxRight, boxBottom,
                    withAlphaFactor(COLOR_FRAME_FILL_TOP, fade), withAlphaFactor(COLOR_FRAME_FILL_BOTTOM, fade));

            int dividerY = boxTop + BIOME_FRAME_PADDING + lineHeight + BIOME_FRAME_PADDING / 2;
            graphics.fill(boxLeft + 3, dividerY, boxRight - 3, dividerY + 1, withAlphaFactor(statusColor, 0.4F));

            graphics.drawCenteredString(minecraft.font, name,
                    centerX, boxTop + BIOME_FRAME_PADDING, withAlphaFactor(COLOR_NAME_TEXT, fade));
            graphics.drawCenteredString(minecraft.font, status,
                    centerX, boxTop + BIOME_FRAME_PADDING * 2 + lineHeight, statusColor);
        }
    }

    private static int withAlphaFactor(int argb, float factor) {
        int alpha = (argb >>> 24) & 0xFF;
        int scaled = Math.max(0, Math.min(255, Math.round(alpha * factor)));
        return (scaled << 24) | (argb & 0x00FFFFFF);
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
