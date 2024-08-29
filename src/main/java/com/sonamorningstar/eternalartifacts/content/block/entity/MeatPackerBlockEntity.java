package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

@Getter
public class MeatPackerBlockEntity extends GenericMachineBlockEntity {
    public MeatPackerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.MEAT_PACKER.getBlockEntity(), pos, blockState, ModMachines.MEAT_PACKER.getMenu());
        setInventory(createBasicInventory(1, false));
        setEnergy(createDefaultEnergy());
        setTank(createBasicTank(16000, fs -> fs.is(ModTags.Fluids.MEAT), true, true));
        outputSlots.add(0);
        screenInfo.setArrowXOffset(-40);
    }

    @Override
    protected void findRecipe() {}

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoOutput(lvl, pos, inventory, outputSlots.toArray(Integer[]::new));
        performAutoInputFluids(lvl, pos, tank);

        ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initOutputSlots(outputSlots)
                .tryInsertForced(ModItems.RAW_MEAT_INGOT.toStack())
                .initInputTank(tank)
                .tryExtractFluidForced(250);

        progress(condition::getResult, ()-> {
            tank.drainForced(250, IFluidHandler.FluidAction.EXECUTE);
            inventory.insertItemForced(0, ModItems.RAW_MEAT_INGOT.toStack(), false);
        }, energy);
    }

}
