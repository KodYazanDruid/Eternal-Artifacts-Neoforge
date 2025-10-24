package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import net.minecraft.world.phys.AABB;

public interface AreaRenderer {
    boolean shouldRender();

    AABB getWorkingArea();
}
