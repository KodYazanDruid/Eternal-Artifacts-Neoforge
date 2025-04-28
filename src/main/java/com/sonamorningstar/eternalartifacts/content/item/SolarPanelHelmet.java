package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModItemEnergyStorage;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.util.EnergyUtils;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolarPanelHelmet extends ArmorItem {
	public SolarPanelHelmet(Properties pProperties) {
		super(ArmorMaterials.IRON, Type.HELMET, pProperties);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int id, boolean isSelected) {
		super.inventoryTick(stack, level, entity, id, isSelected);
		if (id == 39) {
			generate(stack, level, entity.blockPosition().above());
			chargeInventory(stack, (LivingEntity) entity);
		}
	}
	
	public void generate(ItemStack stack, Level level, BlockPos pos) {
		var energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if (energy != null) {
			int xO = pos.getX();
			int yO = pos.getY();
			int zO = pos.getZ();
			int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, xO, zO);
			BlockPos checking = new BlockPos(xO, yO + 1, zO);
			boolean flag = true;
			while (checking.getY() <= y && flag) {
				BlockState state = level.getBlockState(checking);
				if (!(state.isAir() || state.propagatesSkylightDown(level, checking)) || state.is(ModBlocks.SOLAR_PANEL)) {
					flag = false;
				}
				checking = checking.offset(0, 1, 0);
			}
			if (level.isDay() && flag) {
				if (energy.getEnergyStored() < energy.getMaxEnergyStored()) {
					energy.receiveEnergy(2, false);
				}
			}
			
		}
	}
	public void chargeInventory(ItemStack helmet, LivingEntity entity) {
		CharmStorage charms = CharmStorage.get(entity);
		var helmetEnergy = helmet.getCapability(Capabilities.EnergyStorage.ITEM);
		if (helmetEnergy == null || helmetEnergy.getEnergyStored() == 0) return;
		for (int i = 0; i < charms.getSlots(); i++) {
			ItemStack stack = charms.getStackInSlot(i);
			if (stack == helmet || stack.getItem() instanceof SolarPanelHelmet) continue;
			var itemEnergy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
			if (itemEnergy != null) {
				int transferred;
				do transferred = EnergyUtils.transferEnergy(helmetEnergy, itemEnergy, 100);
				while (transferred > 0);
				if (helmetEnergy.getEnergyStored() == 0) break;
			}
		}
		if (helmetEnergy.getEnergyStored() == 0) return;
		IItemHandler entityInv = entity.getCapability(Capabilities.ItemHandler.ENTITY);
		if (entityInv != null) {
			for (int i = 0; i < entityInv.getSlots(); i++) {
				ItemStack stack = entityInv.getStackInSlot(i);
				if (stack != helmet && !(stack.getItem() instanceof SolarPanelHelmet)) {
					var itemEnergy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
					if (itemEnergy != null) {
						int transferred;
						do transferred = EnergyUtils.transferEnergy(helmetEnergy, itemEnergy, 100);
						while (transferred > 0);
						if (helmetEnergy.getEnergyStored() == 0) break;
					}
				}
			}
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
		IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if (energy instanceof ModItemEnergyStorage mies) {
			tooltipComponents.add(ModConstants.GUI.withSuffixTranslatable("stored_energy").append(": ")
				.append(energy.getEnergyStored() + " / ").append(String.valueOf(energy.getMaxEnergyStored())).withStyle(ChatFormatting.YELLOW));
			tooltipComponents.add(ModConstants.GUI.withSuffixTranslatable("energy_transfer_rate").append(": ")
				.append(String.valueOf(mies.getMaxTransfer())).withStyle(ChatFormatting.YELLOW));
		}
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if (energy != null) {
			return energy.getEnergyStored() > 0;
		}
		return false;
	}
	
	@Override
	public int getBarWidth(ItemStack stack) {
		float charge = getChargeLevel(stack);
		return (int) Math.round(charge * 13.0);
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return 0x880808;
	}
	
	private static float getChargeLevel(ItemStack stack) {
		IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		return energy != null ? energy.getEnergyStored() / (float) energy.getMaxEnergyStored() : 0;
	}
}
