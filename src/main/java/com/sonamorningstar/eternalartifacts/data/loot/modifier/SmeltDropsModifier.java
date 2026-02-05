package com.sonamorningstar.eternalartifacts.data.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.phys.Vec3;
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
		Vec3 vecPos = context.getParam(LootContextParams.ORIGIN);
		boolean smelted = false;
		for (ItemStack loot : generatedLoot) {
			Container dummy = new SimpleContainer(loot);
			Optional<RecipeHolder<SmeltingRecipe>> recipeOptional = recipeManager
				.getRecipeFor(RecipeType.SMELTING, dummy, level);
			if (recipeOptional.isPresent()) {
				SmeltingRecipe recipe = recipeOptional.get().value();
				ItemStack result = recipe.assemble(dummy, level.registryAccess());
				result.setCount(loot.getCount() * result.getCount());
				int count = result.getCount();
				float experience = recipe.getExperience();
				int totalXp = 0;
				for (int i = 0; i < count; i++) {
					totalXp += Mth.floor(experience);
					float fractional = Mth.frac(experience);
					if (fractional != 0.0F && Math.random() < (double) fractional) {
						totalXp++;
					}
				}
				if (totalXp > 0) {
					ExperienceOrb.award(level, vecPos, totalXp);
				}
				smeltedLoot.add(result);
				smelted = true;
			} else smeltedLoot.add(loot);
		}
		if (smelted)
			level.sendParticles(ParticleTypes.FLAME, vecPos.x, vecPos.y, vecPos.z,
				10, 0.5, 0.5, 0.5, 0.0);
		return smeltedLoot;
	}
	
	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}
}
