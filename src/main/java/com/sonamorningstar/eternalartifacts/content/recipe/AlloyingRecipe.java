package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.base.BasicRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.SizedIngredient;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AlloyingRecipe extends BasicRecipe {
    private final List<SizedIngredient> inputs;
    private final ItemStack output;

    public AlloyingRecipe(List<SizedIngredient> inputs, ItemStack output) {
        super(ModRecipes.ALLOYING);
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public boolean matches(SimpleContainer con, Level lvl) {
        boolean[] matched = new boolean[3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (matched[j]) continue;
                ItemStack item = con.getItem(i);
                if (j < inputs.size()) {
                    SizedIngredient ingredient = inputs.get(j);
                    if (ingredient.canBeSustained(item)) matched[j] = true;
                }
                else if (item.isEmpty() || item.is(ModItems.SLOT_LOCK)) matched[j] = true;
            }
        }
        for (int i = 0; i < 3; i++) if (!matched[i]) return false;
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess reg) {return output;}

    public static class Serializer implements RecipeSerializer<AlloyingRecipe> {
        private final Codec<AlloyingRecipe> CODEC = RecordCodecBuilder.create(inst ->  inst.group(
                SizedIngredient.LIST_CODEC.fieldOf("inputs").forGetter(r -> r.inputs),
                ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
        ).apply(inst, AlloyingRecipe::new));

        @Override
        public Codec<AlloyingRecipe> codec() {return CODEC;}

        @Override
        public AlloyingRecipe fromNetwork(FriendlyByteBuf buff) {
            List<SizedIngredient> inputs = buff.readCollection(ArrayList::new, SizedIngredient::fromNetwork);
            ItemStack output = buff.readItem();
            return new AlloyingRecipe(inputs, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, AlloyingRecipe recipe) {
            buff.writeCollection(recipe.inputs, (wr, ing) -> ing.toNetwork(buff));
            buff.writeItem(recipe.output);
        }
    }

}
