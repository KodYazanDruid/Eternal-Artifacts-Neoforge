package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CustomRenderButton extends AbstractButton {
    private ResourceLocation[] textures;
    protected static final CustomRenderButton.CreateNarration DEFAULT_NARRATION = Supplier::get;
    protected final CustomRenderButton.OnPress onPress;
    protected final CustomRenderButton.CreateNarration createNarration;

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

    private CustomRenderButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, CustomRenderButton.OnPress pOnPress, CreateNarration pCreateNarration, ResourceLocation... textures) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.onPress = pOnPress;
        this.createNarration = pCreateNarration;
        this.textures = textures;
    }

    @Override
    public void onPress() {}

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        gui.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        /*if(isHovered())*/ gui.fillGradient(getX(), getY(), getX()+getWidth(), getY()+getHeight(), 0xb6cdf2, 0x262d38);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        if(textures != null &&textures.length > 0)
            for(ResourceLocation texture : textures)
                gui.blit(texture, getX(), getY(), 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void setTextures(ResourceLocation... textures) {
        this.textures = textures;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        super.onClick(mouseX, mouseY, button);
        this.onPress.onPress(this, button);
    }


    @Override
    protected boolean isValidClickButton(int button) {
        return button == 0 || button == 1 || button == 2;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

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

    @OnlyIn(Dist.CLIENT)
    public interface CreateNarration {
        MutableComponent createNarrationMessage(Supplier<MutableComponent> pMessageSupplier);
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnPress {
        void onPress(CustomRenderButton button, int key);
    }
}
