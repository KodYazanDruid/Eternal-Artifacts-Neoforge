package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.api.machine.GenericScreenInfo;
import com.sonamorningstar.eternalartifacts.api.machine.records.CustomRenderButtonInfo;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SpriteButton;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.protocol.BlockEntityButtonPress;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.BiConsumer;

public class GenericSidedMachineScreen extends AbstractSidedMachineScreen<GenericMachineMenu>{
    @Getter
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
            Int2ObjectMap<Pair<Integer, Integer>> tankLocations = screenInfo.getTankPositions();
            for (int i = 0; i < machine.tank.getTanks(); i++) {
                Pair<Integer, Integer> loc = tankLocations.get(i);
                if (loc != null) renderFluidBar(gui, leftPos + loc.getFirst(), topPos + loc.getSecond(), i);
                else renderDefaultFluidBar(gui);
            }
        }

        screenInfo.getComponents().forEach(((component, info) -> {
            gui.drawString(font, component, info.x(), info.y(), info.color(), info.dropShadow());
        }));

        if(screenInfo.isShouldDrawArrow()) renderProgressArrow(gui, leftPos + menu.arrowX, topPos + menu.arrowY, mx, my);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mx, int my) {
        if(screenInfo.isShouldDrawMachineTitle()) gui.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        if(screenInfo.isShouldDrawInventoryTitle()) gui.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}
