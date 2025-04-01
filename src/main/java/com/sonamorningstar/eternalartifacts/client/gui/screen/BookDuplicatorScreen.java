package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.BookDuplicatorMenu;
import com.sonamorningstar.eternalartifacts.util.ItemRendererHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class BookDuplicatorScreen extends AbstractSidedMachineScreen<BookDuplicatorMenu> {

    public BookDuplicatorScreen(BookDuplicatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float pPartialTick, int mx, int my) {
        super.renderBg(gui, pPartialTick, mx, my);
        IItemHandler inventory = menu.getBlockEntity().getLevel().getCapability(Capabilities.ItemHandler.BLOCK, menu.getBlockEntity().getBlockPos(), null);

        if(inventory != null && inventory.getStackInSlot(2).isEmpty())
        //Cycle between stack and stack and quill.
            ItemRendererHelper.renderFakeItemTransparent(gui, Items.BOOK.getDefaultInstance(), leftPos + 80, topPos + 26, 96);

    }
    
    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
        renderDefaultEnergyAndFluidBar(gui);
        renderProgressArrow(gui, leftPos + 104, topPos + 49, mx, my);
        renderLArraow(gui, leftPos + 43, topPos + 45);
    }
}
