package com.sonamorningstar.eternalartifacts.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.IAreaRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AreaRenderer<T extends BlockEntity & IAreaRenderer> implements BlockEntityRenderer<T> {
    @Override
    public void render(T be, float partialTick, PoseStack pose, MultiBufferSource buff, int light, int overlay) {
        VertexConsumer consumer = buff.getBuffer(RenderType.lines());
        AABB box = be.getRenderBoundingBox();
        Vec3 posVec = Vec3.atLowerCornerOf(be.getBlockPos());
        if(be.shouldRender()) LevelRenderer.renderLineBox(pose, consumer,
                box.minX - posVec.x, box.minY - posVec.y, box.minZ - posVec.z,
                box.maxX - posVec.x, box.maxY - posVec.y, box.maxZ - posVec.z,
                0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
    }

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        return blockEntity.getRenderBoundingBox();
    }
}
