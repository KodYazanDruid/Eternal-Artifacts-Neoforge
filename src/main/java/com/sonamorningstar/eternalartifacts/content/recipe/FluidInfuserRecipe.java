package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.container.ItemFluidContainer;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class FluidInfuserRecipe implements Recipe<ItemFluidContainer> {

    private final FluidIngredient inputFluid;
    private final Ingredient input;
    private final ItemStack output;

    @Override
    public boolean matches(ItemFluidContainer con, Level level) {
        boolean fluidcheck = false;
        for(FluidStack stack : con.getFluidStacks()) {
            if(inputFluid.test(stack)) fluidcheck = true;
        }
        boolean itemCheck = false;
        for (ItemStack stack : con.getItemStacks()) {
            if (input.test(stack)) itemCheck = true;
        }
        return fluidcheck && itemCheck;
    }

    @Override
    public ItemStack assemble(ItemFluidContainer pContainer, RegistryAccess pRegistryAccess) {return getResultItem(pRegistryAccess);}
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {return false;}
    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {return output;}

    @Override
    public RecipeSerializer<?> getSerializer() {return ModRecipes.FLUID_INFUSING.getSerializer();}

    @Override
    public RecipeType<?> getType() {return ModRecipes.FLUID_INFUSING.getType();}

    public static class Serializer implements RecipeSerializer<FluidInfuserRecipe> {
        private static final Codec<FluidInfuserRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                FluidIngredient.CODEC_NONEMPTY.fieldOf("inputFluid").forGetter(r -> r.inputFluid),
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
            ).apply(inst, FluidInfuserRecipe::new));

        @Override
        public Codec<FluidInfuserRecipe> codec() {return CODEC;}

        @Override
        public FluidInfuserRecipe fromNetwork(FriendlyByteBuf buff) {
            FluidIngredient inputFluid = FluidIngredient.fromNetwork(buff);
            Ingredient input = Ingredient.fromNetwork(buff);
            ItemStack output = buff.readItem();
            return new FluidInfuserRecipe(inputFluid, input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, FluidInfuserRecipe recipe) {
            recipe.inputFluid.toNetwork(buff);
            recipe.input.toNetwork(buff);
            buff.writeItem(recipe.output);
        }
    }
}
