package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMachineScreen;
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
        renderDefaultEnergyAndFluidBar(gui);
        renderProgressArrow(gui, x + 104, y + 49, mx, my);
        renderLArraow(gui, x + 43, y + 45);
        IItemHandler inventory = menu.getBlockEntity().getLevel().getCapability(Capabilities.ItemHandler.BLOCK, menu.getBlockEntity().getBlockPos(), null);

        if(inventory != null && inventory.getStackInSlot(2).isEmpty())
        //Cycle between stack and stack and quill.
        ItemRendererHelper.renderFakeItemTransparent(gui.pose(), Items.BOOK.getDefaultInstance(), x + 80, y + 26, 96);

    }
}
