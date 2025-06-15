package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.BookDuplicatorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BookDuplicatorScreen extends AbstractSidedMachineScreen<BookDuplicatorMenu> {

    public BookDuplicatorScreen(BookDuplicatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    
    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
        renderDefaultEnergyAndFluidBar(gui);
        renderProgressArrow(gui, leftPos + 104, topPos + 49, mx, my);
        renderLArrow(gui, leftPos + 43, topPos + 45);
    }
}
