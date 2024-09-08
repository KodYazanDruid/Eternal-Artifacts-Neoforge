package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.base.BasicItemToItemRecipe;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

@Getter
public class SqueezingRecipe extends BasicItemToItemRecipe {
    private final FluidStack outputFluid;

    public SqueezingRecipe(Ingredient input, ItemStack output, FluidStack outputFluid) {
        super(ModRecipes.SQUEEZING, input, output);
        this.outputFluid = outputFluid;
    }

    public static class Serializer implements RecipeSerializer<SqueezingRecipe> {
        private final Codec<SqueezingRecipe> CODEC = RecordCodecBuilder.create(inst ->  inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(r -> r.input),
                ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output),
                FluidStack.CODEC.fieldOf("outputFluid").forGetter(r -> r.outputFluid)
        ).apply(inst, SqueezingRecipe::new));

        @Override
        public Codec<SqueezingRecipe> codec() {return CODEC;}

        @Override
        public SqueezingRecipe fromNetwork(FriendlyByteBuf buff) {
            Ingredient input = Ingredient.fromNetwork(buff);
            ItemStack output = buff.readItem();
            FluidStack outputFluid = FluidStack.readFromPacket(buff);
            return new SqueezingRecipe(input, output, outputFluid);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, SqueezingRecipe recipe) {
            recipe.input.toNetwork(buff);
            buff.writeItem(recipe.output);
            recipe.outputFluid.writeToPacket(buff);
        }
    }
}
