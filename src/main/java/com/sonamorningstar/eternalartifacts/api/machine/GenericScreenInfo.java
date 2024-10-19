package com.sonamorningstar.eternalartifacts.api.machine;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.api.machine.records.ComponentInfo;
import com.sonamorningstar.eternalartifacts.api.machine.records.CustomRenderButtonInfo;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SpriteButton;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Getter
@Setter
@RequiredArgsConstructor
public class GenericScreenInfo {
    private final GenericMachineBlockEntity machine;

    private int arrowXOffset = 0;
    private int arrowYOffset = 0;
    private int imageWidth = 176;
    private int imageHeight = 166;
    private int arrowX = 0;
    private int arrowY = 0;

    private boolean shouldBindSlots = true;
    private boolean overrideArrowPos = false;
    private boolean shouldDrawArrow = true;
    private boolean shouldDrawMachineTitle = true;
    private boolean shouldDrawInventoryTitle = true;

    private final Map<AbstractFluidTank, Pair<Integer, Integer>> tankPositions = new HashMap<>();
    private final Map<Integer, Pair<Integer, Integer>> slotPositions = new HashMap<>();
    private final Map<Component, ComponentInfo> components = new HashMap<>();
    private final List<CustomRenderButtonInfo> buttons = new ArrayList<>();

    public static final int defaultTankX = 24;
    public static final int defaultTankY = 20;

    public void setTankPosition(int x, int y, int tankNo) {
        AbstractFluidTank tank = machine.tank.get(tankNo);
        tankPositions.put(tank, Pair.of(x, y));
    }
    public void setArrowPos(int x, int y) {
        setArrowX(x);
        setArrowY(y);
    }

    public void attachTankToLeft(int tankNo) {
        attachTankToLeft(tankNo, 20);
    }
    public void attachTankToLeft(int tankNo, int y) {
        AbstractFluidTank tank = machine.tank.get(tankNo);
        tankPositions.put(tank, Pair.of(imageWidth - 24, y));
    }

    public void setSlotPosition(int x, int y, int slot) {
        slotPositions.put(slot, Pair.of(x, y));
    }

    public void addComponent(Component component, int x, int y) {
        addComponent(component, x, y, 4210752, false);
    }
    public void addComponent(Component component, int x, int y, int color, boolean dropShadow) {
        components.put(component, new ComponentInfo(x, y, color, dropShadow));
    }

    public void addButton(String sprite, int x, int y, int width, int height, BiConsumer<SpriteButton, Integer> onPress) {
        buttons.add(new CustomRenderButtonInfo(x, y, width, height, new ResourceLocation(sprite), onPress));
    }
    public void addButton(String namespace, String sprite, int x, int y, int width, int height, BiConsumer<SpriteButton, Integer> onPress) {
        buttons.add(new CustomRenderButtonInfo(x, y, width, height, new ResourceLocation(namespace, sprite), onPress));
    }
}
