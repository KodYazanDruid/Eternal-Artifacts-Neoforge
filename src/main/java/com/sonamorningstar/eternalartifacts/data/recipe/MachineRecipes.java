package com.sonamorningstar.eternalartifacts.data.recipe;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.*;
import java.util.stream.Collectors;

public final class MachineRecipes {

	public static void registerMachineRecipes(RecipeOutput output) {
		registerMachineRecipe(output, ModMachines.ELECTRIC_FURNACE,
			"FCB"+"TMT"+"SRP",
			Map.of(
				'F', Ingredient.of(Items.FURNACE),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'T', Ingredient.of(ModTags.Items.INGOTS_TIN),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'S', Ingredient.of(Items.SMOKER),
				'B', Ingredient.of(Items.BLAST_FURNACE),
				'P', Ingredient.of(Items.CAMPFIRE)
			)
		);
		registerMachineRecipe(output, ModMachines.INDUCTION_FURNACE,
			" C "+"TMT"+" R ",
			Map.of(
				'C', Ingredient.of(ModItems.CAPACITOR),
				'T', Ingredient.of(ModItems.CHLOROPHYTE_TABLET),
				'M', Ingredient.of(ModMachines.ELECTRIC_FURNACE),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE)
			)
		);
		registerMachineRecipe(output, ModMachines.INDUSTRIAL_MACERATOR,
			"FPF"+"BMB"+"RCR",
			Map.of(
				'F', Ingredient.of(Items.FLINT),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'B', Ingredient.of(ModTags.Items.INGOTS_BRONZE),
				'P', Ingredient.of(Items.PISTON)
			)
		);
		registerMachineRecipe(output, ModMachines.MEAT_SHREDDER,
			"SBX"+"AMA"+"RCR",
			Map.of(
				'S', Ingredient.of(Items.IRON_SWORD),
				'X', Ingredient.of(Items.IRON_AXE),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'B', Ingredient.of(Items.BLAZE_POWDER),
				'A', Ingredient.of(ModTags.Items.INGOTS_ALUMINUM)
			)
		);
		registerMachineRecipe(output, ModMachines.MEAT_PACKER,
			"SPS"+"TMT"+"RCR",
			Map.of(
				'S', Ingredient.of(ModTags.Items.PLASTIC),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'P', Ingredient.of(Items.PISTON),
				'T', Ingredient.of(ModTags.Items.INGOTS_TIN)
			)
		);
		registerMachineRecipe(output, ModMachines.MOB_LIQUIFIER,
			"DPD"+"BMB"+"RCR",
			Map.of(
				'D', Ingredient.of(ModItems.DEMONIC_TABLET),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'P', Ingredient.of(Items.BLAZE_POWDER),
				'B', Ingredient.of(ModTags.Items.INGOTS_BRONZE)
			)
		);
		registerMachineRecipe(output, ModMachines.FLUID_INFUSER,
			"BPB"+"IMI"+"RCR",
			Map.of(
				'B', Ingredient.of(ModItems.JAR),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(ModItems.CLAY_DUST),
				'I', Ingredient.of(ModTags.Items.INGOTS_ALUMINUM),
				'P', Ingredient.of(ModTags.Items.PLASTIC)
			)
		);
		registerMachineRecipe(output, ModMachines.MELTING_CRUCIBLE,
			"RPB"+"IMI"+"DCD",
			Map.of(
				'B', Ingredient.of(ModItems.JAR),
				'R', Ingredient.of(Tags.Items.INGOTS_BRICK),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'D', Ingredient.of(Tags.Items.DUSTS_GLOWSTONE),
				'I', Ingredient.of(ModItems.MARIN_INGOT),
				'P', Ingredient.of(ModItems.DEMON_INGOT)
			)
		);
		registerMachineRecipe(output, ModMachines.SOLIDIFIER,
			"BPR"+"IMI"+"DCD",
			Map.of(
				'B', Ingredient.of(ModItems.JAR),
				'R', Ingredient.of(Tags.Items.INGOTS_BRICK),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'D', Ingredient.of(Tags.Items.DUSTS_GLOWSTONE),
				'I', Ingredient.of(ModTags.Items.INGOTS_ALUMINUM),
				'P', Ingredient.of(Tags.Items.INGOTS_GOLD)
			)
		);
		registerMachineRecipe(output, ModMachines.MATERIAL_SQUEEZER,
			"RPJ"+"IMI"+"DCD",
			Map.of(
				'R', Ingredient.of(Items.PISTON),
				'J', Ingredient.of(ModItems.JAR),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'D', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'I', Ingredient.of(ModTags.Items.INGOTS_TIN),
				'P', Ingredient.of(Tags.Items.INGOTS_BRICK)
			)
		);
		registerMachineRecipe(output, ModMachines.COMPRESSOR,
			"RPR"+"IMI"+"DCD",
			Map.of(
				'R', Ingredient.of(Items.PISTON),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'D', Ingredient.of(Tags.Items.DUSTS_PRISMARINE),
				'I', Ingredient.of(ModTags.Items.INGOTS_BRONZE),
				'P', Ingredient.of(ModItems.COPPER_TABLET)
			)
		);
		registerMachineRecipe(output, ModMachines.ALLOY_SMELTER,
			"RPR"+"IMI"+"DCD",
			Map.of(
				'R', Ingredient.of(Tags.Items.INGOTS_BRICK),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'D', Ingredient.of(ModItems.PLANT_MATTER),
				'I', Ingredient.of(ModTags.Items.INGOTS_TIN),
				'P', Ingredient.of(Tags.Items.RODS_BLAZE)
			)
		);
		registerMachineRecipe(output, ModMachines.FLUID_MIXER,
			"JBJ"+"IMI"+"DCD",
			Map.of(
				'J', Ingredient.of(ModItems.JAR),
				'B', Ingredient.of(Tags.Items.INGOTS_BRICK),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'D', Ingredient.of(Tags.Items.DUSTS_GLOWSTONE),
				'I', Ingredient.of(ModTags.Items.INGOTS_BRONZE)
			)
		);
		registerMachineRecipe(output, ModMachines.OIL_REFINERY,
			" J "+"JMJ"+"RCR",
			Map.of(
				'J', Ingredient.of(ModItems.JAR),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE)
			)
		);
		registerMachineRecipe(output, ModMachines.BLOCK_BREAKER,
			"BPB"+"IMI"+"DCD",
			Map.of(
				'P', Ingredient.of(ModItems.COPPER_PICKAXE),
				'B', Ingredient.of(Tags.Items.INGOTS_BRICK),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'D', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'I', Ingredient.of(ModTags.Items.INGOTS_BRONZE)
			)
		);
		registerMachineRecipe(output, ModMachines.BLOCK_PLACER,
			"BPB"+"IMI"+"DCD",
			Map.of(
				'P', Ingredient.of(ModItems.COPPER_TABLET),
				'B', Ingredient.of(Tags.Items.INGOTS_BRICK),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'D', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'I', Ingredient.of(ModTags.Items.INGOTS_BRONZE)
			)
		);
		registerMachineRecipe(output, ModMachines.HARVESTER,
			"IHI"+"AMA"+"RCR",
			Map.of(
				'I', Ingredient.of(Tags.Items.INGOTS_IRON),
				'H', Ingredient.of(Items.IRON_HOE),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'A', Ingredient.of(ModTags.Items.INGOTS_ALUMINUM)
			)
		);
		registerMachineRecipe(output, ModMachines.AUTOCUTTER,
			"PSP"+"AMA"+"RCR",
			Map.of(
				'P', Ingredient.of(Items.IRON_SWORD),
				'S', Ingredient.of(Items.STONECUTTER),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'A', Ingredient.of(ModTags.Items.INGOTS_ALUMINUM)
			)
		);
		registerMachineRecipe(output, ModMachines.ADVANCED_CRAFTER,
			"CTC"+"IMI"+"RPR",
			Map.of(
				'C', Ingredient.of(Items.CRAFTING_TABLE),
				'T', Ingredient.of(ModItems.COPPER_TABLET),
				'I', Ingredient.of(ModTags.Items.INGOTS_TIN),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'P', Ingredient.of(ModItems.CAPACITOR)
			)
		);
		registerMachineRecipe(output, ModMachines.ALCHEMICAL_BREWER,
			"JBJ"+"IMI"+"RCR",
			Map.of(
				'J', Ingredient.of(ModItems.JAR),
				'B', Ingredient.of(Items.BREWING_STAND),
				'I', Ingredient.of(ModTags.Items.INGOTS_BRONZE),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE)
			)
		);
		registerMachineRecipe(output, ModMachines.BOTTLER,
			"BJB"+"IMI"+"RCR",
			Map.of(
				'B', Ingredient.of(Tags.Items.INGOTS_BRICK),
				'J', Ingredient.of(ModItems.JAR),
				'I', Ingredient.of(ModTags.Items.INGOTS_BRONZE),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE)
			)
		);
		registerMachineRecipe(output, ModMachines.DIMENSIONAL_ANCHOR,
			"RCR"+"IMI"+"RPR",
			Map.of(
				'R', Ingredient.of(Tags.Items.INGOTS_BRICK),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'P', Ingredient.of(ModItems.DEMONIC_TABLET),
				'I', Ingredient.of(ModItems.OBLIVIUM_INGOT)
			)
		);
		registerMachineRecipe(output, ModMachines.REPAIRER,
			"ICI"+"AMA"+"RPR",
			Map.of(
				'I', Ingredient.of(Tags.Items.INGOTS_IRON),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'P', Ingredient.of(Items.ANVIL),
				'A', Ingredient.of(ModTags.Items.INGOTS_TIN),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE)
			)
		);
		registerMachineRecipe(output, ModMachines.RECYCLER,
			"BCB"+"AMA"+"RPR",
			Map.of(
				'B', Ingredient.of(ModTags.Items.INGOTS_BRONZE),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'P', Ingredient.of(Items.HOPPER),
				'A', Ingredient.of(ModTags.Items.INGOTS_ALUMINUM),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE)
			)
		);
		registerMachineRecipe(output, ModMachines.PACKER,
			"SCS"+"IMI"+"RPR",
			Map.of(
				'S', Ingredient.of(ModTags.Items.PLASTIC),
				'C', Ingredient.of(Items.CHEST),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'P', Ingredient.of(ModItems.CAPACITOR),
				'I', Ingredient.of(ModTags.Items.INGOTS_ALUMINUM)
			)
		);
		registerMachineRecipe(output, ModMachines.UNPACKER,
			"SDS"+"IMI"+"RPR",
			Map.of(
				'S', Ingredient.of(ModTags.Items.PLASTIC),
				'D', Ingredient.of(Items.DISPENSER),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'P', Ingredient.of(ModItems.CAPACITOR),
				'I', Ingredient.of(ModTags.Items.INGOTS_ALUMINUM)
			)
		);
		registerMachineRecipe(output, ModMachines.SLUDGE_REFINER,
			"JPB"+"IMI"+"RCR",
			Map.of(
				'J', Ingredient.of(ModItems.JAR),
				'P', Ingredient.of(ModTags.Items.PLASTIC),
				'B', Ingredient.of(Tags.Items.INGOTS_BRICK),
				'I', Ingredient.of(ModTags.Items.INGOTS_MANGANESE),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE)
			)
		);
		registerMachineRecipe(output, ModMachines.FLUID_PUMP,
			"JPJ"+"IMI"+"RCR",
			Map.of(
				'J', Ingredient.of(ModItems.JAR),
				'P', Ingredient.of(ModTags.Items.PLASTIC),
				'I', Ingredient.of(ModItems.MARIN_INGOT),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE)
			)
		);
		registerMachineRecipe(output, ModMachines.MARINE_FISHER,
			"FRF"+"AMA"+"BCB",
			Map.of(
				'F', Ingredient.of(Items.FISHING_ROD),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'B', Ingredient.of(ModTags.Items.INGOTS_BRONZE),
				'A', Ingredient.of(ModItems.MARIN_INGOT)
			)
		);
		registerMachineRecipe(output, ModMachines.SMITHINATOR,
			"STS"+"AMA"+"RCR",
			Map.of(
				'S', Ingredient.of(ModItems.STONE_TABLET),
				'T', Ingredient.of(Items.SMITHING_TABLE),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'A', Ingredient.of(ModTags.Items.INGOTS_TIN)
			)
		);
		registerMachineRecipe(output, ModMachines.DISENCHANTER,
			"BEB"+"AMA"+"RCR",
			Map.of(
				'B', Ingredient.of(Items.BOOK),
				'E', Ingredient.of(Items.ENCHANTING_TABLE),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'A', Ingredient.of(ModTags.Items.INGOTS_ALUMINUM)
			)
		);
		registerMachineRecipe(output, ModMachines.BATTERY_BOX,
			"RCR"+"CMC"+"PPP",
			Map.of(
				'C', Ingredient.of(ModItems.COPPER_TABLET),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'P', Ingredient.of(ModTags.Items.PLASTIC),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE)
			)
		);
		registerMachineRecipe(output, ModBlocks.SOLAR_PANEL,
			"LLL"+"GCG"+"IMI",
			Map.of(
				'L', Ingredient.of(Tags.Items.GEMS_LAPIS),
				'G', Ingredient.of(Tags.Items.GLASS),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'I', Ingredient.of(ModTags.Items.INGOTS_TIN),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK)
			)
		);
		registerMachineRecipe(output, ModBlocks.FLUID_COMBUSTION_DYNAMO,
			" C "+"BMB"+"RFR",
			Map.of(
				'C', Ingredient.of(ModItems.CAPACITOR),
				'B', Ingredient.of(ModTags.Items.INGOTS_BRONZE),
				'M', Ingredient.of(ModItems.JAR),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'F', Ingredient.of(Items.FURNACE)
			)
		);
		registerMachineRecipe(output, ModBlocks.SOLID_COMBUSTION_DYNAMO,
			" C "+"IMI"+"RFR",
			Map.of(
				'C', Ingredient.of(ModItems.CAPACITOR),
				'I', Ingredient.of(Tags.Items.INGOTS_IRON),
				'M', Ingredient.of(ModTags.Items.INGOTS_BRONZE),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'F', Ingredient.of(Items.FURNACE)
			)
		);
		registerMachineRecipe(output, ModBlocks.ALCHEMICAL_DYNAMO,
			" C "+"BMB"+"RJR",
			Map.of(
				'C', Ingredient.of(ModItems.CAPACITOR),
				'B', Ingredient.of(ModTags.Items.INGOTS_BRONZE),
				'M', Ingredient.of(Items.BREWING_STAND),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'J', Ingredient.of(ModItems.JAR)
			)
		);
		registerMachineRecipe(output, ModBlocks.CULINARY_DYNAMO,
			" C "+"IMI"+"RFR",
			Map.of(
				'C', Ingredient.of(ModItems.CAPACITOR),
				'I', Ingredient.of(Tags.Items.INGOTS_IRON),
				'M', Ingredient.of(Items.CAMPFIRE),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'F', Ingredient.of(ModTags.Items.INGOTS_BRONZE)
			)
		);
		registerMachineRecipe(output, ModMachines.ANVILINATOR,
			"ACA"+"IMI"+"RDR",
			Map.of(
				'A', Ingredient.of(Items.ANVIL),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'I', Ingredient.of(Tags.Items.INGOTS_IRON),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'D', Ingredient.of(Tags.Items.GEMS_DIAMOND)
			)
		);
		registerMachineRecipe(output, ModMachines.BOOK_DUPLICATOR,
			"BCB"+"IMI"+"RPR",
			Map.of(
				'B', Ingredient.of(Items.BOOK),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'I', Ingredient.of(ModTags.Items.INGOTS_TIN),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'P', Ingredient.of(Items.CRAFTING_TABLE)
			)
		);
		registerMachineRecipe(output, ModItems.TESSERACT,
			"EPE"+"DCD"+"EOE",
			Map.of(
				'E', Ingredient.of(ModItems.ENDER_TABLET),
				'P', Ingredient.of(Tags.Items.ENDER_PEARLS),
				'D', Ingredient.of(ModItems.DEMONIC_TABLET),
				'C', Ingredient.of(ModItems.CHLOROPHYTE_TABLET),
				'O', Ingredient.of(ModItems.OBLIVIUM_INGOT)
			), ModItems.OBLIVIUM_INGOT
		);
	}
	
	static void registerMachineRecipe(RecipeOutput output, ItemLike machine, String pattern, Map<Character, Ingredient> symbols) {
		registerMachineRecipe(output, machine, pattern, symbols, ModBlocks.MACHINE_BLOCK);
	}
	
	static void registerMachineRecipe(RecipeOutput output, ItemLike machine, String pattern, Map<Character, Ingredient> symbols, ItemLike unlockItem) {
		var builder = ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, machine)
			.pattern(pattern.substring(0, 3)).pattern(pattern.substring(3, 6)).pattern(pattern.substring(6, 9));
		var noDup = Arrays.stream(pattern.split(""))
			.distinct().collect(Collectors.joining());
		for (char c : noDup.toCharArray()) {
			if (symbols.containsKey(c)) {
				builder.define(c, symbols.get(c));
			}
		}
		builder.unlockedBy("has_item", has(unlockItem)).save(output);
	}
	
	static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike pItemLike) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(pItemLike));
	}
	static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... pItems) {
		return inventoryTrigger(Arrays.stream(pItems).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
	}
	static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... pPredicates) {
		return CriteriaTriggers.INVENTORY_CHANGED
			.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(pPredicates)));
	}
}
