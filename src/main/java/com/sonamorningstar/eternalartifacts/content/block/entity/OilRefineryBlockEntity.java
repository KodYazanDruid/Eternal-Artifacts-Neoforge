package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.google.common.base.Predicates;
import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.capabilities.MultiFluidTank;
import com.sonamorningstar.eternalartifacts.container.OilRefineryMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Random;

public class OilRefineryBlockEntity extends SidedTransferMachineBlockEntity<OilRefineryMenu> {
    public OilRefineryBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.OIL_REFINERY.get(), pos, blockState, OilRefineryMenu::new);
        setMaxProgress(20);
        setEnergy(createDefaultEnergy());
        setInventory(createBasicInventory(3, false));
        setTank(new MultiFluidTank<>(
                createBasicTank(16000, fs -> fs.is(ModTags.Fluids.CRUDE_OIL), false, true),
                createBasicTank(16000, Predicates.alwaysFalse(), true, false),
                createBasicTank(16000, Predicates.alwaysFalse(), true, false)
        ));
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInputFluids(lvl, pos, tank);
        performAutoOutputFluids(lvl, pos, tank);
        performAutoOutput(lvl, pos, inventory, 0, 1, 2);

        if (tank.getFluidInTank(0).getAmount() < 50) return;

        FluidStack diesel = ModFluids.DIESEL.getFluidStack(20);
        FluidStack gasoline = ModFluids.GASOLINE.getFluidStack(25);
        Pair<ItemStack, Float> tar = Pair.of(ModItems.TAR_BALL.toStack(), 0.15F);
        Pair<ItemStack, Float> bitument = Pair.of(ModItems.BITUMEN.toStack(), 0.1F);

        /*ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initOutputSlots(0, 1, 2)
                .tryInsertForced(tar.getFirst(), bitument.getFirst())
                .initOutputTanks(tanks.getTanksAsList())
                .tryInsertForced(diesel)
                .tryInsertForced(gasoline);*/

        progress(()-> {
            FluidStack oilExtracted = tank.get(0).drainForced(50, IFluidHandler.FluidAction.SIMULATE);
            int dieselInserted = tank.get(1).fillForced(diesel, IFluidHandler.FluidAction.SIMULATE);
            int gasolineInserted = tank.get(2).fillForced(gasoline, IFluidHandler.FluidAction.SIMULATE);
            ItemStack first = ItemHelper.insertItemStackedForced(inventory, tar.getFirst(), true);
            ItemStack second = ItemHelper.insertItemStackedForced(inventory, bitument.getFirst(), true);
            return oilExtracted.getAmount() < 50 || dieselInserted < diesel.getAmount() ||
                    gasolineInserted < gasoline.getAmount() || !first.isEmpty() || !second.isEmpty();
        }, ()-> {
            tank.get(0).drainForced(50, IFluidHandler.FluidAction.EXECUTE);
            tank.get(1).fillForced(diesel, IFluidHandler.FluidAction.EXECUTE);
            tank.get(2).fillForced(gasoline, IFluidHandler.FluidAction.EXECUTE);
            Random random = new Random();
            if (random.nextFloat() <= tar.getSecond()) ItemHelper.insertItemStackedForced(inventory, tar.getFirst(), false);
            if (random.nextFloat() <= bitument.getSecond()) ItemHelper.insertItemStackedForced(inventory, bitument.getFirst(), false);
        }, energy);
    }
}
