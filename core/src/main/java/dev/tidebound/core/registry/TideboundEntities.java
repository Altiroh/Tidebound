package dev.tidebound.core.registry;

import dev.tidebound.core.TideboundCore;
import dev.tidebound.core.vessel.TideboundVesselEntity;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Entity registrations kept separate from the persistent player-owned vessel model. */
public final class TideboundEntities {
    private static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TideboundCore.MOD_ID);

    public static final Supplier<EntityType<TideboundVesselEntity>> VESSEL = ENTITIES.register(
            "vessel",
            () -> EntityType.Builder.<TideboundVesselEntity>of(TideboundVesselEntity::new, MobCategory.MISC)
                    .sized(2.4F, 1.25F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .build("tidebound:vessel")
    );

    private TideboundEntities() {
    }

    public static void register(IEventBus modBus) {
        ENTITIES.register(modBus);
    }
}
