package com.sonamorningstar.eternalartifacts.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.client.resources.model.RetexturedModel;
import com.sonamorningstar.eternalartifacts.client.resources.model.util.ModelHelper;
import com.sonamorningstar.eternalartifacts.content.block.entity.FancyChestBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;

public class FancyChestRenderer implements BlockEntityRenderer<FancyChestBlockEntity> {
    private static final BlockColors blockColors = BlockColors.createDefault();

    public FancyChestRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(FancyChestBlockEntity blockEntity, float partialTicks, PoseStack pose,
                       MultiBufferSource buffer, int light, int overlay) {

        float f1 = blockEntity.getOpenNess(partialTicks);
        f1 = 1.0F - f1;
        f1 = 1.0F - f1 * f1 * f1;

        BlockState blockState = blockEntity.getBlockState();
        RetexturedModel.Baked baked = ModelHelper.getBakedModel(blockState, RetexturedModel.Baked.class);
        if (baked == null) return;
        int color = blockColors.getColor(blockState, blockEntity.getLevel(), blockEntity.getBlockPos(), 0);
        float red = (float)(color >> 16 & 0xFF) / 255.0F;
        float green = (float)(color >> 8 & 0xFF) / 255.0F;
        float blue = (float)(color & 0xFF) / 255.0F;
        for (RenderType type : baked.getRenderTypes(blockState, RandomSource.create(42), blockEntity.getModelData())) {
            VertexConsumer consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(type, true));
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                    pose.last(), consumer, blockState,
                    baked, red, green, blue, light, overlay,
                    blockEntity.getModelData(), type);
        }

    }

    private void renderModels(PoseStack matrixStackIn, VertexConsumer bufferIn, ModelPart chestLid,
                              ModelPart chestLatch, ModelPart chestBottom, float lidAngle, int combinedLightIn,
                              int combinedOverlayIn) {
        chestLid.xRot = -(lidAngle * 1.5707964F);
        chestLatch.xRot = chestLid.xRot;
        chestLid.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        chestLatch.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        chestBottom.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }
}
