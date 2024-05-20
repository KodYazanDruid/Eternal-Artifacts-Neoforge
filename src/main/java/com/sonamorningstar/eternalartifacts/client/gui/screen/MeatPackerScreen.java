package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.container.MeatPackerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class MeatPackerScreen extends AbstractMachineScreen<MeatPackerMenu>{
    public MeatPackerScreen(MeatPackerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        setTexture(new ResourceLocation(MODID, "textures/gui/meat_packer.png"));
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialtick, int mouseX, int mouseY) {
        super.renderBg(gui, partialtick, mouseX, mouseY);
        renderEnergyBar(gui, x + 5, y + 20);
        renderProgressArrow(gui, x + 50, y + 35);
        IFluidHandler tank = menu.getBlockEntity().getLevel().getCapability(Capabilities.FluidHandler.BLOCK, menu.getBlockEntity().getBlockPos(), null);
        if(tank != null) renderFluidBar(gui, x + 24, y + 20, tank.getFluidInTank(0));
    }
}
