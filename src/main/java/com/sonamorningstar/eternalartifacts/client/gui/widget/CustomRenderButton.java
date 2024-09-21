package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.client.gui.widget.records.ButtonDrawContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Minecart;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.client.gui.widget.records.ButtonDrawContent.ButtonBoundInfo;
import static com.sonamorningstar.eternalartifacts.client.gui.widget.records.ButtonDrawContent.SpriteStyle;
import static com.sonamorningstar.eternalartifacts.client.gui.widget.records.ButtonDrawContent.SpriteInfo;

public class CustomRenderButton extends AbstractButton {
    private ResourceLocation[] textures;
    private final ButtonDrawContent sprites;
    private final List<Component> tooltips;
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
        this(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration, builder.sprites, builder.tooltips, builder.textures);
        setTooltip(builder.tooltip);
    }

    private CustomRenderButton(int x, int y, int width, int hegiht, Component pMessage, CustomRenderButton.OnPress pOnPress, CreateNarration pCreateNarration, ButtonDrawContent sprites, List<Component> tooltips, ResourceLocation... textures) {
        super(x, y, width, hegiht, pMessage);
        this.onPress = pOnPress;
        this.createNarration = pCreateNarration;
        this.textures = textures;
        this.sprites = sprites;
        this.tooltips = tooltips;
    }

    @Override
    public void onPress() {}

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        gui.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        if(textures != null)
            for(ResourceLocation texture : textures)
                gui.blit(texture, getX(), getY(), 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
        if (sprites != null)
            for (int i = 0; i < sprites.getSpriteInfos().size(); i++) {
                SpriteInfo sprite = sprites.getSpriteInfos().get(i);
                Collection<ButtonBoundInfo> infos = sprites.getUvMap().get(i);
                for (ButtonBoundInfo info : infos) {
                    int width = sprite.width();
                    int height = sprite.height();
                    SpriteStyle style = sprite.style();
                    if (style != null){
                        switch (style) {
                            case SINGLE -> {
                                int singleWidth = Math.min(sprite.width(), getWidth());
                                int singleHeight = Math.min(sprite.height(), getHeight());
                                gui.blit(sprite.sprite(), getX(), getY(), info.u(), info.v(), singleWidth, singleHeight, sprite.width(), sprite.height());
                            }
                            case SCALE -> {
                                width = getWidth();
                                height = getHeight();
                            }
                        }
                    }

                    if (style != SpriteStyle.SINGLE){
                        gui.blit(sprite.sprite(), getX(), getY(), getWidth(), getHeight(),
                                info.u(), info.v(), info.width(), info.height(),
                                width, height);
                    }
                }

            }

        if (isMouseOver(mouseX, mouseY) && !tooltips.isEmpty())
            gui.renderTooltip(Minecraft.getInstance().font, tooltips, Optional.empty(), mouseX, mouseY);
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
        private final ButtonDrawContent sprites = new ButtonDrawContent(width, height);
        private final List<Component> tooltips = new ArrayList<>();

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

        //region Position and size
        public CustomRenderButton.Builder pos(int pX, int pY) {
            this.x = pX;
            this.y = pY;
            return this;
        }
        public CustomRenderButton.Builder width(int width) {
            this.width = width;
            sprites.setButtonWidth(width);
            return this;
        }
        public CustomRenderButton.Builder height(int height) {
            this.height = height;
            sprites.setButtonHeight(height);
            return this;
        }
        public CustomRenderButton.Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            sprites.setButtonWidth(width);
            sprites.setButtonHeight(height);
            return this;
        }
        public CustomRenderButton.Builder bounds(int pX, int pY, int pWidth, int pHeight) {
            return this.pos(pX, pY).size(pWidth, pHeight);
        }
        //endregion
        //region Misc button stuff
        public CustomRenderButton.Builder tooltip(@Nullable Tooltip pTooltip) {
            this.tooltip = pTooltip;
            return this;
        }
        public CustomRenderButton.Builder createNarration(CustomRenderButton.CreateNarration pCreateNarration) {
            this.createNarration = pCreateNarration;
            return this;
        }
        //endregion
        //region Sprite adding.
        public CustomRenderButton.Builder addSprite(ResourceLocation sprite) {
            return addSprite(sprite, 256, 256, SpriteStyle.SCALE);
        }
        public CustomRenderButton.Builder addSprite(ResourceLocation sprite, SpriteStyle style) {
            return addSprite(sprite, 256, 256, style);
        }
        public CustomRenderButton.Builder addSprite(ResourceLocation sprite, int width, int height, SpriteStyle style) {
            sprites.addSprite(sprite, width, height, style);
            return this;
        }
        public CustomRenderButton.Builder addSprite(ResourceLocation sprite, int u, int v, int width, int height) {
            return addSprite(sprite, 256, 256, u, v, width, height);
        }
        public CustomRenderButton.Builder addSprite(ResourceLocation sprite, int spriteWidth, int spriteHeight, int u, int v, int width, int height) {
            sprites.addBlitSprite(sprite, spriteWidth, spriteHeight, u, v, width, height);
            return this;
        }
        //endregion
        //region Component adding.
        public CustomRenderButton.Builder addTooltipHover(Component tooltip) {
            tooltips.add(tooltip);
            return this;
        }
        //endregion

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
