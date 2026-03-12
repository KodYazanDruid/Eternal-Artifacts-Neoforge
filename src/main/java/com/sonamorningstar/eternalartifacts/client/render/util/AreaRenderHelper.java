package com.sonamorningstar.eternalartifacts.client.render.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.client.render.ModRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

/**
 * General-purpose helper for rendering translucent area boxes with outlines,
 * similar to beacon/working-area highlighting.
 */
public final class AreaRenderHelper {

    private static final float OUTLINE_R = 0.3f;
    private static final float OUTLINE_G = 0.6f;
    private static final float OUTLINE_B = 1.0f;
    private static final float OUTLINE_A = 1.0f;

    private static final float FACE_R = 0.2f;
    private static final float FACE_G = 0.5f;
    private static final float FACE_B = 0.9f;
    private static final float FACE_A = 0.15f;

    private AreaRenderHelper() {}

    /**
     * Renders a translucent box with outline for the given AABB in world coordinates.
     * The PoseStack should already be translated so that world coords are correct
     * (i.e. camera offset already applied).
     * Does NOT call endBatch – the caller should batch-flush after all boxes are rendered.
     */
    public static void renderBox(PoseStack pose, MultiBufferSource buffer, AABB box) {
        renderBox(pose, buffer, box, FACE_R, FACE_G, FACE_B, FACE_A, OUTLINE_R, OUTLINE_G, OUTLINE_B, OUTLINE_A);
    }

    public static void renderBox(PoseStack pose, MultiBufferSource buffer, AABB box,
                                  float faceR, float faceG, float faceB, float faceA,
                                  float outlineR, float outlineG, float outlineB, float outlineA) {
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        renderFaces(pose, buffer, minX, minY, minZ, maxX, maxY, maxZ, faceR, faceG, faceB, faceA);
        renderOutline(pose, buffer, minX, minY, minZ, maxX, maxY, maxZ, outlineR, outlineG, outlineB, outlineA);
    }

    /**
     * Flushes the area render types. Call once after all boxes in a frame have been submitted.
     */
    public static void endBatch(MultiBufferSource.BufferSource buffer) {
        buffer.endBatch(ModRenderTypes.AREA_FACE);
        buffer.endBatch(ModRenderTypes.AREA_OUTLINE);
    }

    // ── faces ────────────────────────────────────────────────────────────────

    private static void renderFaces(PoseStack pose, MultiBufferSource buff,
                                    float minX, float minY, float minZ,
                                    float maxX, float maxY, float maxZ,
                                    float r, float g, float b, float a) {
        VertexConsumer consumer = buff.getBuffer(ModRenderTypes.AREA_FACE);
        Matrix4f matrix = pose.last().pose();
        int color = ((int)(a * 255) << 24) | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);

        // Bottom (Y-)
        consumer.vertex(matrix, minX, minY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, minY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, minY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, minY, maxZ).color(color).endVertex();

        // Top (Y+)
        consumer.vertex(matrix, minX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, minZ).color(color).endVertex();

        // North (Z-)
        consumer.vertex(matrix, minX, minY, minZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, minY, minZ).color(color).endVertex();

        // South (Z+)
        consumer.vertex(matrix, maxX, minY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, minY, maxZ).color(color).endVertex();

        // West (X-)
        consumer.vertex(matrix, minX, minY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, minX, minY, minZ).color(color).endVertex();

        // East (X+)
        consumer.vertex(matrix, maxX, minY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, minY, maxZ).color(color).endVertex();
    }

    // ── outline ──────────────────────────────────────────────────────────────

    private static void renderOutline(PoseStack pose, MultiBufferSource buff,
                                      float minX, float minY, float minZ,
                                      float maxX, float maxY, float maxZ,
                                      float r, float g, float b, float a) {
        VertexConsumer consumer = buff.getBuffer(ModRenderTypes.AREA_OUTLINE);
        Matrix4f matrix = pose.last().pose();

        // Bottom edges
        line(consumer, matrix, minX, minY, minZ, maxX, minY, minZ, r, g, b, a);
        line(consumer, matrix, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a);
        line(consumer, matrix, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
        line(consumer, matrix, minX, minY, maxZ, minX, minY, minZ, r, g, b, a);

        // Top edges
        line(consumer, matrix, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a);
        line(consumer, matrix, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a);
        line(consumer, matrix, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
        line(consumer, matrix, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);

        // Vertical edges
        line(consumer, matrix, minX, minY, minZ, minX, maxY, minZ, r, g, b, a);
        line(consumer, matrix, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a);
        line(consumer, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a);
        line(consumer, matrix, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a);
    }

    private static void line(VertexConsumer consumer, Matrix4f matrix,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float r, float g, float b, float a) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len == 0) len = 1;
        float nx = dx / len;
        float ny = dy / len;
        float nz = dz / len;

        consumer.vertex(matrix, x1, y1, z1)
                .color(r, g, b, a)
                .normal(nx, ny, nz)
                .endVertex();
        consumer.vertex(matrix, x2, y2, z2)
                .color(r, g, b, a)
                .normal(nx, ny, nz)
                .endVertex();
    }
}
