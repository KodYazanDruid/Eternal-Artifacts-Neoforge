package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
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

@Getter
@RequiredArgsConstructor
public class MaceratingRecipe implements Recipe<SimpleContainer> {
    private final Ingredient input;
    private final ItemStack output;

    @Override
    public boolean matches(SimpleContainer container, Level lvl) {
        NonNullList<ItemStack> stacks = container.getItems();
        for(ItemStack stack : stacks) { return input.test(stack); }
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {return getResultItem(pRegistryAccess);}

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {return false;}

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {return output;}

    @Override
    public RecipeSerializer<?> getSerializer() {return ModRecipes.MACERATING.getSerializer();}

    @Override
    public RecipeType<?> getType() {return ModRecipes.MACERATING.getType();}

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
