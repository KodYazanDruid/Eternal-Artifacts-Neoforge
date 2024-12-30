package com.sonamorningstar.eternalartifacts.client.gui.widget.records;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ButtonDrawContent {
    @Getter
    private final List<SpriteInfo> spriteInfos = new ArrayList<>();
    @Getter
    private final Multimap<Integer, ButtonBoundInfo> uvMap = ArrayListMultimap.create();

    @Setter
    private int buttonWidth;
    @Setter
    private int buttonHeight;

    public ButtonDrawContent(int buttonWidth, int buttonHeight) {
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
    }

    public void addSprite(ResourceLocation spriteLoc, int width, int height, SpriteStyle style) {
        /*SpriteInfo info = new SpriteInfo(spriteLoc, width, height, style);
        spriteInfos.add(info);
        uvMap.put(spriteInfos.indexOf(info), new ButtonBoundInfo(0, 0, buttonWidth, buttonHeight));*/
        addSprite(spriteLoc, 0, 0, width, height, style);
    }

    public void addSprite(ResourceLocation spriteLoc, int u, int v, int width, int height, SpriteStyle style) {
        SpriteInfo info = new SpriteInfo(spriteLoc, width, height, style);
        spriteInfos.add(info);
        uvMap.put(spriteInfos.indexOf(info), new ButtonBoundInfo(u, v, buttonWidth, buttonHeight));
    }

    public void addBlitSprite(ResourceLocation spriteLoc, int spriteWidth, int spriteHeight, int u, int v, int width, int height) {
        SpriteInfo info = new SpriteInfo(spriteLoc, spriteWidth, spriteHeight, null);
        if (!spriteInfos.contains(info)) {
            spriteInfos.add(info);
        }
        uvMap.put(spriteInfos.indexOf(info), new ButtonBoundInfo(u, v, width, height));
    }

    public record ButtonBoundInfo(int u, int v, int width, int height) {}
    public record SpriteInfo(ResourceLocation sprite, int width, int height, SpriteStyle style) {}
    public enum SpriteStyle {
        SINGLE,
        TILE,
        SCALE
    }
}
