package com.sonamorningstar.eternalartifacts.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.client.render.ModRenderTypes;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.SpellProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class SpellProjectileRenderer extends EntityRenderer<SpellProjectile> {
	
	public SpellProjectileRenderer(EntityRendererProvider.Context pContext) {
		super(pContext);
	}
	
	@Override
	public void render(SpellProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack,
					   MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		
		// Projektil boyutu
		float scale = 0.8f;
		poseStack.scale(scale, scale, scale);
		
		// Özel RenderType kullan
		RenderType renderType = ModRenderTypes.SPELL_CLOUD.get();
		VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
		
		// Ana renk değerleri - shader kendi rengini uygulayacak ama
		// bu renk ile karışacak (vertexColor ile çarpılıyor)
		float red = 1.0f;    // Daha çok kırmızıya yakın
		float green = 0.6f;  // Orta seviye yeşil
		float blue = 0.2f;   // Düşük mavi (ateş efekti için)
		float alpha = 1.0f;  // Tam opaklık
		
		// Ana quad (shader billboard tekniği kullanıyor zaten)
		Matrix4f matrix = poseStack.last().pose();
		renderBillboard(matrix, vertexConsumer, red, green, blue, alpha);
		
		// Ekstra derinlik ve hacim için ikinci bir quad
		// 90 derece farklı açıda
		poseStack.pushPose();
		poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
		matrix = poseStack.last().pose();
		renderBillboard(matrix, vertexConsumer, red, green, blue, alpha * 0.8f);
		poseStack.popPose();
		
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}
	
	// Billboard quad oluşturma (shader her zaman kameraya bakacak)
	private void renderBillboard(Matrix4f matrix, VertexConsumer vertexConsumer,
								 float r, float g, float b, float a) {
		float size = 1.0f;
		
		// Texture koordinatları doğru olacak şekilde quad oluştur
		vertexConsumer.vertex(matrix, -size, -size, 0)
			.color(r, g, b, a)
			.endVertex();
		vertexConsumer.vertex(matrix, size, -size, 0)
			.color(r, g, b, a)
			.endVertex();
		vertexConsumer.vertex(matrix, size, size, 0)
			.color(r, g, b, a)
			.endVertex();
		vertexConsumer.vertex(matrix, -size, size, 0)
			.color(r, g, b, a)
			.endVertex();
	}
	
	@Override
	public ResourceLocation getTextureLocation(SpellProjectile pEntity) {
		return null; // Shader tarafından yönetildiği için texture gerekmez
	}
}