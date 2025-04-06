package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

@Getter
public class MeatPacker extends GenericMachineBlockEntity {
    public MeatPacker(BlockPos pos, BlockState blockState) {
        super(ModMachines.MEAT_PACKER, pos, blockState);
        setInventory(() -> createBasicInventory(1, false));
        setEnergy(this::createDefaultEnergy);
        setTank(() -> createBasicTank(16000, fs -> fs.is(ModTags.Fluids.MEAT), true, true));
        outputSlots.add(0);
        screenInfo.setArrowXOffset(-40);
    }
    
    @Override
    protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
        condition
            .queueImport(ModItems.RAW_MEAT_INGOT.toStack())
            .initInputTank(tank)
            .tryExtractFluidForced(250)
            .commitQueuedImports();
        super.setProcessCondition(condition, recipe);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoOutputItems(lvl, pos);
        performAutoInputFluids(lvl, pos);

        progress(()-> {
            tank.drainForced(250, IFluidHandler.FluidAction.EXECUTE);
            inventory.insertItemForced(0, ModItems.RAW_MEAT_INGOT.toStack(), false);
        });
    }

}
