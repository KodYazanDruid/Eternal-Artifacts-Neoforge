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
			"SPS"+"AMA"+"RCR",
			Map.of(
				'S', Ingredient.of(ModTags.Items.PLASTIC),
				'C', Ingredient.of(ModItems.CAPACITOR),
				'M', Ingredient.of(ModBlocks.MACHINE_BLOCK),
				'R', Ingredient.of(Tags.Items.DUSTS_REDSTONE),
				'P', Ingredient.of(Items.PISTON),
				'A', Ingredient.of(ModTags.Items.INGOTS_ALUMINUM)
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
	}
	
	static void registerMachineRecipe(RecipeOutput output, ItemLike machine, String pattern, Map<Character, Ingredient> symbols) {
		var builder = ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, machine)
			.pattern(pattern.substring(0, 3)).pattern(pattern.substring(3, 6)).pattern(pattern.substring(6, 9));
		var noDup = Arrays.stream(pattern.split(""))
			.distinct().collect(Collectors.joining());
		for (char c : noDup.toCharArray()) {
			if (symbols.containsKey(c)) {
				builder.define(c, symbols.get(c));
			}
		}
		builder.unlockedBy("has_item", has(ModBlocks.MACHINE_BLOCK)).save(output);
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
