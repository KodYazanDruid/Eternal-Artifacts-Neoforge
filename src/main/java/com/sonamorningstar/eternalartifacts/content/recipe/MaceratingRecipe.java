package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.base.BasicItemToItemRecipe;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MaceratingRecipe extends BasicItemToItemRecipe {

    public MaceratingRecipe(Ingredient input, ItemStack output) {
        super(ModRecipes.MACERATING, input, output);
    }

    public static class Serializer implements RecipeSerializer<MaceratingRecipe> {
        private final Codec<MaceratingRecipe> CODEC = RecordCodecBuilder.create(inst ->  inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(r -> r.input),
                ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
        ).apply(inst, MaceratingRecipe::new));

        @Override
        public Codec<MaceratingRecipe> codec() {return CODEC;}

        @Override
        public MaceratingRecipe fromNetwork(FriendlyByteBuf buff) {
            Ingredient input = Ingredient.fromNetwork(buff);
            ItemStack output = buff.readItem();
            return new MaceratingRecipe(input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, MaceratingRecipe recipe) {
            recipe.input.toNetwork(buff);
            buff.writeItem(recipe.output);
        }
    }
}
