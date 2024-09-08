package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.base.NoResultItemRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class FluidCombustionRecipe extends NoResultItemRecipe<SimpleFluidContainer> {

    private final FluidIngredient fuel;
    private final int generation;
    private final int duration;

    @Override
    public boolean matches(SimpleFluidContainer con, Level level) {
        for(FluidStack stack : con.getFluidStacks()) {
            if(fuel.test(stack)) return true;
        }
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {return ModRecipes.FLUID_COMBUSTING.getSerializer();}
    @Override
    public RecipeType<?> getType() {return ModRecipes.FLUID_COMBUSTING.getType();}

    public static class Serializer implements RecipeSerializer<FluidCombustionRecipe> {
        private static final Codec<FluidCombustionRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                FluidIngredient.CODEC.fieldOf("fuel").forGetter(r -> r.fuel),
                Codec.INT.fieldOf("generation").forGetter(r -> r.generation),
                Codec.INT.fieldOf("duration").forGetter(r -> r.duration)
        ).apply(inst, FluidCombustionRecipe::new));

        @Override
        public Codec<FluidCombustionRecipe> codec() {
            return CODEC;
        }

        @Override
        public FluidCombustionRecipe fromNetwork(FriendlyByteBuf buff) {
            FluidIngredient fuel = FluidIngredient.fromNetwork(buff);
            int generation = buff.readVarInt();
            int duration = buff.readVarInt();
            return new FluidCombustionRecipe(fuel, generation, duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, FluidCombustionRecipe recipe) {
            recipe.fuel.toNetwork(buff);
            buff.writeVarInt(recipe.generation);
            buff.writeVarInt(recipe.duration);
        }
    }
}
