package dev.tidebound.core.client;

import dev.tidebound.core.TideboundCore;
import dev.tidebound.core.menu.HarborMenu;
import dev.tidebound.core.npc.PortNpcRole;
import dev.tidebound.core.vessel.VesselModule;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Harbour screen, skinned from the Tidebound art pack. Rendered at 0.375x instead of the original
 * 0.5x (TB-QA-001 feedback: too large compared to a classic villager trade screen) — every offset
 * below is the original value scaled by the same 0.75 factor, to keep the relative layout intact.
 */
public final class HarborScreen extends AbstractContainerScreen<HarborMenu> {
    private static final float ART_SCALE = 0.375F;
    private static final int PANEL = 0xF2D8BD8C;
    private static final int INK = 0xFF241D18;
    private static final int TRACK = 0xFF313A3C;
    private static final int FILL = 0xFF6FA55F;
    private PortNpcRole initializedRole;

    public HarborScreen(HarborMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 198;
        imageHeight = 215;
        inventoryLabelY = 10_000;
        titleLabelY = 10_000;
    }

    @Override
    protected void init() {
        super.init();
        initializedRole = menu.role();
        int artX = artX(menu.role());
        if (menu.role() == PortNpcRole.INTENDANT) {
            initIntendant(artX);
        } else if (menu.role() == PortNpcRole.SHIPWRIGHT) {
            initShipwright(artX);
        } else if (menu.role() == PortNpcRole.FISHMONGER) {
            initFishmonger(artX);
        }
    }

