package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.container.MeatPackerMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

@Getter
public class MeatPackerMachineBlockEntity extends SidedTransferMachineBlockEntity<MeatPackerMenu> {
    public MeatPackerMachineBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MEAT_PACKER.get(), pos, blockState, MeatPackerMenu::new);
        setInventory(createBasicInventory(1, false));
        setEnergy(createDefaultEnergy());
        setTank(createBasicTank(16000, fs -> fs.is(ModTags.Fluids.MEAT), true, true));
        outputSlots.add(0);
    }

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
