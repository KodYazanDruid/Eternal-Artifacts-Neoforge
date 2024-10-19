package com.sonamorningstar.eternalartifacts.compat.jei;

import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import lombok.Getter;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;

@Getter
public class SimpleBackgroundDrawable implements IDrawable {
    private final int width;
    private final int height;

    public SimpleBackgroundDrawable(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOff, int yOff) {
        GuiDrawer.drawBackground(guiGraphics, xOff, yOff, getWidth(), getHeight());
    }
}
