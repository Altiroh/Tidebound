package dev.tidebound.core.registry;

import dev.tidebound.core.TideboundCore;
import dev.tidebound.core.item.WakeCompassItem;
import java.util.function.Supplier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TideboundItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TideboundCore.MOD_ID);

    public static final Supplier<Item> WAKE_COMPASS = ITEMS.registerItem(
            "wake_compass",
            WakeCompassItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );

    private TideboundItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
        modBus.addListener(TideboundItems::addToCreativeTabs);
    }

    private static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(WAKE_COMPASS.get());
        }
    }
}
