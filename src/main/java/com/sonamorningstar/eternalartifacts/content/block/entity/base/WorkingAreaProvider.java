package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.api.machine.config.ToggleConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public interface WorkingAreaProvider {
    
    default boolean shouldRenderArea() {
        if (this instanceof ModBlockEntity mbe) {
            MachineConfiguration configs = mbe.getConfiguration();
            ToggleConfig renderArea = configs.get(ToggleConfig.class, "render_area");
            return renderArea != null && renderArea.isEnabled();
        }
        return false;
    }

    AABB getWorkingArea(BlockPos anchor);
}
