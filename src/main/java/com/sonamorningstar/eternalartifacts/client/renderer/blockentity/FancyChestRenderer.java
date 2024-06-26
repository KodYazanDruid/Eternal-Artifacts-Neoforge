package com.sonamorningstar.eternalartifacts.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.content.block.entity.FancyChestBlockEntity;
import com.sonamorningstar.eternalartifacts.content.entity.client.ModModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import org.joml.Quaternionf;

public class FancyChestRenderer implements BlockEntityRenderer<FancyChestBlockEntity> {

    private final ModelPart lid;
    private final ModelPart base;
    private final ModelPart lock;

    public FancyChestRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(ModModelLayers.FANCY_CHEST_LAYER);
        this.base = modelpart.getChild("base");
        this.lid = modelpart.getChild("lid");
        this.lock = modelpart.getChild("lock");
    }

    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("base",
                CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("lid",
                CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F),
                PartPose.offset(0.0F, 10.0F, 1.0F));
        partdefinition.addOrReplaceChild("lock",
                CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 9.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void render(FancyChestBlockEntity blockEntity, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        float f = blockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING).toYRot();
        matrixStackIn.translate(0.5D, 0.5D, 0.5D);
        matrixStackIn.mulPose(new Quaternionf().rotationY(Mth.DEG_TO_RAD * -f));
        matrixStackIn.translate(-0.5D, -0.5D, -0.5D);

        float f1 = blockEntity.getOpenNess(partialTicks);
        f1 = 1.0F - f1;
        f1 = 1.0F - f1 * f1 * f1;
        Material material = getRenderMaterial(blockEntity);
        VertexConsumer vertexbuilder = material.buffer(bufferIn, RenderType::entityCutout);
        this.renderModels(matrixStackIn, vertexbuilder, this.lid, this.lock, this.base, f1,
                combinedLightIn, combinedOverlayIn);

        matrixStackIn.popPose();
/*
        RetexturedModel.Baked model = ModelHelper.getBakedModel(blockEntity.getBlockState(), RetexturedModel.Baked.class);
        matrixStackIn.pushPose();
        Material material = getRenderMaterial(blockEntity);
        VertexConsumer vertexbuilder = material.buffer(bufferIn, RenderType::entityCutout);
        if(model != null){
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                    matrixStackIn.last(), vertexbuilder, blockEntity.getBlockState(), model, 255, 255, 255, combinedLightIn, combinedOverlayIn);
        }
        matrixStackIn.popPose();*/

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

    private Material getRenderMaterial(FancyChestBlockEntity blockEntity) {
        //return new Material(InventoryMenu.BLOCK_ATLAS, BuiltInRegistries.BLOCK.getKey(blockEntity.getTexture()));
        return new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("block/birch_log"));
    }
}
