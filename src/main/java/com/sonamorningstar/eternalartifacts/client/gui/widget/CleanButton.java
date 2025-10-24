package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class CleanButton extends AbstractButton {
	public static final int SMALL_WIDTH = 120;
	public static final int DEFAULT_WIDTH = 150;
	public static final int DEFAULT_HEIGHT = 20;
	public static final int DEFAULT_SPACING = 8;
	protected static final CleanButton.CreateNarration DEFAULT_NARRATION = Supplier::get;
	protected final CleanButton.OnPress onPress;
	protected final CleanButton.CreateNarration createNarration;
	protected static final WidgetSprites CLEAN_SPRITES = new WidgetSprites(
		new ResourceLocation(MODID, "widget/clean_button"),
		new ResourceLocation(MODID, "widget/clean_button_disabled"),
		new ResourceLocation(MODID, "widget/clean_button_highlighted")
	);
	protected CleanButton(CleanButton.Builder builder) {
		this(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration);
		setTooltip(builder.tooltip);
	}
	protected CleanButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, CreateNarration pCreateNarration) {
		super(pX, pY, pWidth, pHeight, pMessage);
		this.onPress = pOnPress;
		this.createNarration = pCreateNarration;
	}
	
	public static CleanButton.Builder builder(Component pMessage, CleanButton.OnPress pOnPress) {
		return new CleanButton.Builder(pMessage, pOnPress);
	}
	
	@Override
	public void onPress() {
		if (this.onPress != null) {
			this.onPress.onPress(this);
		}
	}
	
	@Override
	protected MutableComponent createNarrationMessage() {
		return this.createNarration.createNarrationMessage(super::createNarrationMessage);
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		Minecraft minecraft = Minecraft.getInstance();
		pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		pGuiGraphics.blitSprite(CLEAN_SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
		pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		int i = getFGColor();
		this.renderString(pGuiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
		defaultButtonNarrationText(pNarrationElementOutput);
	}
	
	public static class Builder {
		private final Component message;
		private final CleanButton.OnPress onPress;
		@Nullable
		private Tooltip tooltip;
		private int x;
		private int y;
		private int width = 150;
		private int height = 20;
		private CleanButton.CreateNarration createNarration = CleanButton.DEFAULT_NARRATION;
		
		public Builder(Component message, CleanButton.OnPress press) {
			this.message = message;
			this.onPress = press;
		}
		
		public CleanButton.Builder pos(int pX, int pY) {
			this.x = pX;
			this.y = pY;
			return this;
		}
		
		public CleanButton.Builder width(int pWidth) {
			this.width = pWidth;
			return this;
		}
		
		public CleanButton.Builder size(int pWidth, int pHeight) {
			this.width = pWidth;
			this.height = pHeight;
			return this;
		}
		
		public CleanButton.Builder bounds(int pX, int pY, int pWidth, int pHeight) {
			return this.pos(pX, pY).size(pWidth, pHeight);
		}
		
		public CleanButton.Builder tooltip(@Nullable Tooltip pTooltip) {
			this.tooltip = pTooltip;
			return this;
		}
		
		public CleanButton.Builder createNarration(CleanButton.CreateNarration pCreateNarration) {
			this.createNarration = pCreateNarration;
			return this;
		}
		
		public CleanButton build() {
			return new CleanButton(this);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public interface CreateNarration {
		MutableComponent createNarrationMessage(Supplier<MutableComponent> pMessageSupplier);
	}
	
	@OnlyIn(Dist.CLIENT)
	public interface OnPress {
		void onPress(CleanButton pButton);
	}
}
