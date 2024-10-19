package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.google.common.base.Predicates;
import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.MultiFluidTank;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Random;

public class OilRefineryBlockEntity extends GenericMachineBlockEntity {
    public OilRefineryBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.OIL_REFINERY, pos, blockState);
        setMaxProgress(20);
        setEnergy(createDefaultEnergy());
        outputSlots.add(0);
        outputSlots.add(1);
        outputSlots.add(2);
        setInventory(createBasicInventory(3, false));
        setTank(new MultiFluidTank<>(
                createBasicTank(16000, fs -> fs.is(ModTags.Fluids.CRUDE_OIL), false, true),
                createBasicTank(16000, Predicates.alwaysFalse(), true, false),
                createBasicTank(16000, Predicates.alwaysFalse(), true, false)
        ));
        screenInfo.setShouldBindSlots(false);
        screenInfo.setShouldDrawInventoryTitle(false);
        screenInfo.setTankPosition(35, 20, 0);
        screenInfo.setTankPosition(95, 20, 1);
        screenInfo.setTankPosition(115, 20, 2);
        screenInfo.setOverrideArrowPos(true);
        screenInfo.setArrowPos(63, 41);
        screenInfo.setSlotPosition(140, 20, 0);
        screenInfo.setSlotPosition(140, 40, 1);
        screenInfo.setSlotPosition(140, 60, 2);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoInputFluids(lvl, pos, tank);
        performAutoOutputFluids(lvl, pos, tank);

        if (tank.getFluidInTank(0).getAmount() < 50) return;

        FluidStack diesel = ModFluids.DIESEL.getFluidStack(20);
        FluidStack gasoline = ModFluids.GASOLINE.getFluidStack(25);
        Pair<ItemStack, Float> tar = Pair.of(ModItems.TAR_BALL.toStack(), 0.15F);
        Pair<ItemStack, Float> bitumen = Pair.of(ModItems.BITUMEN.toStack(), 0.1F);

        ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initOutputSlots(outputSlots)
                .queueItemStack(tar.getFirst())
                .queueItemStack(bitumen.getFirst())
                .commitQueuedItemStacks()
                .initOutputTank(new MultiFluidTank<>(tank.get(1), tank.get(2)))
                .queueFluidStack(diesel)
                .queueFluidStack(gasoline)
                .commitQueuedFluidStacks();

        progress(condition::getResult, ()-> {
            tank.get(0).drainForced(50, IFluidHandler.FluidAction.EXECUTE);
            tank.get(1).fillForced(diesel, IFluidHandler.FluidAction.EXECUTE);
            tank.get(2).fillForced(gasoline, IFluidHandler.FluidAction.EXECUTE);
            Random random = new Random();
            if (random.nextFloat() <= tar.getSecond()) ItemHelper.insertItemStackedForced(inventory, tar.getFirst(), false);
            if (random.nextFloat() <= bitumen.getSecond()) ItemHelper.insertItemStackedForced(inventory, bitumen.getFirst(), false);
        }, energy);
    }
}
