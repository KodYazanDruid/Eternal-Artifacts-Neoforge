package com.sonamorningstar.eternalartifacts.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.client.render.ModRenderTypes;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.SpellProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;

public class SpellProjectileRenderer extends EntityRenderer<SpellProjectile> {
	
	public SpellProjectileRenderer(EntityRendererProvider.Context pContext) {
		super(pContext);
	}
	
	@Override
	public void render(SpellProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack,
					   MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		
		poseStack.translate(0.0F, 0.5F, 0.0F);
		// Projektil boyutu
		float scale = 0.8f;
		poseStack.scale(scale, scale, scale);
		
		// Özel RenderType kullan
		//RenderType renderType = ModRenderTypes.SPELL_CLOUD.get();
		VertexConsumer vertexConsumer = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("block/sand"))
			.buffer(buffer, RenderType::entitySolid);
		
		drawSphere(vertexConsumer, poseStack.last().pose(),
			0.5f, 10, 10,
			1.0f, 0.5f, 0.0f, 0.7f);
		
		// Ana renk değerleri - shader kendi rengini uygulayacak ama
		// bu renk ile karışacak (vertexColor ile çarpılıyor)
		float red = 1.0f;    // Daha çok kırmızıya yakın
		float green = 0.6f;  // Orta seviye yeşil
		float blue = 0.2f;   // Düşük mavi (ateş efekti için)
		float alpha = 1.0f;  // Tam opaklık
		
		// Ana quad (shader billboard tekniği kullanıyor zaten)
		/*Matrix4f matrix = poseStack.last().pose();
		renderBillboard(matrix, vertexConsumer, red, green, blue, alpha);*/
		
		// Ekstra derinlik ve hacim için ikinci bir quad
		// 90 derece farklı açıda
		/*poseStack.pushPose();
		poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
		matrix = poseStack.last().pose();
		renderBillboard(matrix, vertexConsumer, red, green, blue, alpha * 0.8f);
		poseStack.popPose();*/
		
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}
	
	public static void drawSphere(VertexConsumer vertexConsumer, Matrix4f matrix,
								  float radius, int latBands, int lonBands,
								  float r, float g, float b, float a) {
		for (int lat = 0; lat <= latBands; lat++) {
			float theta1 = (float) (Math.PI * lat / latBands);
			float theta2 = (float) (Math.PI * (lat + 1) / latBands);
			
			for (int lon = 0; lon <= lonBands; lon++) {
				float phi = (float) (2 * Math.PI * lon / lonBands);
				
				// İlk nokta (theta1)
				float x1 = (float) (Math.sin(theta1) * Math.cos(phi));
				float y1 = (float) Math.cos(theta1);
				float z1 = (float) (Math.sin(theta1) * Math.sin(phi));
				
				// İkinci nokta (theta2)
				float x2 = (float) (Math.sin(theta2) * Math.cos(phi));
				float y2 = (float) Math.cos(theta2);
				float z2 = (float) (Math.sin(theta2) * Math.sin(phi));
				
				// Quad için 2 üçgen (x1,y1,z1)-(x2,y2,z2)-(x1',y1',z1')-(x2',y2',z2')
				float phiNext = (float) (2 * Math.PI * (lon + 1) / lonBands);
				
				float x1n = (float) (Math.sin(theta1) * Math.cos(phiNext));
				float y1n = (float) Math.cos(theta1);
				float z1n = (float) (Math.sin(theta1) * Math.sin(phiNext));
				
				float x2n = (float) (Math.sin(theta2) * Math.cos(phiNext));
				float y2n = (float) Math.cos(theta2);
				float z2n = (float) (Math.sin(theta2) * Math.sin(phiNext));
				
				// Üçgen 1
				vertexConsumer.vertex(matrix, radius * x1, radius * y1, radius * z1)
					.color(r, g, b, a)
					.uv(0, 0)
					.overlayCoords(OverlayTexture.NO_OVERLAY)
					.uv2(0xF000F0)
					.normal(1, 0, 0)
					.endVertex();
				vertexConsumer.vertex(matrix, radius * x2, radius * y2, radius * z2)
					.color(r, g, b, a)
					.uv(0, 0)
					.overlayCoords(OverlayTexture.NO_OVERLAY)
					.uv2(0xF000F0)
					.normal(1, 0, 0)
					.endVertex();
				vertexConsumer.vertex(matrix, radius * x1n, radius * y1n, radius * z1n)
					.color(r, g, b, a)
					.uv(0, 0)
					.overlayCoords(OverlayTexture.NO_OVERLAY)
					.uv2(0xF000F0)
					.normal(1, 0, 0)
					.endVertex();
				
				// Üçgen 2
				vertexConsumer.vertex(matrix, radius * x2, radius * y2, radius * z2)
					.color(r, g, b, a)
					.uv(0, 0)
					.overlayCoords(OverlayTexture.NO_OVERLAY)
					.uv2(0xF000F0)
					.normal(1, 0, 0)
					.endVertex();
				vertexConsumer.vertex(matrix, radius * x2n, radius * y2n, radius * z2n)
					.color(r, g, b, a)
					.uv(0, 0)
					.overlayCoords(OverlayTexture.NO_OVERLAY)
					.uv2(0xF000F0)
					.normal(1, 0, 0)
					.endVertex();
				vertexConsumer.vertex(matrix, radius * x1n, radius * y1n, radius * z1n)
					.color(r, g, b, a)
					.uv(0, 0)
					.overlayCoords(OverlayTexture.NO_OVERLAY)
					.uv2(0xF000F0)
					.normal(1, 0, 0)
					.endVertex();
			}
		}
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