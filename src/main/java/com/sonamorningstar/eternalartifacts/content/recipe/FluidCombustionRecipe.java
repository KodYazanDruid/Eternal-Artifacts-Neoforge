package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class FluidCombustionRecipe implements Recipe<SimpleContainer> {

    @Getter
    private final Fluid fuel;
    @Getter
    private final int generation;
    @Getter
    private final int duration;

    public boolean matches(Fluid fluid) {
        return fluid.isSame(fuel);
    }

    @Override
    public boolean matches(SimpleContainer con, Level level) {
        return false;
    }
    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {return ItemStack.EMPTY; }
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {return false;}
    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {return ItemStack.EMPTY;}
    @Override
    public RecipeSerializer<?> getSerializer() {return ModRecipes.FLUID_COMBUSTING_SERIALIZER.get();}
    @Override
    public RecipeType<?> getType() {return ModRecipes.FLUID_COMBUSTING_TYPE.get();}

    public static class Serializer implements RecipeSerializer<FluidCombustionRecipe> {
        private static final Codec<FluidCombustionRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("fuel").forGetter(r -> r.fuel),
                Codec.INT.fieldOf("generation").forGetter(r -> r.generation),
                Codec.INT.fieldOf("duration").forGetter(r -> r.duration)
        ).apply(inst, FluidCombustionRecipe::new));

        @Override
        public Codec<FluidCombustionRecipe> codec() {
            return CODEC;
        }

        @Override
        public FluidCombustionRecipe fromNetwork(FriendlyByteBuf buff) {
            Fluid fuel = buff.readById(BuiltInRegistries.FLUID::byId);
            int generation = buff.readVarInt();
            int duration = buff.readVarInt();
            return new FluidCombustionRecipe(fuel, generation, duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, FluidCombustionRecipe recipe) {
            buff.writeById(BuiltInRegistries.FLUID::getId, recipe.fuel);
            buff.writeVarInt(recipe.generation);
            buff.writeVarInt(recipe.duration);
        }
    }
}
