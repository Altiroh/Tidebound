package dev.tidebound.core.registry;

import dev.tidebound.core.TideboundCore;
import dev.tidebound.core.data.TideboundCodecs;
import dev.tidebound.core.fishing.CatchData;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TideboundDataComponents {
    private static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(TideboundCore.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CatchData>> CATCH_DATA =
            COMPONENTS.registerComponentType("catch_data", builder -> builder
                    .persistent(TideboundCodecs.CATCH_DATA));

    private TideboundDataComponents() {
    }

    public static void register(IEventBus modBus) {
        COMPONENTS.register(modBus);
    }
}
