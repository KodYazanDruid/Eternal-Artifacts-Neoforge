package com.sonamorningstar.eternalartifacts.content.block.properties;

import net.minecraft.util.StringRepresentable;

public enum DockPart implements StringRepresentable {
    NORTH_WEST("north_south"),
    NORTH("north"),
    NORTH_EAST("north_east"),
    WEST("west"),
    CENTER("center"),
    EAST("east"),
    SOUTH_WEST("south_west"),
    SOUTH("south"),
    SOUTH_EAST("south_east");

    private final String name;

    DockPart(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {return name;}
}
