package com.sonamorningstar.eternalartifacts.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModItemEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModItemMultiFluidTank;
import com.sonamorningstar.eternalartifacts.core.ModLoots;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KeepContentsFunction extends LootItemConditionalFunction {
    public static final Codec<KeepContentsFunction> CODEC = RecordCodecBuilder.create(p -> commonFields(p).apply(p, KeepContentsFunction::new));

    public static @NotNull Builder<?> builder() {
        return simpleBuilder(KeepContentsFunction::new);
    }

    protected KeepContentsFunction(List<LootItemCondition> pPredicates) {
        super(pPredicates);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext ctx) {
        BlockEntity entity = ctx.getParam(LootContextParams.BLOCK_ENTITY);
        Level level = ctx.getLevel();
        ItemStack tool = ctx.getParam(LootContextParams.TOOL);
        if (!tool.is(ModTags.Items.TOOLS_WRENCH)) return stack;
        IFluidHandlerItem tankStack = stack.getCapability(Capabilities.FluidHandler.ITEM);
        IFluidHandler tankBe = level.getCapability(Capabilities.FluidHandler.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, null);
        IEnergyStorage energyStack = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        IEnergyStorage energyBe = level.getCapability(Capabilities.EnergyStorage.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, null);
        IItemHandler invStack = stack.getCapability(Capabilities.ItemHandler.ITEM);
        IItemHandler invBe = level.getCapability(Capabilities.ItemHandler.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, null);
        if (tankBe != null) {
            if (tankStack instanceof ModItemMultiFluidTank<? extends ModFluidStorage> mimft) {
                for (int i = 0; i < tankBe.getTanks(); i++) {
                    FluidStack fluidStack = tankBe.getFluidInTank(i);
                    try {
                        mimft.getTank(i).setFluid(fluidStack, 0);
                    } catch (IndexOutOfBoundsException e) {
                        EternalArtifacts.LOGGER.error("Tank index out of bounds: {} for {}", i, stack);
                    }
                }
            } else {
                if (tankStack != null) {
                    for (int i = 0; i < tankBe.getTanks(); i++) {
                        FluidStack fluidStack = tankBe.getFluidInTank(i);
                        tankStack.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
        }
        if (energyStack != null && energyBe != null) {
            if (energyStack instanceof ModItemEnergyStorage mes) mes.setEnergy(energyBe.getEnergyStored());
            else energyStack.receiveEnergy(energyBe.getEnergyStored(), false);
        }
        if (invStack != null && invBe != null) {
            for (int i = 0; i < invBe.getSlots(); i++) {
                invStack.insertItem(i, invBe.extractItem(i, Integer.MAX_VALUE, false), false);
            }
        }
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLoots.KEEP_CONTENTS_FUNCTION.get();
    }
}
