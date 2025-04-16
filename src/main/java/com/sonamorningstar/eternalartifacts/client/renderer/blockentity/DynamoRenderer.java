package com.sonamorningstar.eternalartifacts.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.client.resources.model.DynamoModel;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class DynamoRenderer implements BlockEntityRenderer<AbstractDynamo<?>> {
    private final DynamoModel model;

    public DynamoRenderer(BlockEntityRendererProvider.Context ctx) {
        this.model = new DynamoModel(ctx.bakeLayer(ModModelLayers.DYNAMO_LAYER));
    }

    @Override
    public void render(AbstractDynamo<?> dynamo, float tick, PoseStack poseStack, MultiBufferSource buff, int light, int overlay) {
        Direction facing = dynamo.hasLevel() ? dynamo.getBlockState().getValue(BlockStateProperties.FACING) : Direction.NORTH;
        Quaternionf faceRot = facing.getRotation();
        poseStack.pushPose();
        VertexConsumer consumer = getMaterial(dynamo).buffer(buff, RenderType::entityCutout);
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(faceRot);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        model.setupAnim(dynamo, tick);
        model.renderToBuffer(poseStack, consumer, light, overlay, 1, 1, 1, 1);
        poseStack.popPose();
    }
    
    protected Material getMaterial(AbstractDynamo<?> dynamo) {
        String path = BuiltInRegistries.BLOCK.getKey(dynamo.getBlockState().getBlock()).getPath();
        return new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MODID, "block/"+path));
    }
}
