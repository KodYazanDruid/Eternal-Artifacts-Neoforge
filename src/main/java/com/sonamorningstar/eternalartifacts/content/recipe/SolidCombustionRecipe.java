package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.base.DynamoRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.base.NoResultItemRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.IngredientUtils;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

@Getter
@RequiredArgsConstructor
public class SolidCombustionRecipe extends NoResultItemRecipe<SimpleContainer> implements DynamoRecipe {
	private final Ingredient fuel;
	private final int generation;
	private final int duration;
	
	@Override
	public boolean matches(SimpleContainer con, Level lvl) {
		for (ItemStack item : con.getItems()) {
			if (IngredientUtils.canIngredientSustain(item, fuel)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.SOLID_COMBUSTING.getSerializer();
	}
	
	@Override
	public RecipeType<?> getType() {
		return ModRecipes.SOLID_COMBUSTING.getType();
	}
	
	public static class Serializer implements RecipeSerializer<SolidCombustionRecipe> {
		private static final Codec<SolidCombustionRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
			Ingredient.CODEC_NONEMPTY.fieldOf("fuel").forGetter(r -> r.fuel),
			Codec.INT.fieldOf("generation").forGetter(r -> r.generation),
			Codec.INT.fieldOf("duration").forGetter(r -> r.duration)
		).apply(inst, SolidCombustionRecipe::new));
		
		@Override
		public Codec<SolidCombustionRecipe> codec() {
			return CODEC;
		}
		
		@Override
		public SolidCombustionRecipe fromNetwork(FriendlyByteBuf buff) {
			Ingredient fuel = Ingredient.fromNetwork(buff);
			int generation = buff.readVarInt();
			int duration = buff.readVarInt();
			return new SolidCombustionRecipe(fuel, generation, duration);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf buff, SolidCombustionRecipe recipe) {
			recipe.fuel.toNetwork(buff);
			buff.writeVarInt(recipe.generation);
			buff.writeVarInt(recipe.duration);
		}
	}
}
