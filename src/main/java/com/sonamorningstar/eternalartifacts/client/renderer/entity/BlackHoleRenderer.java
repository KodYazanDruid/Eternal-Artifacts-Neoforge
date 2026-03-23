package com.sonamorningstar.eternalartifacts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.client.render.ModRenderTypes;
import com.sonamorningstar.eternalartifacts.client.shader.SpellShaders;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.BlackHoleEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class BlackHoleRenderer extends EntityRenderer<BlackHoleEntity> {
    public BlackHoleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BlackHoleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        SpellShaders.updateBlackHoleTime();
        
        poseStack.pushPose();
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        int size = 2;
        poseStack.translate(0, entity.getDimensions(entity.getPose()).height / 2, 0);
        renderBlackHole(poseStack, buffer, size);

        poseStack.popPose();
        
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
    
    public static void renderBlackHole(PoseStack poseStack, MultiBufferSource buffer, float size) {
        
        VertexConsumer vc = buffer.getBuffer(ModRenderTypes.BLACK_HOLE);
        
        Matrix4f mat = poseStack.last().pose();
		
		vc.vertex(mat, -size, -size, 0).endVertex();
        vc.vertex(mat, -size, size, 0).endVertex();
        vc.vertex(mat, size, size, 0).endVertex();
        vc.vertex(mat, size, -size, 0).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(BlackHoleEntity entity) {
        return null;
    }
}
