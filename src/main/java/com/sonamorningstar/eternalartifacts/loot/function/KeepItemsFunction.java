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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KeepItemsFunction extends LootItemConditionalFunction {
	public static final Codec<KeepItemsFunction> CODEC = RecordCodecBuilder.create(p -> commonFields(p).apply(p, KeepItemsFunction::new));
	
	public static @NotNull Builder<?> builder() {
		return simpleBuilder(KeepItemsFunction::new);
	}
	
	protected KeepItemsFunction(List<LootItemCondition> pPredicates) {
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
		IItemHandler stackInv = stack.getCapability(Capabilities.ItemHandler.ITEM);
		IItemHandler beInv = level.getCapability(Capabilities.ItemHandler.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, null);
		if (beInv != null) {
			if (stackInv instanceof IItemHandlerModifiable stackInvModifiable) {
				for (int i = 0; i < beInv.getSlots(); i++) {
					stackInvModifiable.setStackInSlot(i, beInv.getStackInSlot(i));
				}
			}
			
		}
		return stack;
	}
	
	@Override
	public LootItemFunctionType getType() {
		return ModLoots.KEEP_ITEMS_FUNCTION.get();
	}
}
