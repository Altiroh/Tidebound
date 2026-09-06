package dev.tidebound.core.registry;

import dev.tidebound.core.TideboundCore;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Single creative tab grouping every Tidebound item, instead of scattering them across vanilla tabs. */
public final class TideboundCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TideboundCore.MOD_ID);

    public static final Supplier<CreativeModeTab> TIDEBOUND = TABS.register(
            "tidebound",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.tidebound"))
                    .icon(() -> new ItemStack(TideboundItems.WAKE_COMPASS.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(TideboundItems.WAKE_COMPASS.get());
                        output.accept(TideboundItems.HAVEN_COMPASS.get());
                        output.accept(TideboundItems.REPAIR_KIT.get());
                        output.accept(TideboundItems.CAULKING_KIT.get());
                        output.accept(TideboundItems.ENGINE_PARTS.get());
                        output.accept(TideboundItems.HOLD_FITTINGS.get());
                        output.accept(TideboundItems.HULL_PLATE.get());
                        output.accept(TideboundItems.MECHANICAL_OIL.get());
                    })
                    .build()
    );

    private TideboundCreativeTabs() {
    }

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }
}
