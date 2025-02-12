package com.sonamorningstar.eternalartifacts.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModItemMultiFluidTank;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModLoots;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KeepFluidsFunction extends LootItemConditionalFunction {
    public static final Codec<KeepFluidsFunction> CODEC = RecordCodecBuilder.create(p -> commonFields(p).apply(p, KeepFluidsFunction::new));

    public static @NotNull Builder<?> builder() {
        return simpleBuilder(KeepFluidsFunction::new);
    }

    protected KeepFluidsFunction(List<LootItemCondition> pPredicates) {
        super(pPredicates);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext ctx) {
        BlockEntity entity = ctx.getParam(LootContextParams.BLOCK_ENTITY);
        Level level = ctx.getLevel();
        if (entity instanceof ModBlockEntity mbe) {
            var enchantments = mbe.enchantments;
            enchantments.forEach(stack::enchant);
        }
        IFluidHandlerItem tankStack = stack.getCapability(Capabilities.FluidHandler.ITEM);
        IFluidHandler tankBe = level.getCapability(Capabilities.FluidHandler.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, null);
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
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLoots.KEEP_FLUIDS_FUNCTION.get();
    }
}
