package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.base.NoResultItemRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.IngredientUtils;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

@Getter
@RequiredArgsConstructor
public class MeltingRecipe extends NoResultItemRecipe<SimpleContainer> {
    private final Ingredient input;
    private final FluidStack output;

    @Override
    public boolean matches(SimpleContainer container, Level lvl) {
        for(ItemStack stack : container.getItems()) {
            if (IngredientUtils.canIngredientSustain(stack, input)){
                return true;
            }
        }
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {return ModRecipes.MELTING.getSerializer();}

    @Override
    public RecipeType<?> getType() {return ModRecipes.MELTING.getType();}

    public static class Serializer implements RecipeSerializer<MeltingRecipe> {
        private final Codec<MeltingRecipe> CODEC = RecordCodecBuilder.create(inst ->  inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(r -> r.input),
                FluidStack.CODEC.fieldOf("output").forGetter(r -> r.output)
        ).apply(inst, MeltingRecipe::new));

        @Override
        public Codec<MeltingRecipe> codec() {return CODEC;}

        @Override
        public MeltingRecipe fromNetwork(FriendlyByteBuf buff) {
            Ingredient input = Ingredient.fromNetwork(buff);
            FluidStack output = FluidStack.readFromPacket(buff);
            return new MeltingRecipe(input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, MeltingRecipe recipe) {
            recipe.input.toNetwork(buff);
            recipe.output.writeToPacket(buff);
        }
    }
}
