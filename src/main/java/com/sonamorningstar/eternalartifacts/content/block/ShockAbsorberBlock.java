package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.BaseMachineBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.ShockAbsorber;
import com.sonamorningstar.eternalartifacts.core.ModProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class ShockAbsorberBlock extends BaseMachineBlock<ShockAbsorber> {
    public ShockAbsorberBlock() {
        super(ModProperties.Blocks.SHOCK_ABSORBER, ShockAbsorber::new);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return false;
    }
}
