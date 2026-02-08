package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.IngredientUtils;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class MeatShredderRecipe implements Recipe<SimpleContainer> {
    private final Ingredient input;
    private final FluidStack output;

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        for(ItemStack stack : container.getItems()) {
            if (IngredientUtils.canIngredientSustain(stack, input)){
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {return getResultItem(pRegistryAccess).copy();}
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {return false;}
    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {return ItemStack.EMPTY;}
    @Override
    public RecipeType<?> getType() {return ModRecipes.MEAT_SHREDDING.getType();}
    @Override
    public RecipeSerializer<?> getSerializer() {return ModRecipes.MEAT_SHREDDING.getSerializer();}

    public static class Serializer implements RecipeSerializer<MeatShredderRecipe> {
        private static final Codec<MeatShredderRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
                FluidStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output)
        ).apply(instance, MeatShredderRecipe::new));

        @Override
        public Codec<MeatShredderRecipe> codec() {
            return CODEC;
        }

        @Override
        public MeatShredderRecipe fromNetwork(FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            FluidStack output = buffer.readFluidStack();
            return new MeatShredderRecipe(input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MeatShredderRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeFluidStack(recipe.output);
        }
    }
}
