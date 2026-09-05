package dev.tidebound.core.registry;

import dev.tidebound.core.TideboundCore;
import dev.tidebound.core.menu.HarborMenu;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TideboundMenus {
    private static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, TideboundCore.MOD_ID);

    public static final Supplier<MenuType<HarborMenu>> HARBOR = MENUS.register(
            "harbor",
            () -> new MenuType<>(HarborMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );

    private TideboundMenus() {
    }

    public static void register(IEventBus modBus) {
        MENUS.register(modBus);
    }
}
