package dev.tidebound.core.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Compact visual help reached from the player's inventory. */
public final class CaptainsLogScreen extends Screen {
    private final Screen parent;

    public CaptainsLogScreen(Screen parent) {
        super(Component.translatable("screen.tidebound.logbook"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int center = width / 2;
        addRenderableWidget(Button.builder(Component.translatable("screen.tidebound.open_voyage"), button -> {
            if (minecraft != null && minecraft.player != null) {
                minecraft.player.connection.sendCommand("ftbquests open_book");
            }
        }).bounds(center - 102, height / 2 + 58, 98, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(center + 4, height / 2 + 58, 98, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        int left = width / 2 - 130;
        int top = height / 2 - 90;
        graphics.fill(left, top, left + 260, top + 180, 0xF019272B);
        graphics.fill(left + 4, top + 4, left + 256, top + 176, 0xFFE1C99E);
        graphics.drawCenteredString(font, title, width / 2, top + 12, 0xFF26383B);
        graphics.drawCenteredString(font, Component.translatable("screen.tidebound.logbook.subtitle"),
                width / 2, top + 28, 0xFF68513D);

        int routeY = top + 79;
        int startX = left + 28;
        for (int i = 0; i < 6; i++) {
            int x = startX + i * 41;
            if (i < 5) {
                graphics.fill(x + 11, routeY + 5, x + 41, routeY + 7, 0xFF6C8E84);
            }
            drawStepIcon(graphics, x, routeY, i);
            graphics.drawCenteredString(font,
                    Component.translatable("screen.tidebound.step." + i), x + 6, routeY + 25, 0xFF332820);
        }
        graphics.drawCenteredString(font, Component.translatable("screen.tidebound.logbook.hint"),
                width / 2, top + 128, 0xFF4D4035);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private static void drawStepIcon(GuiGraphics graphics, int x, int y, int step) {
        int color = switch (step) {
            case 0 -> 0xFF7A5230;
            case 1 -> 0xFF765033;
            case 2 -> 0xFF385C68;
            case 3 -> 0xFF4D8BA0;
            case 4 -> 0xFFD9A82E;
            default -> 0xFF4A294D;
        };
        graphics.fill(x, y, x + 13, y + 13, 0xFF26383B);
        graphics.fill(x + 2, y + 2, x + 11, y + 11, color);
        if (step == 5) {
            graphics.fill(x + 5, y + 3, x + 8, y + 10, 0xFFB88CBE);
        }
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }
}
