package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

@Getter
@RequiredArgsConstructor
public class SolidifierRecipe implements Recipe<SimpleFluidContainer> {

    private final FluidIngredient inputFluid;
    private final ItemStack output;

    @Override
    public boolean matches(SimpleFluidContainer con, Level lvl) {
        for (FluidStack stack : con.getFluidStacks()) {
            if (inputFluid.canSustain(stack)) return true;
        }
        return false;
    }

    @Override
    public ItemStack assemble(SimpleFluidContainer pContainer, RegistryAccess pRegistryAccess) {return getResultItem(pRegistryAccess).copy();}
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {return false;}
    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {return getOutput();}
    @Override
    public RecipeSerializer<?> getSerializer() {return ModRecipes.SOLIDIFYING.getSerializer();}
    @Override
    public RecipeType<?> getType() {return ModRecipes.SOLIDIFYING.getType();}

    public static class Serializer implements RecipeSerializer<SolidifierRecipe> {
        private static final Codec<SolidifierRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                FluidIngredient.CODEC_NONEMPTY.fieldOf("inputFluid").forGetter(r -> r.inputFluid),
                ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
        ).apply(inst, SolidifierRecipe::new));

        @Override
        public Codec<SolidifierRecipe> codec() {
            return CODEC;
        }

        @Override
        public SolidifierRecipe fromNetwork(FriendlyByteBuf buff) {
            FluidIngredient fluidInput = FluidIngredient.fromNetwork(buff);
            ItemStack output = buff.readItem();
            return new SolidifierRecipe(fluidInput, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, SolidifierRecipe recipe) {
            recipe.inputFluid.toNetwork(buff);
            buff.writeItem(recipe.output);
        }
    }
}
