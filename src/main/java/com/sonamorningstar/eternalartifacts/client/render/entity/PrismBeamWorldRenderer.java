package com.sonamorningstar.eternalartifacts.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.PrismBeamEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class PrismBeamWorldRenderer {
    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("textures/entity/beacon_beam.png");

    private static final float[][] COLORS = {
        {1.0f, 0.2f, 0.2f},
        {1.0f, 0.6f, 0.2f},
        {1.0f, 1.0f, 0.2f},
        {0.2f, 1.0f, 0.2f},
        {0.2f, 0.6f, 1.0f},
        {0.5f, 0.2f, 1.0f},
        {0.8f, 0.2f, 1.0f},
    };

    public static void renderAll(PoseStack pose, MultiBufferSource.BufferSource buffer, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.player == null) return;

        for (PrismBeamEntity entity : level.getEntitiesOfClass(PrismBeamEntity.class,
                mc.player.getBoundingBox().inflate(mc.options.renderDistance().get() * 16), e -> true)) {
            renderBeam(entity, pose, buffer, partialTick);
        }

        buffer.endBatch(RenderType.beaconBeam(BEAM_TEXTURE, true));
    }

    private static void renderBeam(PrismBeamEntity entity, PoseStack pose, MultiBufferSource buffer, float partialTick) {
        LivingEntity owner = entity.getOwner();
        if (owner == null) return;

        Vec3 eyePos = owner.getEyePosition(partialTick);
        Vec3 dir = entity.getBeamDirection(partialTick);
        if (dir.lengthSqr() < 0.001) return;
        
        BlockHitResult hitResult = entity.getHitResult(eyePos);
        float beamLength;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            beamLength = (float) eyePos.distanceTo(hitResult.getLocation());
        } else {
            beamLength = (float) entity.getBeamLength();
        }
        int age = entity.getAge();
        float time = (age + partialTick) * 0.1F;
        
        float colorTime = (age + partialTick) * 0.05F;
        int colorIndex = (int) colorTime % COLORS.length;
        int nextColorIndex = (colorIndex + 1) % COLORS.length;
        float colorLerp = colorTime - (int) colorTime;

        float r = Mth.lerp(colorLerp, COLORS[colorIndex][0], COLORS[nextColorIndex][0]);
        float g = Mth.lerp(colorLerp, COLORS[colorIndex][1], COLORS[nextColorIndex][1]);
        float b = Mth.lerp(colorLerp, COLORS[colorIndex][2], COLORS[nextColorIndex][2]);

        float beamRotation = (age + partialTick) * 0.2F;

        pose.pushPose();
        pose.translate(eyePos.x, eyePos.y, eyePos.z);

        float yaw = (float) Math.atan2(dir.x, dir.z);
        float pitch = (float) Math.asin(-dir.y);
        pose.mulPose(new Quaternionf().rotateY(yaw));
        pose.mulPose(new Quaternionf().rotateX(pitch));
        pose.mulPose(new Quaternionf().rotateX((float) (Math.PI / 2)));

        pose.pushPose();
        pose.mulPose(new Quaternionf().rotateY(beamRotation));
        renderSquareTube(pose, buffer, time, beamLength, r, g, b, 0.35f, 0.15f, true);
        pose.popPose();

        pose.pushPose();
        pose.mulPose(new Quaternionf().rotateY(-beamRotation));
        renderSquareTube(pose, buffer, -time, beamLength, r, g, b, 0.15f, 0.4f, true);
        pose.popPose();

        renderEndCap(pose, buffer, beamLength, r, g, b, 0.3f, 0.4f);

        pose.popPose();
    }
    
    private static void renderSquareTube(PoseStack poseStack, MultiBufferSource buffer,
                                         float uvOffset, float height,
                                         float red, float green, float blue, float alpha, float radius,
                                         boolean translucent) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();
        VertexConsumer consumer = buffer.getBuffer(RenderType.beaconBeam(BEAM_TEXTURE, translucent));

        float v0 = uvOffset;
        float v1 = uvOffset + height;

        addVertex(consumer, matrix, normal, red, green, blue, alpha, height, -radius, -radius, 1.0F, v0);
        addVertex(consumer, matrix, normal, red, green, blue, alpha, 0,      -radius, -radius, 1.0F, v1);
        addVertex(consumer, matrix, normal, red, green, blue, alpha, 0,       radius, -radius, 0.0F, v1);
        addVertex(consumer, matrix, normal, red, green, blue, alpha, height,  radius, -radius, 0.0F, v0);

        addVertex(consumer, matrix, normal, red, green, blue, alpha, height,  radius,  radius, 1.0F, v0);
        addVertex(consumer, matrix, normal, red, green, blue, alpha, 0,       radius,  radius, 1.0F, v1);
        addVertex(consumer, matrix, normal, red, green, blue, alpha, 0,      -radius,  radius, 0.0F, v1);
        addVertex(consumer, matrix, normal, red, green, blue, alpha, height, -radius,  radius, 0.0F, v0);

        addVertex(consumer, matrix, normal, red, green, blue, alpha, height,  radius, -radius, 1.0F, v0);
        addVertex(consumer, matrix, normal, red, green, blue, alpha, 0,       radius, -radius, 1.0F, v1);
        addVertex(consumer, matrix, normal, red, green, blue, alpha, 0,       radius,  radius, 0.0F, v1);
        addVertex(consumer, matrix, normal, red, green, blue, alpha, height,  radius,  radius, 0.0F, v0);

        addVertex(consumer, matrix, normal, red, green, blue, alpha, height, -radius,  radius, 1.0F, v0);
        addVertex(consumer, matrix, normal, red, green, blue, alpha, 0,      -radius,  radius, 1.0F, v1);
        addVertex(consumer, matrix, normal, red, green, blue, alpha, 0,      -radius, -radius, 0.0F, v1);
        addVertex(consumer, matrix, normal, red, green, blue, alpha, height, -radius, -radius, 0.0F, v0);
    }
    
    private static void renderEndCap(PoseStack poseStack, MultiBufferSource buffer,
                                     float beamLength, float r, float g, float b, float alpha, float radius) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();
        VertexConsumer consumer = buffer.getBuffer(RenderType.beaconBeam(BEAM_TEXTURE, true));

        addVertex(consumer, matrix, normal, r, g, b, alpha, beamLength, -radius, -radius, 0.0F, 0.0F);
        addVertex(consumer, matrix, normal, r, g, b, alpha, beamLength,  radius, -radius, 1.0F, 0.0F);
        addVertex(consumer, matrix, normal, r, g, b, alpha, beamLength,  radius,  radius, 1.0F, 1.0F);
        addVertex(consumer, matrix, normal, r, g, b, alpha, beamLength, -radius,  radius, 0.0F, 1.0F);
    }

    private static void addVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
                                  float r, float g, float b, float a,
                                  float y, float x, float z, float u, float v) {
        consumer.vertex(matrix, x, y, z)
            .color(r, g, b, a)
            .uv(u, v)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(0xF000F0)
            .normal(normal, 0.0F, 1.0F, 0.0F)
            .endVertex();
    }
}
