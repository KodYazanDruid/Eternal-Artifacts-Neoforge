package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.container.ItemFluidContainer;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.SizedIngredient;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public record FluidMixingRecipe(FluidIngredient fluidInput1, FluidIngredient fluidInput2, SizedIngredient itemInput,
								FluidStack output) implements Recipe<ItemFluidContainer> {
	
	@Override
	public boolean matches(ItemFluidContainer con, Level level) {
		if (!itemInput.isEmpty() && !itemInput.canBeSustained(con.getItem(0))) {
			return false;
		}
		
		if (fluidInput1.isEmpty() && fluidInput2.isEmpty()) {
			return false;
		}
		
		if (fluidInput1.isEmpty()) {
			return fluidInput2.canSustain(con.getFluidstack(0)) || fluidInput2.canSustain(con.getFluidstack(1));
		}
		
		if (fluidInput2.isEmpty()) {
			return fluidInput1.canSustain(con.getFluidstack(0)) || fluidInput1.canSustain(con.getFluidstack(1));
		}
		
		return (fluidInput1.canSustain(con.getFluidstack(0)) && fluidInput2.canSustain(con.getFluidstack(1))) ||
			(fluidInput1.canSustain(con.getFluidstack(1)) && fluidInput2.canSustain(con.getFluidstack(0)));
	}
	
	@Override
	public ItemStack assemble(ItemFluidContainer con, RegistryAccess reg) {
		return getResultItem(reg);
	}
	
	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return false;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess reg) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.FLUID_MIXING.getSerializer();
	}
	
	@Override
	public RecipeType<?> getType() {
		return ModRecipes.FLUID_MIXING.getType();
	}
	
	public static class Serializer implements RecipeSerializer<FluidMixingRecipe> {
		private static final Codec<FluidMixingRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
			FluidIngredient.CODEC.fieldOf("fluidInput1").forGetter(r -> r.fluidInput1),
			FluidIngredient.CODEC.fieldOf("fluidInput2").forGetter(r -> r.fluidInput2),
			SizedIngredient.CODEC.fieldOf("itemInput").forGetter(r -> r.itemInput),
			FluidStack.CODEC.fieldOf("output").forGetter(r -> r.output)
		).apply(inst, FluidMixingRecipe::new));
		
		@Override
		public Codec<FluidMixingRecipe> codec() {
			return CODEC;
		}
		
		@Override
		public FluidMixingRecipe fromNetwork(FriendlyByteBuf buff) {
			FluidIngredient inputFluid1 = FluidIngredient.fromNetwork(buff);
			FluidIngredient inputFluid2 = FluidIngredient.fromNetwork(buff);
			SizedIngredient input = SizedIngredient.fromNetwork(buff);
			FluidStack output = buff.readFluidStack();
			return new FluidMixingRecipe(inputFluid1, inputFluid2, input, output);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf buff, FluidMixingRecipe recipe) {
			recipe.fluidInput1.toNetwork(buff);
			recipe.fluidInput2.toNetwork(buff);
			recipe.itemInput.toNetwork(buff);
			buff.writeFluidStack(recipe.output);
		}
	}
}
