package com.sonamorningstar.eternalartifacts.data.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.Optional;
import java.util.function.Supplier;

public class SmeltDropsModifier extends LootModifier {
	public static final Supplier<Codec<SmeltDropsModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(instance ->
		codecStart(instance).apply(instance, SmeltDropsModifier::new)));
	
	public SmeltDropsModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}
	
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		ServerLevel level = context.getLevel();
		RecipeManager recipeManager = level.getRecipeManager();
		ObjectArrayList<ItemStack> smeltedLoot = new ObjectArrayList<>();
		for (ItemStack loot : generatedLoot) {
			Container dummy = new SimpleContainer(loot);
			Optional<RecipeHolder<SmeltingRecipe>> recipeOptional = recipeManager
				.getRecipeFor(RecipeType.SMELTING, dummy, level);
			if (recipeOptional.isPresent()) {
				SmeltingRecipe recipe = recipeOptional.get().value();
				ItemStack result = recipe.assemble(dummy, level.registryAccess());
				result.setCount(loot.getCount() * result.getCount());
				float xp = recipe.getExperience();
				ExperienceOrb.award(level, context.getParam(LootContextParams.ORIGIN), Mth.floor(xp));
				smeltedLoot.add(result);
			} else smeltedLoot.add(loot);
		}
		return smeltedLoot;
	}
	
	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}
}
