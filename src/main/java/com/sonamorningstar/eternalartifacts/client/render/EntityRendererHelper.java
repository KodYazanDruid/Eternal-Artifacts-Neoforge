package com.sonamorningstar.eternalartifacts.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.ModListUtils;
import com.sonamorningstar.eternalartifacts.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

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
		TooltipHelper.appendModName(tooltips, ModListUtils.getEntityCreatorModId(living.getType()));
		gui.renderTooltip(Minecraft.getInstance().font, tooltips, Optional.empty(), mx, my);
	}
}
