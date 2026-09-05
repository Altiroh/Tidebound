package dev.tidebound.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tidebound.core.vessel.TideboundVesselEntity;
import dev.tidebound.core.vessel.VesselVisualProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** A code-native voxel silhouette; a future Blockbench model can replace only this renderer. */
public final class TideboundVesselRenderer extends EntityRenderer<TideboundVesselEntity> {
    public TideboundVesselRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 1.3F;
    }

    @Override
    public void render(TideboundVesselEntity vessel, float yaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffers, int packedLight) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        pose.translate(0.0F, 0.35F, 0.0F);

        VesselVisualProfile profile = vessel.visualProfile();
        renderHull(pose, buffers, packedLight, profile);
        renderDeck(pose, buffers, packedLight, profile);
        renderEquipment(pose, buffers, packedLight, profile);

        pose.popPose();
        super.render(vessel, yaw, partialTick, pose, buffers, packedLight);
    }

    private static void renderHull(PoseStack pose, MultiBufferSource buffers, int light,
                                   VesselVisualProfile profile) {
        BlockState timber = profile.hullTier() >= 4 ? Blocks.DARK_OAK_PLANKS.defaultBlockState()
                : Blocks.OAK_PLANKS.defaultBlockState();
        for (int z = -2; z <= 2; z++) {
            float taper = Math.abs(z) == 2 ? 0.58F : 0.78F;
            block(pose, buffers, light, timber, 0, -0.28F, z * 0.55F, taper, 0.32F, 0.62F);
            if (Math.abs(z) < 2) {
                block(pose, buffers, light, timber, -0.72F, -0.05F, z * 0.55F, 0.25F, 0.45F, 0.62F);
                block(pose, buffers, light, timber, 0.72F, -0.05F, z * 0.55F, 0.25F, 0.45F, 0.62F);
            }
        }
        if (profile.reinforcedHull()) {
            BlockState plate = profile.hullTier() >= 4 ? Blocks.CUT_COPPER.defaultBlockState()
                    : Blocks.EXPOSED_CUT_COPPER.defaultBlockState();
            block(pose, buffers, light, plate, -0.80F, -0.18F, 0, 0.12F, 0.18F, 2.65F);
            block(pose, buffers, light, plate, 0.80F, -0.18F, 0, 0.12F, 0.18F, 2.65F);
        }
    }

    private static void renderDeck(PoseStack pose, MultiBufferSource buffers, int light,
                                   VesselVisualProfile profile) {
        BlockState deck = Blocks.SPRUCE_SLAB.defaultBlockState();
        for (int z = -1; z <= 1; z++) {
            block(pose, buffers, light, deck, 0, 0.16F, z * 0.62F, 1.3F, 0.17F, 0.62F);
        }
        block(pose, buffers, light, Blocks.OAK_FENCE.defaultBlockState(), 0, 0.63F, 0.38F,
                0.18F, 1.45F, 0.18F);
        block(pose, buffers, light, Blocks.WHITE_WOOL.defaultBlockState(), 0.25F, 1.05F, 0.38F,
                0.08F, 0.72F, 0.92F);
        if (profile.enclosedHold()) {
            block(pose, buffers, light, Blocks.BARREL.defaultBlockState(), 0, 0.35F, -0.56F,
                    0.62F, 0.50F, 0.55F);
        }
    }

    private static void renderEquipment(PoseStack pose, MultiBufferSource buffers, int light,
                                        VesselVisualProfile profile) {
        if (profile.poweredEngine()) {
            BlockState motor = profile.motorTier() >= 4 ? Blocks.BLAST_FURNACE.defaultBlockState()
                    : Blocks.FURNACE.defaultBlockState();
            block(pose, buffers, light, motor, 0, 0.40F, 1.08F, 0.52F, 0.52F, 0.52F);
        }
        int slots = profile.moduleSlots();
        for (int i = 0; i < slots; i++) {
            float side = i % 2 == 0 ? -0.58F : 0.58F;
            float z = i < 2 ? -0.12F : 0.64F;
            block(pose, buffers, light, Blocks.LANTERN.defaultBlockState(), side, 0.56F, z,
                    0.28F, 0.38F, 0.28F);
        }
    }

    private static void block(PoseStack pose, MultiBufferSource buffers, int light, BlockState state,
                              float x, float y, float z, float sx, float sy, float sz) {
        pose.pushPose();
        pose.translate(x - sx / 2.0F, y, z - sz / 2.0F);
        pose.scale(sx, sy, sz);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, pose, buffers, light, 0);
        pose.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(TideboundVesselEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
