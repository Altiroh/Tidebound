package dev.tidebound.core.registry;

import dev.tidebound.core.TideboundCore;
import dev.tidebound.core.item.WakeCompassItem;
import dev.tidebound.core.item.HavenCompassItem;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TideboundItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TideboundCore.MOD_ID);

    public static final Supplier<Item> WAKE_COMPASS = ITEMS.registerItem(
            "wake_compass",
            WakeCompassItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );
    public static final Supplier<Item> HAVEN_COMPASS = ITEMS.registerItem(
            "haven_compass",
            HavenCompassItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );
    public static final Supplier<Item> REPAIR_KIT = component("repair_kit", 16);
    public static final Supplier<Item> CAULKING_KIT = component("caulking_kit", 16);
    public static final Supplier<Item> ENGINE_PARTS = component("engine_parts", 32);
    public static final Supplier<Item> HOLD_FITTINGS = component("hold_fittings", 32);
    public static final Supplier<Item> HULL_PLATE = component("hull_plate", 32);
    public static final Supplier<Item> MECHANICAL_OIL = component("mechanical_oil", 16);

    private TideboundItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    private static Supplier<Item> component(String id, int stackSize) {
        return ITEMS.registerSimpleItem(id, new Item.Properties().stacksTo(stackSize));
    }
}
