package dev.tidebound.core.client;

import dev.tidebound.core.TideboundCore;
import dev.tidebound.core.registry.TideboundDataComponents;
import dev.tidebound.core.registry.TideboundItems;
import dev.tidebound.core.registry.TideboundMenus;
import dev.tidebound.core.registry.TideboundEntities;
import net.minecraft.core.GlobalPos;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = TideboundCore.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class TideboundClientModEvents {
    private static final ResourceLocation ANGLE_PROPERTY =
            ResourceLocation.fromNamespaceAndPath(TideboundCore.MOD_ID, "angle");

    private TideboundClientModEvents() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(TideboundMenus.HARBOR.get(), HarborScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(TideboundEntities.VESSEL.get(), TideboundVesselRenderer::new);
        event.registerEntityRenderer(TideboundEntities.HARBOR_INTENDANT.get(), PortNpcRenderer::new);
        event.registerEntityRenderer(TideboundEntities.SHIPWRIGHT.get(), PortNpcRenderer::new);
        event.registerEntityRenderer(TideboundEntities.FISHMONGER.get(), PortNpcRenderer::new);
        event.registerEntityRenderer(TideboundEntities.NATURALIST.get(), PortNpcRenderer::new);
        event.registerEntityRenderer(TideboundEntities.LIGHTHOUSE_KEEPER.get(), PortNpcRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(TideboundItems.WAKE_COMPASS.get(), ANGLE_PROPERTY,
                    TideboundClientModEvents::compassAngle);
            ItemProperties.register(TideboundItems.HAVEN_COMPASS.get(), ANGLE_PROPERTY,
                    TideboundClientModEvents::compassAngle);
        });
    }

    /** Mirrors vanilla's compass needle: bearing to the stored target, relative to where the holder faces. */
    private static float compassAngle(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
        if (entity == null) {
            return spin(seed, seed);
        }
        GlobalPos target = stack.get(TideboundDataComponents.COMPASS_TARGET.get());
        if (target == null || level == null || !target.dimension().equals(level.dimension())) {
            return spin(entity.tickCount, seed);
        }
        double deltaX = target.pos().getX() + 0.5 - entity.getX();
        double deltaZ = target.pos().getZ() + 0.5 - entity.getZ();
        double targetYaw = Math.toDegrees(Math.atan2(-deltaX, deltaZ));
        return Mth.positiveModulo((float) ((targetYaw - entity.getYRot()) / 360.0), 1.0F);
    }

    private static float spin(int tick, int seed) {
        return Mth.positiveModulo((tick + seed * 37) / 200.0F, 1.0F);
    }
}
