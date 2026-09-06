package dev.tidebound.core.advancement;

import dev.tidebound.core.TideboundCore;
import java.util.function.Supplier;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TideboundCriteriaTriggers {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, TideboundCore.MOD_ID);

    public static final Supplier<TideboundSignalTrigger> QUEST_SIGNAL =
            TRIGGERS.register("quest_signal", TideboundSignalTrigger::new);

    private TideboundCriteriaTriggers() {
    }

    public static void register(IEventBus modBus) {
        TRIGGERS.register(modBus);
    }
}
