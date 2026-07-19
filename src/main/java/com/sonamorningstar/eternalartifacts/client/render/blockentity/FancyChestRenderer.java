package com.sonamorningstar.eternalartifacts.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.client.resources.model.RetexturedModel;
import com.sonamorningstar.eternalartifacts.client.resources.model.util.ModelHelper;
import com.sonamorningstar.eternalartifacts.content.block.FancyChestBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.FancyChestBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class FancyChestRenderer implements BlockEntityRenderer<FancyChestBlockEntity> {
    private static final BlockColors blockColors = BlockColors.createDefault();
    public static final ResourceLocation FANCY_CHEST_LID = new ResourceLocation(MODID, "block/fancy_chest_lid");
    private final BlockRenderDispatcher blockRenderer;
    private final ModelManager modelManager;
    
    public FancyChestRenderer(BlockEntityRendererProvider.Context context) {
        this.modelManager = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager();
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(FancyChestBlockEntity blockEntity, float partialTicks, PoseStack pose,
                       MultiBufferSource buffer, int light, int overlay) {

        float f1 = blockEntity.getOpenNess(partialTicks);
        f1 = 1.0F - f1;
        f1 = 1.0F - f1 * f1 * f1;

        BlockState blockState = blockEntity.getBlockState();
        BakedModel lidModel = modelManager.getModel(FANCY_CHEST_LID);
        int color = blockColors.getColor(blockState, blockEntity.getLevel(), blockEntity.getBlockPos(), 0);
        float red = (float)(color >> 16 & 0xFF) / 255.0F;
        float green = (float)(color >> 8 & 0xFF) / 255.0F;
        float blue = (float)(color & 0xFF) / 255.0F;
        pose.pushPose();
        pose.translate(0.5D, 0.5D, 0.5D);
        pose.mulPose(blockEntity.getBlockState().getValue(FancyChestBlock.FACING).getOpposite().getRotation());
        pose.mulPose(Axis.XN.rotationDegrees(90));
        pose.translate(-0.5D, -0.5D, -0.5D);
        
        float pivotX = 8f / 16f;
        float pivotY = 9f / 16f;
        float pivotZ = 15f / 16f;
        
        pose.translate(pivotX, pivotY, pivotZ);
        pose.mulPose(Axis.XP.rotationDegrees(f1 * 90.0F));
        pose.translate(-pivotX, -pivotY, -pivotZ);
        
        for (RenderType type : lidModel.getRenderTypes(blockState, RandomSource.create(42), blockEntity.getModelData())) {
            VertexConsumer consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(type, true));
            blockRenderer.getModelRenderer().renderModel(
                pose.last(), consumer, blockState,
                lidModel, red, green, blue, light, overlay,
                blockEntity.getModelData(), type);
        }
        pose.popPose();

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
