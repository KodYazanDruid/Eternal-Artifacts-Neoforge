package com.sonamorningstar.eternalartifacts.content.block.base;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class ItemStorageEntityBlock extends BaseEntityBlock {
	protected ItemStorageEntityBlock(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof ModBlockEntity mbe) {
			Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
			mbe.enchantments.clear();
			mbe.enchantments.putAll(enchantments);
			for (Map.Entry<Enchantment, Integer> entry : mbe.enchantments.object2IntEntrySet()) {
				mbe.onEnchanted(entry.getKey(), entry.getValue());
			}
		}
		
		IItemHandler ihi = stack.getCapability(Capabilities.ItemHandler.ITEM);
		IItemHandler ih = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
		if (ihi != null && ih instanceof IItemHandlerModifiable ihm) {
			for (int i = 0; i < ihi.getSlots() && i < ih.getSlots(); i++) {
				ihm.setStackInSlot(i, ihi.getStackInSlot(i));
			}
		}
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		Block block = state.getBlock();
		ItemStack stack = new ItemStack(block);
		Level actualLevel = level.getBlockEntity(pos).getLevel();
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof ModBlockEntity mbe) {
			mbe.enchantments.forEach(stack::enchant);
		}
		if(actualLevel != null) {
			IItemHandler ih = actualLevel.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
			IItemHandler ihi = stack.getCapability(Capabilities.ItemHandler.ITEM);
			if (ih != null && ihi instanceof IItemHandlerModifiable ihm) {
				for (int i = 0; i < ih.getSlots() && i < ihi.getSlots(); i++) {
					ihm.setStackInSlot(i, ih.getStackInSlot(i));
				}
			}
		}
		return stack;
	}
}
