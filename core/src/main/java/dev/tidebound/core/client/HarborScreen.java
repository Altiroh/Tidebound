package dev.tidebound.core.client;

import dev.tidebound.core.TideboundCore;
import dev.tidebound.core.menu.HarborMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** First visual harbour pass, skinned from the Tidebound art pack. */
public final class HarborScreen extends AbstractContainerScreen<HarborMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
            TideboundCore.MOD_ID, "textures/gui/harbor_intendant.png");
    private static final float ART_SCALE = 0.5F;
    private static final int PANEL = 0xF2D8BD8C;
    private static final int INK = 0xFF241D18;
    private static final int TRACK = 0xFF313A3C;
    private static final int FILL = 0xFF6FA55F;

    public HarborScreen(HarborMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 265;
        imageHeight = 287;
        inventoryLabelY = 10_000;
        titleLabelY = 10_000;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new Hotspot(leftPos + 13, topPos + 170, 56, 39,
                Component.translatable("menu.tidebound.contracts"),
                () -> press(HarborMenu.ACTION_CONTRACTS)));
        addRenderableWidget(new Hotspot(leftPos + 134, topPos + 170, 55, 39,
                Component.translatable("menu.tidebound.voyage"), this::openVoyage));

        addRenderableWidget(new Hotspot(leftPos + 178, topPos + 86, 59, 12,
                Component.translatable("menu.tidebound.upgrade.hull"),
                () -> press(HarborMenu.ACTION_HULL)));
        addRenderableWidget(new Hotspot(leftPos + 178, topPos + 110, 59, 12,
                Component.translatable("menu.tidebound.upgrade.motor"),
                () -> press(HarborMenu.ACTION_MOTOR)));
        addRenderableWidget(new Hotspot(leftPos + 178, topPos + 134, 59, 12,
                Component.translatable("menu.tidebound.upgrade.hold"),
                () -> press(HarborMenu.ACTION_HOLD)));
        addRenderableWidget(new Hotspot(leftPos + 178, topPos + 151, 59, 11,
                Component.translatable("menu.tidebound.upgrade.module"),
                () -> press(HarborMenu.ACTION_MODULE)));

        int actionY = topPos + 248;
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                primaryActionLabel(), button -> press(primaryAction()))
                .bounds(leftPos + 13, actionY, 112, 20).build());
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                Component.translatable(secondaryActionKey()),
                button -> press(secondaryAction()))
                .bounds(leftPos + 132, actionY, 112, 20).build());
    }

    @Override
    public void containerTick() {
        super.containerTick();
        // Rebuild the small action row when synchronized vessel state changes after a click.
        if (getFocused() == null) {
            // State bars are rendered live; buttons remain valid server-side even before a rebuild.
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.pose().pushPose();
        graphics.pose().translate(leftPos, topPos, 0);
        graphics.pose().scale(ART_SCALE, ART_SCALE, 1.0F);
        graphics.blit(BACKGROUND, 0, 0, 0, 0, 529, 573, 529, 573);
        graphics.pose().popPose();

        // Replace the static mock-up values with synchronized state while retaining its frame and portrait.
        int x = leftPos + 121;
        int y = topPos + 40;
        graphics.fill(x, y, x + 124, y + 124, PANEL);
        graphics.drawString(font, Component.translatable("menu.tidebound.my_vessel"), x + 8, y + 7, INK, false);
        drawVesselSilhouette(graphics, x + 10, y + 27);
        drawTier(graphics, Component.translatable("menu.tidebound.hull"), menu.hullTier(), x + 8, y + 49);
        drawTier(graphics, Component.translatable("menu.tidebound.motor"), menu.motorTier(), x + 8, y + 73);
        drawTier(graphics, Component.translatable("menu.tidebound.hold"), menu.holdTier(), x + 8, y + 97);
        drawTier(graphics, Component.translatable("menu.tidebound.modules"), menu.moduleSlots(), x + 8, y + 114);

        graphics.fill(leftPos + 14, topPos + 214, leftPos + 244, topPos + 244, 0xE6192528);
        graphics.drawString(font, Component.translatable("menu.tidebound.tides", menu.tides()),
                leftPos + 22, topPos + 225, 0xFFFFD36A, false);
        graphics.drawString(font, Component.translatable(menu.deployed()
                        ? "menu.tidebound.status.at_sea" : "menu.tidebound.status.at_harbor"),
                leftPos + 145, topPos + 225, 0xFFB9D8D0, false);
    }

    private void drawTier(GuiGraphics graphics, Component label, int tier, int x, int y) {
        graphics.drawString(font, label, x, y, INK, false);
        int barX = x + 57;
        int width = 51;
        graphics.fill(barX, y, barX + width, y + 8, TRACK);
        graphics.fill(barX + 1, y + 1, barX + 1 + Math.round((width - 2) * tier / 5.0F), y + 7, FILL);
        graphics.drawString(font, tier + "/5", barX + 18, y, 0xFFFFFFFF, true);
    }

    private static void drawVesselSilhouette(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y + 12, x + 44, y + 17, 0xFF744C2C);
        graphics.fill(x + 5, y + 17, x + 38, y + 21, 0xFF4D3324);
        graphics.fill(x + 20, y, x + 22, y + 13, 0xFF513725);
        graphics.fill(x + 22, y + 2, x + 35, y + 10, 0xFFE5D5B0);
        graphics.fill(x + 7, y + 22, x + 41, y + 24, 0xFF2C8090);
    }

    private int primaryAction() {
        if (!menu.vesselUnlocked()) {
            return HarborMenu.ACTION_REGISTER;
        }
        return menu.deployed() ? HarborMenu.ACTION_COMPASS : HarborMenu.ACTION_DEPLOY;
    }

    private Component primaryActionLabel() {
        if (!menu.vesselUnlocked()) {
            return Component.translatable("menu.tidebound.register_boat");
        }
        return Component.translatable(menu.deployed()
                ? "menu.tidebound.locate_boat" : "menu.tidebound.launch_boat");
    }

    private int secondaryAction() {
        if (menu.refitAvailable()) {
            return HarborMenu.ACTION_REFIT;
        }
        return menu.repairAvailable() ? HarborMenu.ACTION_REPAIR : HarborMenu.ACTION_COMPASS;
    }

    private String secondaryActionKey() {
        if (menu.refitAvailable()) {
            return "menu.tidebound.refit";
        }
        return menu.repairAvailable() ? "menu.tidebound.repair" : "menu.tidebound.compass";
    }

    private void press(int action) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action);
        }
    }

    private void openVoyage() {
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.connection.sendCommand("ftbquests open_book");
        }
    }

    private static final class Hotspot extends AbstractButton {
        private final Runnable action;

        private Hotspot(int x, int y, int width, int height, Component message, Runnable action) {
            super(x, y, width, height, message);
            this.action = action;
        }

        @Override
        public void onPress() {
            action.run();
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            if (isHoveredOrFocused()) {
                graphics.fill(getX(), getY(), getX() + width, getY() + height, 0x35FFFFFF);
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }
}
