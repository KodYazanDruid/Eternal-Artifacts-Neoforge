package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;

public class Disenchanter extends GenericMachine {
	public Disenchanter(BlockPos pos, BlockState blockState) {
		super(ModMachines.DISENCHANTER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		setTank(() -> createBasicTank(16000, fs -> fs.is(ModTags.Fluids.EXPERIENCE),true, false));
		setInventory(() -> createBasicInventory(4, (i, s) -> {
			if (i == 0 && (s.isEnchanted() ||
				(s.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(s).isEmpty())
			)) return true;
			else if (i == 1 && s.getItem() == Items.BOOK) return true;
			else if (i != 0 && i != 1) return !outputSlots.contains(i);
			return false;
		}, s -> s == 1 ? 64 : 1));
		setMaxProgress(200);
		outputSlots.add(2);
		outputSlots.add(3);
		screenInfo.attachTankToLeft(0);
		screenInfo.setSlotPosition(46, 44, 0);
		screenInfo.setSlotPosition(64, 44, 1);
		screenInfo.setArrowPos(85, 45);
		screenInfo.setSlotPosition(113, 30, 2);
		screenInfo.setSlotPosition(113, 58, 3);
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoOutputFluids(lvl, pos);
		ItemStack enchantedItem = inventory.getStackInSlot(0);
		ItemStack book = inventory.getStackInSlot(1);
		ItemStack extracted = inventory.getStackInSlot(2);
		ItemStack enchantmentBook = inventory.getStackInSlot(3);
		var enchs = EnchantmentHelper.getEnchantments(enchantedItem);
		var filtered = enchs.entrySet().stream()
			.filter(e -> {
				if (isVersatile()) return e.getKey().isAllowedOnBooks();
				else return !e.getKey().isCurse() && e.getKey().isAllowedOnBooks();
			}).toList();
		progress(() -> validateItems(enchantedItem, book, extracted, enchantmentBook, filtered, enchs), () -> {
			if (!filtered.isEmpty()) {
				var first = filtered.get(0);
				ItemStack newExtracted = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(first.getKey(), first.getValue()));
				inventory.setStackInSlot(3, newExtracted);
				ItemStack copy = enchantedItem.copy();
				inventory.extractItem(1, 1, false);
				inventory.extractItem(0, 1, false);
				enchs.remove(first.getKey());
				if (copy.is(Items.ENCHANTED_BOOK) && copy.getTag() != null) {
					copy.getTag().remove("StoredEnchantments");
				}
				EnchantmentHelper.setEnchantments(enchs, copy);
				inventory.setStackInSlot(2, copy);
			}
		}, energy);
	}
	
	private boolean validateItems(ItemStack enchanted, ItemStack book, ItemStack extracted, ItemStack enchantmentBook,
								  List<Map.Entry<Enchantment, Integer>> filtered, Map<Enchantment, Integer> enchs) {
	 	return !extracted.isEmpty() || !enchantmentBook.isEmpty() ||
			filtered.isEmpty() || book.isEmpty() || (enchanted.is(Items.ENCHANTED_BOOK) && enchs.size() == 1);
	}
}
