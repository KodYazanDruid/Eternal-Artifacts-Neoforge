package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMultiblockScreen;
import com.sonamorningstar.eternalartifacts.container.MultiblockEnergyHatchMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MultiblockEnergyHatchScreen extends AbstractMultiblockScreen<MultiblockEnergyHatchMenu> {
    public MultiblockEnergyHatchScreen(MultiblockEnergyHatchMenu menu, Inventory pPlayerInventory, Component pTitle) {
        super(menu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        
        AbstractMultiblockBlockEntity mbe = (AbstractMultiblockBlockEntity) menu.getBlockEntity();
        if (mbe.hasHatchEnergy() && menu.getBeEnergy() != null) {
            renderEnergyBar(guiGraphics, leftPos + mbe.getHatchEnergyX(), topPos + mbe.getHatchEnergyY());
        }
    }
}
