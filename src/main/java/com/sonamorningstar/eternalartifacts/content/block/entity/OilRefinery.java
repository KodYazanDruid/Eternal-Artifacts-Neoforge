package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.MultiFluidTank;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class OilRefinery extends GenericMachine {
    private final int consume = 100;
    public OilRefinery(BlockPos pos, BlockState blockState) {
        super(ModMachines.OIL_REFINERY, pos, blockState);
        setMaxProgress(20);
        setEnergy(this::createDefaultEnergy);
        outputSlots.add(0);
        outputSlots.add(1);
        outputSlots.add(2);
        setInventory(() -> createBasicInventory(3, false));
        setTank(() -> new MultiFluidTank<>(
                createBasicTank(16000, fs -> fs.is(ModTags.Fluids.CRUDE_OIL), false, true),
                createBasicTank(16000, fs -> fs.is(ModFluids.DIESEL.getFluid()), true, false),
                createBasicTank(16000, fs -> fs.is(ModFluids.GASOLINE.getFluid()), true, false),
                createBasicTank(16000, fs -> fs.is(ModFluids.NAPHTHA.getFluid()), true, false)
        ));
        screenInfo.setShouldDrawInventoryTitle(false);
        screenInfo.setTankPosition(24, 20, 0);
        screenInfo.setTankPosition(84, 20, 1);
        screenInfo.setTankPosition(104, 20, 2);
        screenInfo.setTankPosition(124, 20, 3);
        screenInfo.setArrowPos(52, 41);
        screenInfo.setSlotPosition(149, 20, 0);
        screenInfo.setSlotPosition(149, 40, 1);
        screenInfo.setSlotPosition(149, 60, 2);
    }
    
    @Override
    public void setProcessCondition(ProcessCondition processCondition, @Nullable Recipe<?> recipe) {
        FluidStack diesel = ModFluids.DIESEL.getFluidStack(20);
        FluidStack gasoline = ModFluids.GASOLINE.getFluidStack(25);
        FluidStack naphtha = ModFluids.NAPHTHA.getFluidStack(15);
        ItemStack tar = ModItems.TAR_BALL.toStack();
        ItemStack bitumen = ModItems.BITUMEN.toStack();
        processCondition
            .queueImport(tar).queueImport(bitumen)
            .initOutputTank(new MultiFluidTank<>(tank.get(1), tank.get(2), tank.get(3)))
            .queueImport(diesel).queueImport(gasoline).queueImport(naphtha)
            .commitQueuedImports()
            .initInputTank(tank.get(0))
            .tryExtractFluidForced(consume);
        super.setProcessCondition(processCondition, recipe);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoInputFluids(lvl, pos);
        performAutoOutputFluids(lvl, pos);

        if (tank.getFluidInTank(0).getAmount() < consume) return;

        progress(()-> {
            FluidStack diesel = ModFluids.DIESEL.getFluidStack(25);
            FluidStack gasoline = ModFluids.GASOLINE.getFluidStack(25);
            FluidStack naphtha = ModFluids.NAPHTHA.getFluidStack(30);
            Pair<ItemStack, Float> tar = Pair.of(ModItems.TAR_BALL.toStack(), 0.2F);
            Pair<ItemStack, Float> bitumen = Pair.of(ModItems.BITUMEN.toStack(), 0.3F);
            tank.get(0).drainForced(consume, IFluidHandler.FluidAction.EXECUTE);
            tank.get(1).fillForced(diesel, IFluidHandler.FluidAction.EXECUTE);
            tank.get(2).fillForced(gasoline, IFluidHandler.FluidAction.EXECUTE);
            tank.get(3).fillForced(naphtha, IFluidHandler.FluidAction.EXECUTE);
            
            Random random = new Random();
            if (random.nextFloat() <= tar.getSecond()) ItemHelper.insertItemStackedForced(inventory, tar.getFirst(), false);
            if (random.nextFloat() <= bitumen.getSecond()) ItemHelper.insertItemStackedForced(inventory, bitumen.getFirst(), false);
        });
    }
}
