package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.base.ParentalWidget;
import com.sonamorningstar.eternalartifacts.client.gui.widget.base.TooltipRenderable;
import com.sonamorningstar.eternalartifacts.client.gui.widget.records.ButtonDrawContent;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
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

public class SpriteButton extends AbstractButton implements TooltipRenderable {
    private ResourceLocation[] textures;
    @Setter
    private ButtonDrawContent sprites;
    private final List<Component> tooltips;
    private final List<Supplier<Component>> dynamicTooltips;
    protected static final SpriteButton.CreateNarration DEFAULT_NARRATION = Supplier::get;
    protected final SpriteButton.OnPress onPress;
    protected final SpriteButton.CreateNarration createNarration;

    public static SpriteButton.Builder builder(Component pMessage, SpriteButton.OnPress pOnPress, ResourceLocation... textures) {
        return new SpriteButton.Builder(pMessage, pOnPress, textures);
    }

    public static SpriteButton.Builder builderNoTexture(Component pMessage, SpriteButton.OnPress pOnPress) {
        return new SpriteButton.Builder(pMessage, pOnPress);
    }

    public SpriteButton(Builder builder) {
        this(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration, builder.sprites,
            builder.tooltips, builder.dynamicTooltips, builder.textures);
        setTooltip(builder.tooltip);
    }

    private SpriteButton(int x, int y, int width, int hegiht, Component pMessage, SpriteButton.OnPress pOnPress, CreateNarration pCreateNarration, ButtonDrawContent sprites,
                         List<Component> tooltips, List<Supplier<Component>> dynamicTooltips, ResourceLocation... textures) {
        super(x, y, width, hegiht, pMessage);
        this.onPress = pOnPress;
        this.createNarration = pCreateNarration;
        this.textures = textures;
        this.sprites = sprites;
        this.tooltips = tooltips;
        this.dynamicTooltips = dynamicTooltips;
    }

    @Override
    public void onPress() {}

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        gui.setColor(1.0F, 1.0F, 1.0F, this.alpha);
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

                    if (style != SpriteStyle.SINGLE) {
                        gui.blit(sprite.sprite(), getX(), getY(), getWidth(), getHeight(),
                                info.u(), info.v(), info.width(), info.height(),
                                width, height);
                    }
                }

            }
        
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    @Override
    public void renderTooltip(GuiGraphics gui, int mouseX, int mouseY, int tooltipZ) {
        List<Component> allTooltips = new ArrayList<>();
        allTooltips.addAll(tooltips);
        allTooltips.addAll(dynamicTooltips.stream().map(Supplier::get).toList());
        if (isMouseOver(mouseX, mouseY) && !allTooltips.isEmpty()) {
            gui.pose().pushPose();
            gui.pose().translate(0, 0, tooltipZ);
            RenderSystem.disableDepthTest();
            gui.renderTooltip(Minecraft.getInstance().font, allTooltips, Optional.empty(), mouseX, mouseY);
            RenderSystem.enableDepthTest();
            gui.pose().popPose();
        }
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
    public boolean isMouseOver(double mX, double mY) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AbstractModContainerScreen<?> modScreen) {
            for (int i = modScreen.upperLayerChildren.size() - 1; i >= 0; i--) {
                GuiEventListener child = modScreen.upperLayerChildren.get(i);
                if (child instanceof ParentalWidget parental) {
                    if (child instanceof SimpleDraggablePanel panel && panel.isMouseOverRaw(mX, mY)) {
                        return parental.getChildUnderCursorRaw(mX, mY) == this;
                    }
                }
            }
        }
        return super.isMouseOver(mX, mY);
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
        private final SpriteButton.OnPress onPress;
        private ResourceLocation[] textures;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private final ButtonDrawContent sprites = new ButtonDrawContent(width, height);
        private final List<Component> tooltips = new ArrayList<>();
        private final List<Supplier<Component>> dynamicTooltips = new ArrayList<>();

        private SpriteButton.CreateNarration createNarration = SpriteButton.DEFAULT_NARRATION;

        public Builder(Component pMessage, SpriteButton.OnPress pOnPress, ResourceLocation... textures) {
            this.message = pMessage;
            this.onPress = pOnPress;
            this.textures = textures;
        }
        public Builder(Component pMessage, SpriteButton.OnPress pOnPress) {
            this.message = pMessage;
            this.onPress = pOnPress;
        }

        //region Position and size
        public SpriteButton.Builder pos(int pX, int pY) {
            this.x = pX;
            this.y = pY;
            return this;
        }
        public SpriteButton.Builder width(int width) {
            this.width = width;
            sprites.setButtonWidth(width);
            return this;
        }
        public SpriteButton.Builder height(int height) {
            this.height = height;
            sprites.setButtonHeight(height);
            return this;
        }
        public SpriteButton.Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            sprites.setButtonWidth(width);
            sprites.setButtonHeight(height);
            return this;
        }
        public SpriteButton.Builder bounds(int pX, int pY, int pWidth, int pHeight) {
            return this.pos(pX, pY).size(pWidth, pHeight);
        }
        //endregion
        //region Misc button stuff
        public SpriteButton.Builder tooltip(@Nullable Tooltip pTooltip) {
            this.tooltip = pTooltip;
            return this;
        }
        public SpriteButton.Builder createNarration(SpriteButton.CreateNarration pCreateNarration) {
            this.createNarration = pCreateNarration;
            return this;
        }
        //endregion
        //region Sprite adding.
        public SpriteButton.Builder addSprite(ResourceLocation sprite) {
            return addSprite(sprite, 256, 256, SpriteStyle.SCALE);
        }
        public SpriteButton.Builder addSprite(ResourceLocation sprite, SpriteStyle style) {
            return addSprite(sprite, 256, 256, style);
        }
        public SpriteButton.Builder addSprite(ResourceLocation sprite, int width, int height, SpriteStyle style) {
            sprites.addSprite(sprite, width, height, style);
            return this;
        }
        public SpriteButton.Builder addSprite(ResourceLocation sprite, int u, int v, int width, int height) {
            return addSprite(sprite, 256, 256, u, v, width, height);
        }
        public SpriteButton.Builder addSprite(ResourceLocation sprite, int spriteWidth, int spriteHeight, int u, int v, int width, int height) {
            sprites.addBlitSprite(sprite, spriteWidth, spriteHeight, u, v, width, height);
            return this;
        }
        //endregion
        //region Component adding.
        public SpriteButton.Builder addTooltipHover(Component tooltip) {
            tooltips.add(tooltip);
            return this;
        }
        public SpriteButton.Builder addTooltipHover(Supplier<Component> tooltip) {
            dynamicTooltips.add(tooltip);
            return this;
        }
        //endregion

        public SpriteButton build() {
            return build(SpriteButton::new);
        }

        public SpriteButton build(java.util.function.Function<SpriteButton.Builder, SpriteButton> builder) {
            return builder.apply(this);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface CreateNarration {
        MutableComponent createNarrationMessage(Supplier<MutableComponent> pMessageSupplier);
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnPress {
        void onPress(SpriteButton button, int key);
    }
}
