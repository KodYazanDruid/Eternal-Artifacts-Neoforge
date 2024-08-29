package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.capabilities.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.Map;

public class GenericSidedMachineScreen extends AbstractSidedMachineScreen<GenericMachineMenu>{
    private final GenericMachineBlockEntity machine = ((GenericMachineBlockEntity) menu.getBlockEntity());
    public GenericSidedMachineScreen(GenericMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float tick, int mx, int my) {
        super.renderBg(gui, tick, mx, my);
        if (menu.getBeEnergy() != null) renderDefaultEnergyBar(gui);
        if (menu.getBeTank() != null) {
            for (int i = 0; i < machine.tank.getTanks(); i++) {
                Map<AbstractFluidTank, Pair<Integer, Integer>> tankLocations = machine.getScreenInfo().getTankPositions();
                Pair<Integer, Integer> loc = tankLocations.get(machine.tank.get(i));
                if (loc != null) renderFluidBar(gui, x + loc.getFirst(), y + loc.getSecond(), i);
                else renderDefaultFluidBar(gui);
            }
        }

        renderProgressArrow(gui, x + menu.arrowX, y + menu.arrowY, mx, my);
    }
}
