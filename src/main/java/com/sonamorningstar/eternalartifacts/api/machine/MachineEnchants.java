package com.sonamorningstar.eternalartifacts.api.machine;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MachineEnchants {
	public static final Map<BlockEntityType<? extends ModBlockEntity>, Set<Enchantment>> enchantMap = new HashMap<>();
	private static final Set<Enchantment> commonMachineEnchants = Set.of(
		Enchantments.BLOCK_EFFICIENCY,
		Enchantments.UNBREAKING,
		Enchantments.BLAST_PROTECTION,
		Enchantments.FIRE_PROTECTION,
		ModEnchantments.CELERITY.get(),
		ModEnchantments.SOULBOUND.get(),
		ModEnchantments.EVERLASTING.get(),
		ModEnchantments.VOLUME.get()
	);
	
	private static final Set<Enchantment> nonProgressMachineEnchants = Set.of(
		Enchantments.UNBREAKING,
		Enchantments.BLAST_PROTECTION,
		Enchantments.FIRE_PROTECTION,
		ModEnchantments.SOULBOUND.get(),
		ModEnchantments.EVERLASTING.get(),
		ModEnchantments.VOLUME.get()
	);
	
	public static void bootstrap() {
		enchantMap.put(ModMachines.MOB_LIQUIFIER.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.MEAT_SHREDDER.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.MEAT_PACKER.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.FLUID_INFUSER.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.INDUSTRIAL_MACERATOR.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.MELTING_CRUCIBLE.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.MATERIAL_SQUEEZER.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.ALLOY_SMELTER.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.SOLIDIFIER.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.COMPRESSOR.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.ADVANCED_CRAFTER.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.FLUID_MIXER.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.DISENCHANTER.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.MARINE_FISHER.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.OIL_REFINERY.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.ELECTRIC_FURNACE.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), commonMachineEnchants);
		enchantMap.put(ModBlockEntities.SOLID_COMBUSTION_DYNAMO.get(), commonMachineEnchants);
		enchantMap.put(ModBlockEntities.ANVILINATOR.get(), commonMachineEnchants);
		enchantMap.put(ModBlockEntities.BOOK_DUPLICATOR.get(), commonMachineEnchants);
		enchantMap.put(ModMachines.SMITHINATOR.getBlockEntity(), commonMachineEnchants);
		enchantMap.put(ModMachines.AUTOCUTTER.getBlockEntity(), commonMachineEnchants);
		
		enchantMap.put(ModMachines.BLOCK_BREAKER.getBlockEntity(), nonProgressMachineEnchants);
		enchantMap.put(ModMachines.BLOCK_PLACER.getBlockEntity(), nonProgressMachineEnchants);
		enchantMap.put(ModMachines.MOB_HARVESTER.getBlockEntity(), nonProgressMachineEnchants);
		
		enchantMap.put(ModMachines.INDUCTION_FURNACE.getBlockEntity(), Set.of(
			Enchantments.UNBREAKING,
			Enchantments.BLAST_PROTECTION,
			Enchantments.FIRE_PROTECTION,
			ModEnchantments.CELERITY.get(),
			ModEnchantments.SOULBOUND.get(),
			ModEnchantments.EVERLASTING.get(),
			ModEnchantments.VOLUME.get()
		));
		
		
		
	}
}
