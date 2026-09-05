package dev.tidebound.core.registry;

import dev.tidebound.core.TideboundCore;
import dev.tidebound.core.npc.PortNpcEntity;
import dev.tidebound.core.npc.PortNpcRole;
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

    public static final Supplier<EntityType<PortNpcEntity>> HARBOR_INTENDANT = portNpc(
            "harbor_intendant", PortNpcRole.INTENDANT);
    public static final Supplier<EntityType<PortNpcEntity>> SHIPWRIGHT = portNpc(
            "shipwright", PortNpcRole.SHIPWRIGHT);
    public static final Supplier<EntityType<PortNpcEntity>> FISHMONGER = portNpc(
            "fishmonger", PortNpcRole.FISHMONGER);
    public static final Supplier<EntityType<PortNpcEntity>> NATURALIST = portNpc(
            "naturalist", PortNpcRole.NATURALIST);
    public static final Supplier<EntityType<PortNpcEntity>> LIGHTHOUSE_KEEPER = portNpc(
            "lighthouse_keeper", PortNpcRole.LIGHTHOUSE_KEEPER);

    private TideboundEntities() {
    }

    public static void register(IEventBus modBus) {
        ENTITIES.register(modBus);
    }

    private static Supplier<EntityType<PortNpcEntity>> portNpc(String id, PortNpcRole role) {
        return ENTITIES.register(id, () -> EntityType.Builder.<PortNpcEntity>of(
                        (type, level) -> new PortNpcEntity(type, level, role), MobCategory.CREATURE)
                .sized(0.6F, 1.95F)
                .clientTrackingRange(10)
                .build(TideboundCore.MOD_ID + ":" + id));
    }
}
