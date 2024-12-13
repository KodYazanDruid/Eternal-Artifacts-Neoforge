package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.Meteorite;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class MeteoriteRenderer extends EntityRenderer<Meteorite> {
    private final MeteoriteModel model;
    public MeteoriteRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        model = new MeteoriteModel(ctx.bakeLayer(ModModelLayers.METEORITE_LAYER));
    }

    //TODO: Create custom rendering block method to handle culling faces.
    @Override
    public void render(Meteorite meteorite, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buff, int light) {
        BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
        long tick = Minecraft.getInstance().clientTickCount;
        BlockState magma = Blocks.MAGMA_BLOCK.defaultBlockState();
        poseStack.pushPose();
        poseStack.translate(-1, 0, -1);
        poseStack.rotateAround(Axis.YP.rotationDegrees(tick + partialTick), 1, 1, 1);
        poseStack.rotateAround(Axis.XP.rotationDegrees(tick + partialTick), 1, 1, 1);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(tick + partialTick), 1, 1, 1);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    poseStack.pushPose();
                    poseStack.translate(i, j, k);
                    renderer.renderSingleBlock(magma, poseStack, buff, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
                    poseStack.popPose();
                }
            }
        }
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Meteorite meteorite) {
        return new ResourceLocation("block/magma");
    }
}
