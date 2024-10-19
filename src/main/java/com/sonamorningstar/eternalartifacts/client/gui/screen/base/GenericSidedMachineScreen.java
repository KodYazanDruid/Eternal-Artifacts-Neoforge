package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.api.machine.GenericScreenInfo;
import com.sonamorningstar.eternalartifacts.api.machine.records.CustomRenderButtonInfo;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SpriteButton;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.protocol.BlockEntityButtonPress;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.Map;
import java.util.function.BiConsumer;

public class GenericSidedMachineScreen extends AbstractSidedMachineScreen<GenericMachineMenu>{
    private final GenericMachineBlockEntity machine;
    private final GenericScreenInfo screenInfo;
    public GenericSidedMachineScreen(GenericMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.machine = ((GenericMachineBlockEntity) menu.getBlockEntity());
        this.screenInfo = machine.getScreenInfo();
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < screenInfo.getButtons().size(); i++) {
            CustomRenderButtonInfo info = screenInfo.getButtons().get(i);
            int finalI = i;
            addRenderableWidget(SpriteButton
                    .builder(Component.empty(), (button, key) -> handleButtonPress(button, key, finalI, info.onPress()), info.tex())
                    .size(info.width(), info.height())
                    .pos(leftPos + info.x(), topPos + info.y()).build());
        }

    }

    private void handleButtonPress(SpriteButton button, int key, int index, BiConsumer<SpriteButton, Integer> onPress) {
        onPress.accept(button, key);
        Channel.sendToServer(new BlockEntityButtonPress(machine.getBlockPos(), key, index));
    }

    @Override
    protected void renderBg(GuiGraphics gui, float tick, int mx, int my) {
        super.renderBg(gui, tick, mx, my);
        if (menu.getBeEnergy() != null) renderDefaultEnergyBar(gui);
        if (menu.getBeTank() != null) {
            for (int i = 0; i < machine.tank.getTanks(); i++) {
                Map<AbstractFluidTank, Pair<Integer, Integer>> tankLocations = screenInfo.getTankPositions();
                Pair<Integer, Integer> loc = tankLocations.get(machine.tank.get(i));
                if (loc != null) renderFluidBar(gui, x + loc.getFirst(), y + loc.getSecond(), i);
                else renderDefaultFluidBar(gui);
            }
        }

        screenInfo.getComponents().forEach(((component, info) -> {
            gui.drawString(font, component, info.x(), info.y(), info.color(), info.dropShadow());
        }));

        if(screenInfo.isShouldDrawArrow()) renderProgressArrow(gui, x + menu.arrowX, y + menu.arrowY, mx, my);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mx, int my) {
        if(screenInfo.isShouldDrawMachineTitle()) gui.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        if(screenInfo.isShouldDrawInventoryTitle()) gui.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}
