package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.InductionFurnaceMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.InductionFurnaceBlockEntity;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class InductionFurnaceScreen extends AbstractSidedMachineScreen<InductionFurnaceMenu> {
    public InductionFurnaceScreen(InductionFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float tick, int mx, int my) {
        super.renderBg(gui, tick, mx, my);
        renderDefaultEnergyBar(gui);
        renderProgressArrow(gui, x + 81, y + 36, mx, my);
        renderHeat(gui, x + 30, y + 56);
    }

    private void renderHeat(GuiGraphics gui, int x, int y) {
        double heat = ((InductionFurnaceBlockEntity) menu.getBlockEntity()).getHeatPercentage();
        gui.drawString(font, ModConstants.GUI.withSuffixTranslatable("heat").append(": ")
                .append(String.format("%.2f", heat)).append("%") , x, y, labelColor, false);
    }
}
