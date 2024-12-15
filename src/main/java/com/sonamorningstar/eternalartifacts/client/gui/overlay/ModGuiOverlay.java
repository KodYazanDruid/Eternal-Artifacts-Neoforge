package com.sonamorningstar.eternalartifacts.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public abstract class ModGuiOverlay implements IGuiOverlay {
    protected static final ResourceLocation BLANK_BLACK = new ResourceLocation(MODID, "textures/gui/blank_black.png");
    protected static final Minecraft mc = Minecraft.getInstance();

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
}
