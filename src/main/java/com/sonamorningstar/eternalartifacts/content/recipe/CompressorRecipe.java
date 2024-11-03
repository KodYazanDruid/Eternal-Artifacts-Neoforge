package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.base.BasicRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.SizedIngredient;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

@Getter
public class CompressorRecipe extends BasicRecipe {
    private final SizedIngredient input;
    private final ItemStack output;

    public CompressorRecipe(SizedIngredient input, ItemStack output) {
        super(ModRecipes.COMPRESSING);
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(SimpleContainer con, Level level) {
        NonNullList<ItemStack> stacks = con.getItems();
        for (ItemStack stack : stacks) {
            return input.canBeSustained(stack);
        }
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess reg) {
        return output;
    }

    public static class Serializer implements RecipeSerializer<CompressorRecipe> {
        private final Codec<CompressorRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                SizedIngredient.CODEC_NONEMPTY.fieldOf("input").forGetter(r -> r.input),
                ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
        ).apply(inst, CompressorRecipe::new));

        @Override
        public Codec<CompressorRecipe> codec() {
            return CODEC;
        }

        @Override
        public CompressorRecipe fromNetwork(FriendlyByteBuf buff) {
            SizedIngredient input = SizedIngredient.fromNetwork(buff);
            ItemStack output = buff.readItem();
            return new CompressorRecipe(input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, CompressorRecipe recipe) {
            recipe.input.toNetwork(buff);
            buff.writeItem(recipe.output);
        }
    }
}
