package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.sonamorningstar.eternalartifacts.client.gui.widget.base.AbstractBaseWidget;
import com.sonamorningstar.eternalartifacts.client.render.EntityRendererHelper;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class EntityCapsuleWidget extends AbstractBaseWidget {
	@Getter
	private final Entity entity;
	private final float scale;
	private boolean shouldRenderTooltip = false;
	
	public EntityCapsuleWidget(int x, int y, int width, int height,
							   @NotNull Entity entity, float scale, boolean shouldRenderTooltip) {
		super(x, y, width, height, Component.empty());
		this.entity = entity;
		this.scale = scale;
		this.shouldRenderTooltip = shouldRenderTooltip;
	}
	
	@Override
	protected void renderWidget(GuiGraphics gui, int mx, int my, float delta) {
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		
		int backgroundColor = 0xFF2D6B42;
		int highlightColor = 0xFF3FA45E;
		gui.fill(getX(), getY(), getX() + width, getY() + height, isFocused() ? highlightColor : backgroundColor);
		
		EntityRendererHelper.renderEntityInGui(gui.pose(), getX() + width / 2, getY() + height - 20, scale, entity);
		
		Component name = entity.getName();
		
		renderScrollingString(gui, font, Component.literal(name.getString()),
			getX() + 2, getY() + getHeight() - 15,  getX() + getWidth() - 2, getY() + getHeight() - 5, 0xFFFFFFFF);
		
		if (entity instanceof LivingEntity living) {
			float health = living.getHealth();
			float maxHealth = living.getMaxHealth();
			int barWidth = width - 4;
			int filled = (int) (barWidth * (health / maxHealth));
			
			gui.fill(getX() + 2, getY() + height - 5,
				getX() + 2 + barWidth, getY() + height - 3, 0xFF555555);
			gui.fill(getX() + 2, getY() + height - 5,
				getX() + 2 + filled, getY() + height - 3, 0xFFAA0000);
		}
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput narration) {
		this.defaultButtonNarrationText(narration);
	}
}
