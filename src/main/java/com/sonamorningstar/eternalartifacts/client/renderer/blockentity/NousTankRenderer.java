package com.sonamorningstar.eternalartifacts.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.client.renderer.util.RendererHelper;
import com.sonamorningstar.eternalartifacts.content.block.entity.NousTankBlockEntity;
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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class NousTankRenderer implements BlockEntityRenderer<NousTankBlockEntity> {
    public static final Material TEXTURE_TANK = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MODID, "block/nous_tank"));

    private static ModelPart modelPart;
    public NousTankRenderer(BlockEntityRendererProvider.Context ctx) {
        modelPart = ctx.bakeLayer(ModModelLayers.NOUS_TANK_LAYER);
    }

    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("top",
                CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 1.0F, 14.0F, 2.0F, 14.0F),
                PartPose.offset(0, 12, 0));
        partdefinition.addOrReplaceChild("bottom",
                CubeListBuilder.create().texOffs(0, 16).addBox(1.0F, 0.0F, 1.0F, 14.0F, 2.0F, 14.0F),
                PartPose.ZERO);
        partdefinition.addOrReplaceChild("glass_tank",
                CubeListBuilder.create().texOffs(0, 20).addBox(2.0F, 0.0F, 2.0F, 12.0F, 10.0F, 12.0F),
                PartPose.offset(0, 2, 0));
        partdefinition.addOrReplaceChild("leg_1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 2.0F, 0.0F, 2.0F, 10.0F, 2.0F),
                PartPose.offset(1, 0, 1));
        partdefinition.addOrReplaceChild("leg_2",
                CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 2.0F, 0.0F, 2.0F, 10.0F, 2.0F),
                PartPose.offset(13, 0, 1));
        partdefinition.addOrReplaceChild("leg_3",
                CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 2.0F, 0.0F, 2.0F, 10.0F, 2.0F),
                PartPose.offset(1, 0, 13));
        partdefinition.addOrReplaceChild("leg_4",
                CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 2.0F, 0.0F, 2.0F, 10.0F, 2.0F),
                PartPose.offset(13, 0, 13));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
    @Override
    public void render(NousTankBlockEntity tank, float tick, PoseStack poseStack, MultiBufferSource buff, int light, int overlay) {
        poseStack.pushPose();

        Direction facing = tank.hasLevel() ? tank.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
        float yRot = facing.toYRot();;
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        VertexConsumer consumer = TEXTURE_TANK.buffer(buff, RenderType::entityCutout);
        modelPart.render(poseStack, consumer, light, overlay);
        RendererHelper.renderFluidCube(poseStack, buff, tank.tank,
                new RendererHelper.FluidRenderCubeInfo(RendererHelper.FluidRenderCubeInfo.allExcept(Direction.DOWN), false),
                light, overlay, 10, 10, 10, 3, 2, 3);
        poseStack.popPose();
    }
}
