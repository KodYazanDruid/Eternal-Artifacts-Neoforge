package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import net.minecraft.world.phys.AABB;

public interface IAreaRenderer {
    boolean shouldRender();

    AABB getBoundingBox();
}
