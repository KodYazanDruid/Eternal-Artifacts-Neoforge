package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.container.NousTankMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class NousTankBlockEntity extends SidedTransferMachineBlockEntity<NousTankMenu> {
    public NousTankBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.NOUS_TANK.get(), pos, blockState, NousTankMenu::new);
        setTank(new ModFluidStorage(Integer.MAX_VALUE, fs -> fs.is(ModTags.Fluids.EXPERIENCE)) {
            @Override
            protected void onContentsChanged() {
                NousTankBlockEntity.this.requestModelDataUpdate();
                NousTankBlockEntity.this.sendUpdate();
            }
        });
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInputFluids(lvl, pos, tank);
        performAutoOutputFluids(lvl, pos, tank);
    }
}
