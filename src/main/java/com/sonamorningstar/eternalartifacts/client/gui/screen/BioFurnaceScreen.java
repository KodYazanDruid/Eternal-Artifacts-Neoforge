package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.container.BioFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BioFurnaceScreen extends AbstractMachineScreen<BioFurnaceMenu> {
    public BioFurnaceScreen(BioFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        setTexture(new ResourceLocation(MODID, "textures/gui/biofurnace.png"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);

        renderEnergyBar(pGuiGraphics, x + 5, y + 20);
        renderBurn(pGuiGraphics, x, y);
    }

}
