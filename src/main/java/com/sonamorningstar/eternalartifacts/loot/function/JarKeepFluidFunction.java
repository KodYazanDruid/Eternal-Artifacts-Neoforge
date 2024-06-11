package com.sonamorningstar.eternalartifacts.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.content.block.entity.JarBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModLoots;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JarKeepFluidFunction extends LootItemConditionalFunction {
    public static final Codec<JarKeepFluidFunction> CODEC = RecordCodecBuilder.create(p -> commonFields(p).apply(p, JarKeepFluidFunction::new));

    public static @NotNull Builder builder() {
        return simpleBuilder(JarKeepFluidFunction::new);
    }

    protected JarKeepFluidFunction(List<LootItemCondition> pPredicates) {
        super(pPredicates);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext ctx) {
        BlockEntity entity = ctx.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if(entity instanceof JarBlockEntity jar) {
            FluidStack fluidStack = jar.tank.getFluid();
            IFluidHandlerItem fluidHandlerItem = FluidUtil.getFluidHandler(stack).get();
            fluidHandlerItem.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
        }else {
            String name = entity == null ? "null" : entity.getClass().getName();
            EternalArtifacts.LOGGER.warn("Found wrong block entity for loot function, expected JarEntity, found {}", name);
        }
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLoots.JAR_KEEP_FUNCTION.get();
    }
}
