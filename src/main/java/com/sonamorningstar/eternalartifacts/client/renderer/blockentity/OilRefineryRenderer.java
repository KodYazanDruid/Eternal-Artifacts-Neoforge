package com.sonamorningstar.eternalartifacts.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.client.renderer.util.RendererHelper;
import com.sonamorningstar.eternalartifacts.content.block.entity.OilRefineryBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class OilRefineryRenderer implements BlockEntityRenderer<OilRefineryBlockEntity> {

    public static final Material REFINERY = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MODID, "block/oil_refinery"));
    private static ModelPart modelPart;

    public OilRefineryRenderer(BlockEntityRendererProvider.Context ctx) {
        modelPart = ctx.bakeLayer(ModModelLayers.OIL_REFINERY_LAYER);
    }

    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("right_tank",
                CubeListBuilder.create().texOffs(0, 32).addBox(9.0F, 0.0F, 0.0F, 7.0F, 14.0F, 7.0F),
                PartPose.offset(0, 2, 0));
        partdefinition.addOrReplaceChild("left_tank",
                CubeListBuilder.create().texOffs(0, 32).addBox(0.0F, 0.0F, 0.0F, 7.0F, 14.0F, 7.0F),
                PartPose.offset(0, 2, 0));
        partdefinition.addOrReplaceChild("input_tank",
                CubeListBuilder.create().texOffs(0, 18).addBox(1.0F, 0.0F, 9.0F, 14.0F, 7.0F, 7.0F),
                PartPose.offset(0, 2, 0));
        partdefinition.addOrReplaceChild("pipe_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, 7.0F, 7.0F, 2.0F, 2.0F, 4.0F),
                PartPose.offset(0, 2, 0));
        partdefinition.addOrReplaceChild("pipe_2",
                CubeListBuilder.create().texOffs(0, 6).addBox(7.0F, 2.0F, 7.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offset(0, 2, 0));
        partdefinition.addOrReplaceChild("pipe_3",
                CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, 0.0F, 5.0F, 2.0F, 2.0F, 4.0F),
                PartPose.offset(0, 2, 0));
        partdefinition.addOrReplaceChild("floor",
                CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 16.0F, 2.0F, 16.0F),
                PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void render(OilRefineryBlockEntity refinery, float tick, PoseStack poseStack, MultiBufferSource buff, int light, int overlay) {
        poseStack.pushPose();

        Direction facing = refinery.hasLevel() ? refinery.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
        float yRot = facing.toYRot();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        VertexConsumer consumer = REFINERY.buffer(buff, RenderType::entityCutout);
        modelPart.render(poseStack, consumer, light, overlay);

        //Input tank.
        RendererHelper.renderFluidCube(poseStack, buff, refinery.tanks.get(0),
                new RendererHelper.FluidRenderCubeInfo(RendererHelper.FluidRenderCubeInfo.allExcept(Direction.DOWN), true),
                light, overlay, 12, 5, 5, 2, 3, 10);

        //Right tank.
        RendererHelper.renderFluidCube(poseStack, buff, refinery.tanks.get(1),
                new RendererHelper.FluidRenderCubeInfo(RendererHelper.FluidRenderCubeInfo.allExcept(Direction.DOWN), true),
                light, overlay, 5, 12, 5, 10, 3, 1);

        //Left tank.
        RendererHelper.renderFluidCube(poseStack, buff, refinery.tanks.get(2),
                new RendererHelper.FluidRenderCubeInfo(RendererHelper.FluidRenderCubeInfo.allExcept(Direction.DOWN), true),
                light, overlay, 5, 12, 5, 1, 3, 1);

        poseStack.popPose();
    }
}
