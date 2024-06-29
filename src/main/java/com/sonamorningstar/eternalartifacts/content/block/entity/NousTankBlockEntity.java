package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.IHasFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.container.NousTankMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class NousTankBlockEntity extends SidedTransferMachineBlockEntity<NousTankMenu>  implements IHasFluidTank {
    public NousTankBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.NOUS_TANK.get(), pos, blockState, NousTankMenu::new);
    }

    @Getter
    public ModFluidStorage tank = new ModFluidStorage(64000, fs -> fs.is(ModTags.Fluids.EXPERIENCE)) {
        @Override
        protected void onContentsChanged() {
            NousTankBlockEntity.this.requestModelDataUpdate();
            NousTankBlockEntity.this.sendUpdate();
        }
    };

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        tank.readFromNBT(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        tank.writeToNBT(pTag);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInputFluids(lvl, pos, tank);
        performAutoOutputFluids(lvl, pos, tank);
    }
}
