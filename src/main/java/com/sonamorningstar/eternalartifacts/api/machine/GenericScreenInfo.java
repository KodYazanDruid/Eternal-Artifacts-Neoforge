package com.sonamorningstar.eternalartifacts.api.machine;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.capabilities.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class GenericScreenInfo {
    private final GenericMachineBlockEntity machine;

    private int arrowXOffset = 0;
    private int arrowYOffset = 0;

    private int imageWidth = 176;
    private int imageHeight = 166;

    private final Map<AbstractFluidTank, Pair<Integer, Integer>> tankPositions = new HashMap<>();

    public void setTankPosition(int x, int y, int tankNo) {
        AbstractFluidTank tank = machine.tank.get(tankNo);
        tankPositions.put(tank, Pair.of(x, y));
    }

    public void attachTankToLeft( int tankNo) {
        attachTankToLeft(tankNo, 20);
    }
    public void attachTankToLeft( int tankNo, int y) {
        AbstractFluidTank tank = machine.tank.get(tankNo);
        tankPositions.put(tank, Pair.of(imageWidth - 24, y));
    }
}
