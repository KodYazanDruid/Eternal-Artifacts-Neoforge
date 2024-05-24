package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class CustomRenderButton extends Button {
    private ResourceLocation[] textures;

    public static CustomRenderButton.Builder builder(Component pMessage, CustomRenderButton.OnPress pOnPress, ResourceLocation... textures) {
        return new CustomRenderButton.Builder(pMessage, pOnPress, textures);
    }

    public static CustomRenderButton.Builder builderNoTexture(Component pMessage, CustomRenderButton.OnPress pOnPress) {
        return new CustomRenderButton.Builder(pMessage, pOnPress);
    }

    public CustomRenderButton(Builder builder) {
        this(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration, builder.textures);
        setTooltip(builder.tooltip);
    }

    private CustomRenderButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, CreateNarration pCreateNarration, ResourceLocation... textures) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pCreateNarration);
        this.textures = textures;
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        gui.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        /*if(isHovered())*/ gui.fillGradient(getX(), getY(), getX()+getWidth(), getY()+getHeight(), 0xb6cdf2, 0x262d38);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        for(ResourceLocation texture : textures) gui.blit(texture, getX(), getY(), 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void setTextures(ResourceLocation... textures) {
        this.textures = textures;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final Component message;
        private final CustomRenderButton.OnPress onPress;
        private ResourceLocation[] textures;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private CustomRenderButton.CreateNarration createNarration = CustomRenderButton.DEFAULT_NARRATION;

        public Builder(Component pMessage, CustomRenderButton.OnPress pOnPress, ResourceLocation... textures) {
            this.message = pMessage;
            this.onPress = pOnPress;
            this.textures = textures;
        }

        public Builder(Component pMessage, CustomRenderButton.OnPress pOnPress) {
            this.message = pMessage;
            this.onPress = pOnPress;
        }

        public CustomRenderButton.Builder pos(int pX, int pY) {
            this.x = pX;
            this.y = pY;
            return this;
        }

        public CustomRenderButton.Builder width(int pWidth) {
            this.width = pWidth;
            return this;
        }

        public CustomRenderButton.Builder size(int pWidth, int pHeight) {
            this.width = pWidth;
            this.height = pHeight;
            return this;
        }

        public CustomRenderButton.Builder bounds(int pX, int pY, int pWidth, int pHeight) {
            return this.pos(pX, pY).size(pWidth, pHeight);
        }

        public CustomRenderButton.Builder tooltip(@Nullable Tooltip pTooltip) {
            this.tooltip = pTooltip;
            return this;
        }

        public CustomRenderButton.Builder createNarration(CustomRenderButton.CreateNarration pCreateNarration) {
            this.createNarration = pCreateNarration;
            return this;
        }

        public CustomRenderButton build() {
            return build(CustomRenderButton::new);
        }

        public CustomRenderButton build(java.util.function.Function<CustomRenderButton.Builder, CustomRenderButton> builder) {
            return builder.apply(this);
        }
    }
}