    private void initFishmonger(int artX) {
        addRenderableWidget(new Hotspot(artX + 13, topPos + 128, 59, 29,
                Component.translatable("menu.tidebound.sell_all"),
                () -> press(HarborMenu.ACTION_SELL_ALL)));
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                Component.translatable("menu.tidebound.sell_all"), button -> press(HarborMenu.ACTION_SELL_ALL))
                .bounds(artX + 149, topPos + 186, 37, 15).build());
    }

    private void initIntendant(int artX) {
        addRenderableWidget(new Hotspot(artX + 10, topPos + 128, 42, 29,
                Component.translatable("menu.tidebound.contracts"),
                () -> press(HarborMenu.ACTION_CONTRACTS)));
        addRenderableWidget(new Hotspot(artX + 101, topPos + 128, 41, 29,
                Component.translatable("menu.tidebound.voyage"), this::openVoyage));
        int actionY = topPos + 186;
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                primaryActionLabel(), button -> press(primaryAction()))
                .bounds(artX + 10, actionY, 84, 15).build());
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                Component.translatable("menu.tidebound.compass"),
                button -> press(HarborMenu.ACTION_COMPASS))
                .bounds(artX + 99, actionY, 84, 15).build());
    }

    private void initShipwright(int artX) {
        boolean enabled = menu.tideboundVessel();
        Hotspot motor = addRenderableWidget(new Hotspot(artX + 79, topPos + 34, 29, 34,
                Component.translatable("menu.tidebound.upgrade.motor"), () -> press(HarborMenu.ACTION_MOTOR)));
        Hotspot hull = addRenderableWidget(new Hotspot(artX + 77, topPos + 68, 30, 32,
                Component.translatable("menu.tidebound.upgrade.hull"), () -> press(HarborMenu.ACTION_HULL)));
        Hotspot hold = addRenderableWidget(new Hotspot(artX + 147, topPos + 68, 30, 32,
                Component.translatable("menu.tidebound.upgrade.hold"), () -> press(HarborMenu.ACTION_HOLD)));
        Hotspot module = addRenderableWidget(new Hotspot(artX + 79, topPos + 102, 99, 23,
                Component.translatable("menu.tidebound.upgrade.module"), () -> press(HarborMenu.ACTION_MODULE)));
        motor.active = enabled;
        hull.active = enabled;
        hold.active = enabled;
        module.active = enabled;

        addRenderableWidget(new Hotspot(artX + 14, topPos + 128, 55, 29,
                Component.translatable("menu.tidebound.repair"), () -> press(HarborMenu.ACTION_REPAIR)));
        if (menu.refitAvailable()) {
            addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                    Component.translatable("menu.tidebound.refit"), button -> press(HarborMenu.ACTION_REFIT))
                    .bounds(artX + 127, topPos + 185, 59, 15).build());
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (initializedRole != menu.role()) {
            clearWidgets();
            init();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (menu.role() == PortNpcRole.INTENDANT && menu.tideboundVessel()) {
            renderModuleTooltip(graphics, mouseX, mouseY);
        }
    }

    private void renderModuleTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        int x = artX(menu.role()) + 91 + 6;
        int y = topPos + 30 + 86;
        if (mouseX < x || mouseX > x + 81 || mouseY < y - 2 || mouseY > y + 8) {
            return;
        }
        int moduleSlots = menu.moduleSlots();
        List<Component> lines = List.of(
                moduleTooltipLine(VesselModule.SPOTLIGHT, moduleSlots, "screen.tidebound.module.spotlight"),
                moduleTooltipLine(VesselModule.SONAR, moduleSlots, "screen.tidebound.module.sonar"),
                moduleTooltipLine(VesselModule.WINCH, moduleSlots, "screen.tidebound.module.winch"),
                moduleTooltipLine(VesselModule.NET, moduleSlots, "screen.tidebound.module.net"));
        graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
    }

    private static Component moduleTooltipLine(VesselModule module, int moduleSlots, String translationKey) {
        boolean active = module.active(moduleSlots);
        ChatFormatting color = active ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY;
        return Component.literal(active ? "✓ " : "✗ ").append(Component.translatable(translationKey)).withStyle(color);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        Art art = art(menu.role());
        int artX = artX(menu.role());
        graphics.pose().pushPose();
        graphics.pose().translate(artX, topPos, 0);
        graphics.pose().scale(ART_SCALE, ART_SCALE, 1.0F);
        graphics.blit(art.texture(), 0, 0, 0, 0, art.width(), art.height(), art.width(), art.height());
        graphics.pose().popPose();

        if (menu.role() != PortNpcRole.INTENDANT) {
            if (menu.role() == PortNpcRole.FISHMONGER) {
                graphics.fill(artX + 119, topPos + 161, artX + 188, topPos + 183, 0xE6192528);
                graphics.drawString(font, Component.translatable("menu.tidebound.sale.estimate",
                                menu.saleCount(), menu.saleValue()),
                        artX + 123, topPos + 169, 0xFFFFD36A, false);
            }
            return;
        }

        // Replace the static mock-up values with synchronized state while retaining its frame and portrait.
        int x = artX + 91;
        int y = topPos + 30;
        graphics.fill(x, y, x + 93, y + 93, PANEL);
        graphics.drawString(font, Component.translatable(menu.tideboundVessel()
                ? "menu.tidebound.my_vessel" : "menu.tidebound.simple_boat"), x + 6, y + 5, INK, false);
        drawVesselSilhouette(graphics, x + 8, y + 20);
        if (menu.tideboundVessel()) {
            drawTier(graphics, Component.translatable("menu.tidebound.hull"), menu.hullTier(), x + 6, y + 37);
            drawTier(graphics, Component.translatable("menu.tidebound.motor"), menu.motorTier(), x + 6, y + 55);
            drawTier(graphics, Component.translatable("menu.tidebound.hold"), menu.holdTier(), x + 6, y + 73);
            drawTier(graphics, Component.translatable("menu.tidebound.modules"), menu.moduleSlots(), x + 6, y + 86);
        } else {
            graphics.drawWordWrap(font, Component.translatable("menu.tidebound.simple_boat.help"),
                    x + 6, y + 41, 81, 0xFF6B3F2B);
        }

        graphics.fill(artX + 11, topPos + 161, artX + 183, topPos + 183, 0xE6192528);
        graphics.drawString(font, Component.translatable("menu.tidebound.tides", menu.tides()),
                artX + 17, topPos + 169, 0xFFFFD36A, false);
        graphics.drawString(font, Component.translatable(menu.deployed()
                        ? "menu.tidebound.status.at_sea" : "menu.tidebound.status.at_harbor"),
                artX + 109, topPos + 169, 0xFFB9D8D0, false);
    }

    private void drawTier(GuiGraphics graphics, Component label, int tier, int x, int y) {
        graphics.drawString(font, label, x, y, INK, false);
        int barX = x + 43;
        int width = 38;
        graphics.fill(barX, y, barX + width, y + 6, TRACK);
        graphics.fill(barX + 1, y + 1, barX + 1 + Math.round((width - 2) * tier / 5.0F), y + 5, FILL);
        graphics.drawString(font, tier + "/5", barX + 14, y, 0xFFFFFFFF, true);
    }

    private static void drawVesselSilhouette(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y + 9, x + 33, y + 13, 0xFF744C2C);
        graphics.fill(x + 4, y + 13, x + 29, y + 16, 0xFF4D3324);
        graphics.fill(x + 15, y, x + 17, y + 10, 0xFF513725);
        graphics.fill(x + 17, y + 2, x + 26, y + 8, 0xFFE5D5B0);
        graphics.fill(x + 5, y + 17, x + 31, y + 18, 0xFF2C8090);
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

    private int artX(PortNpcRole role) {
        return leftPos + Math.round((imageWidth - art(role).width() * ART_SCALE) / 2.0F);
    }

    private static Art art(PortNpcRole role) {
        return switch (role) {
            case INTENDANT -> new Art("harbor_intendant.png", 529, 573);
            case SHIPWRIGHT -> new Art("harbor_shipwright.png", 522, 573);
            case FISHMONGER -> new Art("harbor_fishmonger.png", 515, 573);
            case NATURALIST -> new Art("harbor_naturalist.png", 783, 466);
            case LIGHTHOUSE_KEEPER -> new Art("harbor_lighthouse_keeper.png", 768, 466);
        };
    }

    private record Art(ResourceLocation texture, int width, int height) {
        private Art(String filename, int width, int height) {
            this(ResourceLocation.fromNamespaceAndPath(TideboundCore.MOD_ID, "textures/gui/" + filename),
                    width, height);
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
