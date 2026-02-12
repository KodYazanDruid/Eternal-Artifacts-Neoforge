package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.ExperienceHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class Enchanter extends GenericMachine {
	@Getter
	private float enchantmentPower = 0.0F;
	@Getter
	private int currentXpCost = 0;
	
	private int enchantmentSeed;
	private ItemStack pendingResult = ItemStack.EMPTY;
	private int pendingFluidCost = 0;
	private int pendingLapisCost = 0;
	
	public Enchanter(BlockPos pos, BlockState blockState) {
		super(ModMachines.ENCHANTER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		setTank(() -> createBasicTank(16000, fs -> fs.is(ModTags.Fluids.EXPERIENCE), true, true));
		outputSlots.add(2);
		setInventory(() -> createBasicInventory(3,
			(slot, stack) -> {
				if (slot == 0) return !stack.is(Items.LAPIS_LAZULI);
				if (slot == 1) return stack.is(Items.LAPIS_LAZULI);
				return !outputSlots.contains(slot);
			},
			slot -> slot == 0 || slot == 2 ? 1 : 64)
		);
		enchantmentSeed = (int) System.currentTimeMillis();
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("EnchantmentSeed", enchantmentSeed);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("EnchantmentSeed")) {
			enchantmentSeed = tag.getInt("EnchantmentSeed");
		}
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoInputFluids(lvl, pos);
		
		enchantmentPower = 0.0F;
		for (BlockPos bookshelfOffset : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
			if (EnchantmentTableBlock.isValidBookShelf(lvl, pos, bookshelfOffset)) {
				BlockPos bookshelfPos = pos.offset(bookshelfOffset);
				enchantmentPower += lvl.getBlockState(bookshelfPos).getEnchantPowerBonus(lvl, bookshelfPos);
			}
		}
		
		ItemStack input = inventory.getStackInSlot(0);
		ItemStack lapis = inventory.getStackInSlot(1);
		ItemStack output = inventory.getStackInSlot(2);
		
		if (!input.isEmpty() && !lapis.isEmpty() && lapis.is(Items.LAPIS_LAZULI)) {
			int enchantLevel = calculateEnchantLevel();
			
			if (enchantLevel > 0) {
				int levelSlot = getSelectedLevelSlot();
				
				ItemStack result = enchantItem(input.copy(), enchantLevel);
				
				if (!ItemStack.isSameItemSameTags(input, result)) {
					int xpCost = ExperienceHelper.totalXpForLevel(levelSlot);
					currentXpCost = xpCost;
					int fluidCost = xpCost * 20;
					
					if (lapis.getCount() >= levelSlot && tank.getFluidAmount(0) >= fluidCost && canOutputResult(result, output)) {
						setPendingOperation(result, fluidCost, levelSlot);
						progress(
							() -> pendingResult.isEmpty() || !canOutputResult(pendingResult, output),
							this::craftEnchantResult,
							energy
						);
						return;
					}
				}
			}
		}
		
		currentXpCost = 0;
		progress = 0;
	}
	
	private int calculateEnchantLevel() {
		int power = (int) enchantmentPower;
		int slot = getSelectedLevelSlot() - 1;
		RandomSource random = RandomSource.create(enchantmentSeed);
		return EnchantmentHelper.getEnchantmentCost(random, slot, power, inventory.getStackInSlot(0));
	}
	
	private int getSelectedLevelSlot() {
		int power = (int) enchantmentPower;
		if (power >= 15) return 3;
		else if (power >= 8) return 2;
		else return 1;
	}
	
	private ItemStack enchantItem(ItemStack stack, int level) {
		RandomSource random = RandomSource.create(enchantmentSeed);
		return EnchantmentHelper.enchantItem(random, stack, level, isVersatile());
	}
	
	private void setPendingOperation(ItemStack result, int fluidCost, int lapisCost) {
		this.pendingResult = result;
		this.pendingFluidCost = fluidCost;
		this.pendingLapisCost = lapisCost;
	}
	
	private boolean canOutputResult(ItemStack result, ItemStack output) {
		if (output.isEmpty()) return true;
		return ItemHandlerHelper.canItemStacksStack(output, result) && output.getCount() + result.getCount() <= 64;
	}
	
	private void craftEnchantResult() {
		if (pendingResult.isEmpty()) return;
		
		inventory.extractItem(0, 1, false);
		inventory.extractItem(1, pendingLapisCost, false);
		tank.drainForced(pendingFluidCost, IFluidHandler.FluidAction.EXECUTE);
		inventory.insertItemForced(2, pendingResult.copy(), false);
		
		enchantmentSeed = level != null ? level.getRandom().nextInt() : (int) System.currentTimeMillis();
		
		pendingResult = ItemStack.EMPTY;
		pendingFluidCost = 0;
		pendingLapisCost = 0;
	}
}
