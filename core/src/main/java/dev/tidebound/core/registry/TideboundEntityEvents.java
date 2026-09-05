package dev.tidebound.core.registry;

import dev.tidebound.core.TideboundCore;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

/** Supplies vanilla villager attributes to each visual port role. */
@EventBusSubscriber(modid = TideboundCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class TideboundEntityEvents {
    private TideboundEntityEvents() {
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        var attributes = Villager.createAttributes().build();
        event.put(TideboundEntities.HARBOR_INTENDANT.get(), attributes);
        event.put(TideboundEntities.SHIPWRIGHT.get(), attributes);
        event.put(TideboundEntities.FISHMONGER.get(), attributes);
        event.put(TideboundEntities.NATURALIST.get(), attributes);
        event.put(TideboundEntities.LIGHTHOUSE_KEEPER.get(), attributes);
    }
}
