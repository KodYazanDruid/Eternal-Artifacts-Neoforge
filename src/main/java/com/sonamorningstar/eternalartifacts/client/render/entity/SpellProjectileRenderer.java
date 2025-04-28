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
import net.minecraft.world.phys.Vec3;
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
		float scale = 0.5f;
		poseStack.scale(scale, scale, scale);
		
		// Kamera yönünü takip et ama tamamen billboard olmasın
		Vec3 cameraPos = this.entityRenderDispatcher.camera.getPosition();
		Vec3 entityPos = entity.position();
		
		// Özel RenderType kullan
		RenderType renderType = ModRenderTypes.SPELL_CLOUD.get();
		VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
		
		// Renk değerleri
		float red = 0.3f;
		float green = 0.3f;
		float blue = 1.0f;
		float alpha = 0.8f;
		
		// Quad boyutu
		float size = 1.0f;
		
		// Matris oluştur
		Matrix4f matrix = poseStack.last().pose();
		
		// Birden fazla açıyla quad'ları yerleştir (3D bulut efekti)
		// 1. X-Y düzlemi
		renderQuad(matrix, vertexConsumer, size, 0f, red, green, blue, alpha);
		
		// 2. X-Z düzlemi
		poseStack.pushPose();
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
		matrix = poseStack.last().pose();
		renderQuad(matrix, vertexConsumer, size, 0.2f, red, green, blue, alpha);
		poseStack.popPose();
		
		// 3. Y-Z düzlemi
		poseStack.pushPose();
		poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
		matrix = poseStack.last().pose();
		renderQuad(matrix, vertexConsumer, size, 0.1f, red, green, blue, alpha);
		poseStack.popPose();
		
		// 4-6. Ekstra düzlemler - daha hacimli görünüm için
		for (int i = 0; i < 3; i++) {
			poseStack.pushPose();
			poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(45 + i * 30));
			poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(45 + i * 30));
			matrix = poseStack.last().pose();
			renderQuad(matrix, vertexConsumer, size * 0.8f, 0.15f, red, green, blue, alpha * 0.8f);
			poseStack.popPose();
		}
		
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}
	
	// Bir quad render etmek için yardımcı metod
	private void renderQuad(Matrix4f matrix, VertexConsumer vertexConsumer, float size, float zOffset,
							float r, float g, float b, float a) {
		// Quad'ı vertex buffer'a ekle
		addVertex(matrix, vertexConsumer, -size, -size, zOffset, r, g, b, a);
		addVertex(matrix, vertexConsumer, size, -size, zOffset, r, g, b, a);
		addVertex(matrix, vertexConsumer, size, size, zOffset, r, g, b, a);
		addVertex(matrix, vertexConsumer, -size, size, zOffset, r, g, b, a);
	}
	
	// Vertex ekleme yardımcı metodu
	private void addVertex(Matrix4f matrix, VertexConsumer builder, float x, float y, float z,
						   float r, float g, float b, float a) {
		builder.vertex(matrix, x, y, z)
			.color(r, g, b, a)
			.endVertex();
	}
	
	@Override
	public ResourceLocation getTextureLocation(SpellProjectile pEntity) {
		return null; // Shader tarafından yönetildiği için texture gerekmez
	}
}