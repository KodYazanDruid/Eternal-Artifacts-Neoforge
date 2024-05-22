package com.sonamorningstar.eternalartifacts.data.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class MeatPackerRecipe implements Recipe<SimpleContainer> {
    private final ItemStack output;
    @Getter
    private final TagKey<Fluid> fluidIn;
    @Getter
    private final int fluidAmount;

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        return fluidAmount >= 250;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MEAT_PACKER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Type implements RecipeType<MeatPackerRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "meat_packer";

        @Override
        public String toString() {
            return new ResourceLocation(MODID, ID).toString();
        }
    }

    public static class Serializer implements RecipeSerializer<MeatPackerRecipe> {
        private static final Codec<MeatPackerRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
                TagKey.codec(Registries.FLUID).fieldOf("fluidIn").forGetter(recipe -> recipe.fluidIn),
                Codec.INT.fieldOf("amount").forGetter(recipe -> recipe.fluidAmount)
        ).apply(instance, MeatPackerRecipe::new));

        @Override
        public Codec<MeatPackerRecipe> codec() {
            return CODEC;
        }

        @Override
        public MeatPackerRecipe fromNetwork(FriendlyByteBuf pBuffer) {
            ItemStack output = pBuffer.readItem();
            TagKey<Fluid> fluidIn = pBuffer.readJsonWithCodec(TagKey.codec(Registries.FLUID));
            int fluidAmount = pBuffer.readInt();
            return new MeatPackerRecipe(output, fluidIn, fluidAmount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, MeatPackerRecipe pRecipe) {
            pBuffer.writeItem(pRecipe.output);
            pBuffer.writeJsonWithCodec(TagKey.codec(Registries.FLUID), pRecipe.fluidIn);
            pBuffer.writeInt(pRecipe.fluidAmount);
        }
    }
}
