package dev.tidebound.core.client;

import dev.tidebound.core.TideboundCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = TideboundCore.MOD_ID, value = Dist.CLIENT)
public final class TideboundClientEvents {
    private TideboundClientEvents() {
    }

    @SubscribeEvent
    public static void addLogbookButton(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen inventory)) {
            return;
        }
        int x = inventory.width / 2 + 68;
        int y = inventory.height / 2 - 82;
        Button button = Button.builder(Component.literal("⚓"), ignored ->
                        Minecraft.getInstance().setScreen(new CaptainsLogScreen(inventory)))
                .bounds(x, y, 18, 18)
                .tooltip(Tooltip.create(Component.translatable("screen.tidebound.logbook.tooltip")))
                .build();
        event.addListener(button);
    }
}
