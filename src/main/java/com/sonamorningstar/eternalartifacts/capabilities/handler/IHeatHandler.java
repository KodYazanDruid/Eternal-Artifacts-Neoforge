package com.sonamorningstar.eternalartifacts.capabilities.handler;

public interface IHeatHandler {
    int heat(int amount, boolean simulate);
    int cool(int amount, boolean simulate);

    int getHeat();
    int getMaxHeat();

    boolean canHeat();
    boolean setCanHeat(boolean flag);
    boolean canCool();
    boolean setCanCool(boolean flag);
}
