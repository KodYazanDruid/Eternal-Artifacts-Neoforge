package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.base.AbstractFluidRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.extensions.IFriendlyByteBufExtension;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Map;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class OilRefineryRecipe extends AbstractFluidRecipe {

    private final FluidIngredient input;
    private final FluidStack output;
    private final FluidStack secondaryOutput;
    private final NonNullList<ItemStack> itemOutputs;
    private final NonNullList<Float> chances;

    @Override
    public boolean matches(SimpleFluidContainer con, Level level) {
        for(FluidStack stack : con.getFluidStacks()) {
            if(input.test(stack) && input.getFluidStacks()[0].getAmount() <= stack.getAmount()) return true;
        }
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {return ModRecipes.OIL_REFINERY_SERIALIZER.get();}

    @Override
    public RecipeType<?> getType() {return ModRecipes.OIL_REFINERY_TYPE.get();}

    public static class Serializer implements RecipeSerializer<OilRefineryRecipe> {
        private static final Codec<OilRefineryRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                FluidIngredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                FluidStack.CODEC.fieldOf("output").forGetter(r -> r.output),
                FluidStack.CODEC.fieldOf("secondaryOutput").forGetter(r -> r.secondaryOutput),
                NonNullList.codecOf(ItemStack.CODEC).fieldOf("itemOutputs").forGetter(r -> r.itemOutputs),
                NonNullList.codecOf(Codec.FLOAT).fieldOf("chances").forGetter(r -> r.chances)

        ).apply(inst, OilRefineryRecipe::new));

        @Override
        public Codec<OilRefineryRecipe> codec() {
            return CODEC;
        }

        @Override
        public OilRefineryRecipe fromNetwork(FriendlyByteBuf buff) {
            FluidIngredient input = FluidIngredient.fromNetwork(buff);
            FluidStack output = FluidStack.readFromPacket(buff);
            FluidStack secondaryOutput = FluidStack.readFromPacket(buff);
            NonNullList<ItemStack> itemOutputs = buff.readCollection(NonNullList::createWithCapacity, IFriendlyByteBufExtension::readItemWithLargeCount);
            NonNullList<Float> chances = buff.readCollection(NonNullList::createWithCapacity, FriendlyByteBuf::readFloat);
            return new OilRefineryRecipe(input, output, secondaryOutput, itemOutputs, chances);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, OilRefineryRecipe recipe) {
            recipe.input.toNetwork(buff);
            buff.writeFluidStack(recipe.output);
            buff.writeFluidStack(recipe.secondaryOutput);
            buff.writeCollection(recipe.itemOutputs, IFriendlyByteBufExtension::writeItemWithLargeCount);
            buff.writeCollection(recipe.chances, FriendlyByteBuf::writeFloat);
        }
    }
}
