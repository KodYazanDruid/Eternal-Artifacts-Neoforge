package com.sonamorningstar.eternalartifacts.client.gui.overlay;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

@Setter
public abstract class ModGuiOverlay implements IGuiOverlay {
    protected static final Minecraft mc = Minecraft.getInstance();
    protected int width;
    protected int height;
    protected int strWidth;
    protected int strHeight;
    
    public ModGuiOverlay(int width, int height) {
        this.width = width;
        this.height = height;
    }

    protected void renderBlankBlack(GuiGraphics guiGraphics, int x, int y, int width, int height, float alpha) {
        guiGraphics.fill(x, y, x + width, y + height, 0x1F000000);
    }

    protected boolean shouldReturn(Minecraft mc) {
        Player player = mc.player;
        return player == null || player.isSpectator() || mc.getDebugOverlay().showDebugScreen();
    }

    protected int getComponentWidth(Component component) {
        return mc.font.width(component);
    }
    
    public int getWidth() {
        return width + strWidth;
    }
    
    public int getHeight() {
        return height + strHeight;
    }
}
