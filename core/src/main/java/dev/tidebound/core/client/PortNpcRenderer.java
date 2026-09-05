package dev.tidebound.core.client;

import dev.tidebound.core.TideboundCore;
import dev.tidebound.core.npc.PortNpcEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Uses the five role-specific UV atlases while preserving the familiar villager silhouette. */
public final class PortNpcRenderer extends MobRenderer<PortNpcEntity, VillagerModel<PortNpcEntity>> {
    public PortNpcRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(PortNpcEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(TideboundCore.MOD_ID,
                "textures/entity/port_npc/" + entity.role().id() + ".png");
    }
}
