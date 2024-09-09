package com.sonamorningstar.eternalartifacts.capabilities.handler;

public interface IItemCooldown {
    int getCooldown();
    int getMaxCooldown();
    void setCooldown(int tick);
    int addCooldown(int tick);
    int lowerCooldown(int tick);
    void resetCooldown();
    boolean isOnCooldown();
    void onChange();
}