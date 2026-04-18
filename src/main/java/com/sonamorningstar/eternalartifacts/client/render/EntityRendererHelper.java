package com.sonamorningstar.eternalartifacts.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.core.ModModelBakery;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.ModListUtils;
import com.sonamorningstar.eternalartifacts.util.StringUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class EntityRendererHelper {
	public static void renderEntityInGui(PoseStack poseStack, int x, int y, float scale, Entity entity) {
		poseStack.pushPose();
		
		poseStack.translate(x, y, 50);
		poseStack.scale(scale, scale, scale);
		poseStack.mulPose(Axis.XP.rotationDegrees(165));
		poseStack.mulPose(Axis.YP.rotationDegrees(135));
		
		EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		dispatcher.setRenderShadow(false);
		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		entity.setSecondsOnFire(0);
		RenderSystem.runAsFancy(() ->
			dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 0.0F,
				poseStack, buffer, LightTexture.FULL_BRIGHT)
		);
		buffer.endBatch();
		dispatcher.setRenderShadow(true);
		
		poseStack.popPose();
	}
	
	public static void renderTooltip(GuiGraphics gui, LivingEntity living, int mx, int my, boolean isAdvanced) {
		float health = living.getHealth();
		float maxHealth = living.getMaxHealth();
		List<Component> tooltips = new ArrayList<>();
		tooltips.add(living.getDisplayName());
		tooltips.add(Component.translatable(ModConstants.GUI.withSuffix("catalogue_health"),
			health, maxHealth));
		if (isAdvanced) {
			tooltips.add(Component.literal(BuiltInRegistries.ENTITY_TYPE.getKey(living.getType()).toString())
				.withStyle(ChatFormatting.DARK_GRAY));
			if (!living.getTags().isEmpty()) {
				tooltips.add(Component.translatable("item.nbt_tags", living.getTags().size())
					.withStyle(ChatFormatting.DARK_GRAY));
			}
		}
		StringUtils.appendModName(tooltips, ModListUtils.getEntityCreatorModId(living.getType()));
		gui.renderTooltip(Minecraft.getInstance().font, tooltips, Optional.empty(), mx, my);
	}
	
	public static void renderSoulFlame(PoseStack poseStack, MultiBufferSource buffer, Entity entity) {
		renderSoulFlame(poseStack, buffer, entity, Mth.rotationAroundAxis(Mth.Y_AXIS, Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation(), new Quaternionf()));
	}
	
	public static void renderSoulFlame(PoseStack poseStack, MultiBufferSource buffer, Entity entity, Quaternionf quaternion) {
		TextureAtlasSprite fire0 = ModModelBakery.SOUL_FIRE_0.sprite();
		TextureAtlasSprite fire1 = ModModelBakery.SOUL_FIRE_1.sprite();
		poseStack.pushPose();
		float f = entity.getBbWidth() * 1.4F;
		poseStack.scale(f, f, f);
		float f1 = 0.5F;
		float f2 = 0.0F;
		float f3 = entity.getBbHeight() / f;
		float f4 = 0.0F;
		poseStack.mulPose(quaternion);
		poseStack.translate(0.0F, 0.0F, -0.3F + (float)((int)f3) * 0.02F);
		float f5 = 0.0F;
		int i = 0;
		VertexConsumer consumer = buffer.getBuffer(Sheets.cutoutBlockSheet());
		
		for(PoseStack.Pose pose = poseStack.last(); f3 > 0.0F; ++i) {
			TextureAtlasSprite sprite = i % 2 == 0 ? fire0 : fire1;
			float f6 = sprite.getU0();
			float f7 = sprite.getV0();
			float f8 = sprite.getU1();
			float f9 = sprite.getV1();
			if (i / 2 % 2 == 0) {
				float f10 = f8;
				f8 = f6;
				f6 = f10;
			}
			
			fireVertex(pose, consumer, f1 - 0.0F, 0.0F - f4, f5, f8, f9);
			fireVertex(pose, consumer, -f1 - 0.0F, 0.0F - f4, f5, f6, f9);
			fireVertex(pose, consumer, -f1 - 0.0F, 1.4F - f4, f5, f6, f7);
			fireVertex(pose, consumer, f1 - 0.0F, 1.4F - f4, f5, f8, f7);
			f3 -= 0.45F;
			f4 -= 0.45F;
			f1 *= 0.9F;
			f5 += 0.03F;
		}
		
		poseStack.popPose();
	}
	
	private static void fireVertex(
		PoseStack.Pose matrixEntry, VertexConsumer buffer, float x, float y, float z, float texU, float texV
	) {
		buffer.vertex(matrixEntry.pose(), x, y, z)
			.color(255, 255, 255, 255)
			.uv(texU, texV)
			.overlayCoords(0, 10)
			.uv2(240)
			.normal(matrixEntry.normal(), 0.0F, 1.0F, 0.0F)
			.endVertex();
	}
}
