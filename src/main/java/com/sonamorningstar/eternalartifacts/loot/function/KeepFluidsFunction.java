package com.sonamorningstar.eternalartifacts.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
        BlockEntity entity = ctx.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        Level level = ctx.getLevel();
        IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
        IFluidHandler fluidHandlerEntity = null;
        if(entity != null)
            fluidHandlerEntity = level.getCapability(Capabilities.FluidHandler.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, null);
        if (fluidHandlerItem != null && fluidHandlerEntity != null) {
            for (int i = 0; i < fluidHandlerEntity.getTanks(); i++) {
                FluidStack fluidStack = fluidHandlerEntity.getFluidInTank(i);
                fluidHandlerItem.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            }
        }
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLoots.KEEP_FLUIDS_FUNCTION.get();
    }
}
