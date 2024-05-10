package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.container.BookDuplicatorMenu;
import com.sonamorningstar.eternalartifacts.util.ItemRendererHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BookDuplicatorScreen extends AbstractMachineScreen<BookDuplicatorMenu> {

    public BookDuplicatorScreen(BookDuplicatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        setTexture(new ResourceLocation(MODID, "textures/gui/book_duplicator.png"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        renderEnergyBar(pGuiGraphics, x + 5, y + 20);
        renderProgressArrow(pGuiGraphics, x + 104, y + 49);
        IFluidHandler tank = menu.getBlockEntity().getLevel().getCapability(Capabilities.FluidHandler.BLOCK, menu.getBlockEntity().getBlockPos(), null);
        if(tank != null) renderFluidBar(pGuiGraphics, x + 24, y + 20, tank.getFluidInTank(0));
        IItemHandler inventory = menu.getBlockEntity().getLevel().getCapability(Capabilities.ItemHandler.BLOCK, menu.getBlockEntity().getBlockPos(), null);

        if(inventory != null && inventory.getStackInSlot(2).isEmpty())
        //Cycle between book and book and quill.
        ItemRendererHelper.renderFakeItemTransparent(pGuiGraphics.pose(), Items.BOOK.getDefaultInstance(), x + 80, y + 26, 96);

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
}
