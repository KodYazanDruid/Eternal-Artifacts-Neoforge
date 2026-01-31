package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MarineFisher extends GenericMachine {
	public MarineFisher(BlockPos pos, BlockState blockState) {
		super(ModMachines.MARINE_FISHER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		setEnergyPerTick(80);
		outputSlots.addAll(List.of(1, 2, 3, 4, 5, 6, 7, 8));
		setInventory(() -> createBasicInventory(9, outputSlots, (slot, stack) -> slot == 0, s -> {}));
		screenInfo.setSlotPosition(26, 44, 0);
		screenInfo.setArrowPos(51, 45);
		for (int i = 0; i < 8; i++) {
			int x = i % 4;
			int y = i / 4;
			screenInfo.setSlotPosition(84 + x * 18, 35 + y * 18, i + 1);
		}
	}
	
	@Override
	protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
		condition.createCustomCondition(() -> {
			boolean hasEmptySlot = false;
			for (Integer outputSlot : outputSlots) {
				ItemStack stack = inventory.getStackInSlot(outputSlot);
				if (stack.isEmpty()) {
					hasEmptySlot = true;
					break;
				}
			}
			ItemStack tool = inventory.getStackInSlot(0);
			return tool.isEmpty() || !tool.is(Tags.Items.TOOLS_FISHING_RODS) || !hasEmptySlot;
		});
		super.setProcessCondition(condition, recipe);
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		
		boolean haveWater = true;
		for (BlockPos blockPos : BlockPos.betweenClosed(pos.below().offset(-1, 0, -1),
														pos.below().offset(1, 0, 1))) {
			if (!lvl.getFluidState(blockPos).is(FluidTags.WATER)) {
				haveWater = false;
				break;
			}
		}
		if (!haveWater) {
			progress = 0;
			return;
		}
		
		FakePlayer fakePlayer = FakePlayerHelper.getFakePlayer(this, level);
		fakePlayer.setYRot(st.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot());
		fakePlayer.setPosRaw(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
		ItemStack tool = inventory.getStackInSlot(0);
		fakePlayer.getInventory().selected = 0;
		fakePlayer.setItemSlot(EquipmentSlot.MAINHAND, tool);
		for (int i = 1; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			fakePlayer.getInventory().setItem(i, stack);
		}
		fakePlayer.detectEquipmentUpdates();
		
		progress(() -> {
			FishingHook hook = new FishingHook(fakePlayer, lvl, EnchantmentHelper.getFishingSpeedBonus(tool), getLuck(tool));
			hook.setPosRaw(pos.getX(), pos.getY() - 1, pos.getZ());
			LootParams lootparams = new LootParams.Builder((ServerLevel)lvl)
				.withParameter(LootContextParams.ORIGIN, pos.below().getCenter())
				.withParameter(LootContextParams.TOOL, tool)
				.withParameter(LootContextParams.THIS_ENTITY, hook)
				.withParameter(LootContextParams.KILLER_ENTITY, fakePlayer)
				.withLuck(getLuck(tool) + fakePlayer.getLuck())
				.create(LootContextParamSets.FISHING);
			LootTable loottable = lvl.getServer().getLootData().getLootTable(BuiltInLootTables.FISHING);
			List<ItemStack> list = loottable.getRandomItems(lootparams);
			for (ItemStack loot : list) {
				ItemHelper.insertItemStackedForced(inventory, loot, false, outputSlots);
			}
			tool.hurtAndBreak(1, fakePlayer, p -> {});
		});
	}
	
	private int getLuck(ItemStack stack) {
		return EnchantmentHelper.getFishingLuckBonus(stack)
			+ getEnchantmentLevel(Enchantments.BLOCK_FORTUNE)
			+ getEnchantmentLevel(Enchantments.MOB_LOOTING);
	}
}
