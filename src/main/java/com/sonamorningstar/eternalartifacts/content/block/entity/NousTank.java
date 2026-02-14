package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.container.NousTankMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class NousTank extends SidedTransferMachine<NousTankMenu> {
    public NousTank(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.NOUS_TANK.get(), pos, blockState, NousTankMenu::new);
        setTank(() -> new ModFluidStorage(64000 * (getVolumeLevel() + 1), fs -> fs.is(ModTags.Fluids.EXPERIENCE)) {
            @Override
            protected void onContentsChanged() {
                NousTank.this.requestModelDataUpdate();
                NousTank.this.sendUpdate();
            }
        });
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoInputFluids(lvl, pos);
        performAutoOutputFluids(lvl, pos);
    }
}
