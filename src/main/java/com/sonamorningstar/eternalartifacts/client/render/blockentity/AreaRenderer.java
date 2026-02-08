package com.sonamorningstar.eternalartifacts.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.client.render.ModRenderTypes;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.WorkingAreaProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class AreaRenderer<T extends BlockEntity & WorkingAreaProvider> implements BlockEntityRenderer<T> {
    
    private static final float OUTLINE_R = 0.3f;
    private static final float OUTLINE_G = 0.6f;
    private static final float OUTLINE_B = 1.0f;
    private static final float OUTLINE_A = 1.0f;
    
    private static final float FACE_R = 0.2f;
    private static final float FACE_G = 0.5f;
    private static final float FACE_B = 0.9f;
    private static final float FACE_A = 0.15f;
    
    @Override
    public void render(T be, float partialTick, PoseStack pose, MultiBufferSource buff, int light, int overlay) {
        if (!be.shouldRenderArea()) return;
        
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        
        AABB box = be.getWorkingArea(be.getBlockPos());
        Vec3 posVec = Vec3.atLowerCornerOf(be.getBlockPos());
        
        float minX = (float) (box.minX - posVec.x);
        float minY = (float) (box.minY - posVec.y);
        float minZ = (float) (box.minZ - posVec.z);
        float maxX = (float) (box.maxX - posVec.x);
        float maxY = (float) (box.maxY - posVec.y);
        float maxZ = (float) (box.maxZ - posVec.z);
        
        // Render translucent faces
        renderFaces(pose, bufferSource, minX, minY, minZ, maxX, maxY, maxZ);
        
        // Render thick outline
        renderOutline(pose, bufferSource, minX, minY, minZ, maxX, maxY, maxZ);
        
        bufferSource.endBatch(ModRenderTypes.AREA_FACE);
        bufferSource.endBatch(ModRenderTypes.AREA_OUTLINE);
    }
    
    private void renderFaces(PoseStack pose, MultiBufferSource buff,
                             float minX, float minY, float minZ,
                             float maxX, float maxY, float maxZ) {
        VertexConsumer consumer = buff.getBuffer(ModRenderTypes.AREA_FACE);
        Matrix4f matrix = pose.last().pose();
        
        int color = ((int)(FACE_A * 255) << 24) | ((int)(FACE_R * 255) << 16) | ((int)(FACE_G * 255) << 8) | (int)(FACE_B * 255);
        
        // Bottom face (Y-)
        consumer.vertex(matrix, minX, minY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, minY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, minY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, minY, maxZ).color(color).endVertex();
        
        // Top face (Y+)
        consumer.vertex(matrix, minX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, minZ).color(color).endVertex();
        
        // North face (Z-)
        consumer.vertex(matrix, minX, minY, minZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, minY, minZ).color(color).endVertex();
        
        // South face (Z+)
        consumer.vertex(matrix, maxX, minY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, minY, maxZ).color(color).endVertex();
        
        // West face (X-)
        consumer.vertex(matrix, minX, minY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, minX, minY, minZ).color(color).endVertex();
        
        // East face (X+)
        consumer.vertex(matrix, maxX, minY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, minY, maxZ).color(color).endVertex();
    }
    
    private void renderOutline(PoseStack pose, MultiBufferSource buff,
                               float minX, float minY, float minZ,
                               float maxX, float maxY, float maxZ) {
        VertexConsumer consumer = buff.getBuffer(ModRenderTypes.AREA_OUTLINE);
        Matrix4f matrix = pose.last().pose();
        
        // Bottom edges
        line(consumer, matrix, minX, minY, minZ, maxX, minY, minZ);
        line(consumer, matrix, maxX, minY, minZ, maxX, minY, maxZ);
        line(consumer, matrix, maxX, minY, maxZ, minX, minY, maxZ);
        line(consumer, matrix, minX, minY, maxZ, minX, minY, minZ);
        
        // Top edges
        line(consumer, matrix, minX, maxY, minZ, maxX, maxY, minZ);
        line(consumer, matrix, maxX, maxY, minZ, maxX, maxY, maxZ);
        line(consumer, matrix, maxX, maxY, maxZ, minX, maxY, maxZ);
        line(consumer, matrix, minX, maxY, maxZ, minX, maxY, minZ);
        
        // Vertical edges
        line(consumer, matrix, minX, minY, minZ, minX, maxY, minZ);
        line(consumer, matrix, maxX, minY, minZ, maxX, maxY, minZ);
        line(consumer, matrix, maxX, minY, maxZ, maxX, maxY, maxZ);
        line(consumer, matrix, minX, minY, maxZ, minX, maxY, maxZ);
    }
    
    private void line(VertexConsumer consumer, Matrix4f matrix,
                      float x1, float y1, float z1,
                      float x2, float y2, float z2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len == 0) len = 1;
        float nx = dx / len;
        float ny = dy / len;
        float nz = dz / len;
        
        consumer.vertex(matrix, x1, y1, z1)
                .color(OUTLINE_R, OUTLINE_G, OUTLINE_B, OUTLINE_A)
                .normal(nx, ny, nz)
                .endVertex();
        consumer.vertex(matrix, x2, y2, z2)
                .color(OUTLINE_R, OUTLINE_G, OUTLINE_B, OUTLINE_A)
                .normal(nx, ny, nz)
                .endVertex();
    }

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        return blockEntity.getWorkingArea(blockEntity.getBlockPos());
    }
}
