package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMultiblockScreen;
import com.sonamorningstar.eternalartifacts.container.MultiblockFluidHatchMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MultiblockFluidHatchScreen extends AbstractMultiblockScreen<MultiblockFluidHatchMenu> {
    public MultiblockFluidHatchScreen(MultiblockFluidHatchMenu menu, Inventory pPlayerInventory, Component pTitle) {
        super(menu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        
        AbstractMultiblockBlockEntity mbe = (AbstractMultiblockBlockEntity) menu.getBlockEntity();
        if (menu.getBeTank() != null) {
            int[] tanks = mbe.getHatchFluidTanks();
            int startX = mbe.getHatchFluidStartX();
            int startY = mbe.getHatchFluidStartY();
            int spacing = mbe.getHatchFluidSpacing();
            
            if (tanks != null) {
                for (int i = 0; i < tanks.length; i++) {
                    renderFluidBar(guiGraphics, leftPos + startX + (i * spacing), topPos + startY, tanks[i]);
                }
            } else {
                for (int i = 0; i < menu.getBeTank().getTanks(); i++) {
                    renderFluidBar(guiGraphics, leftPos + startX + (i * spacing), topPos + startY, i);
                }
            }
        }
    }
}
