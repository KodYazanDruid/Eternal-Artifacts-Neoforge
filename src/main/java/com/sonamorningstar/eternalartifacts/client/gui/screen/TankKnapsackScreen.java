package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.container.TankKnapsackMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class TankKnapsackScreen extends AbstractModContainerScreen<TankKnapsackMenu> {
    public TankKnapsackScreen(TankKnapsackMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        int knapsackSize = menu.fluidSlots.size();
        int rowSize = 4;
        rowSize += Mth.ceil((float) knapsackSize / menu.column);
        setImageSize((Math.max(menu.column, 9) * 18) + 14, (rowSize * 18) + 40);
        inventoryLabelY = imageHeight - 92;
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
        renderTooltip(gui, mx, my);
    }
    
}
