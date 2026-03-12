package com.sonamorningstar.eternalartifacts.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.render.util.AreaRenderHelper;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.WorkingAreaProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AreaRenderer<T extends BlockEntity & WorkingAreaProvider> implements BlockEntityRenderer<T> {
    
    @Override
    public void render(T be, float partialTick, PoseStack pose, MultiBufferSource buff, int light, int overlay) {
        if (!be.shouldRenderArea()) return;
        
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        
        AABB box = be.getWorkingArea(be.getBlockPos());
        Vec3 posVec = Vec3.atLowerCornerOf(be.getBlockPos());
        
        AABB offsetBox = box.move(-posVec.x, -posVec.y, -posVec.z);
        
        AreaRenderHelper.renderBox(pose, bufferSource, offsetBox);
        AreaRenderHelper.endBatch(bufferSource);
    }

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        return blockEntity.getWorkingArea(blockEntity.getBlockPos());
    }
}
