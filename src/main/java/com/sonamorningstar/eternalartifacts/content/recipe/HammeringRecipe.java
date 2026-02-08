package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

@RequiredArgsConstructor
public class HammeringRecipe implements Recipe<SimpleContainer> {
    private final Block block;
    private final ItemStack output;

    @Override
    public boolean matches(SimpleContainer con, Level lvl) {
        for (ItemStack item : con.getItems()) {
            if (item.getItem() instanceof BlockItem bi) {
                return bi.getBlock().equals(block);
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer con, RegistryAccess reg) {
        return getResultItem(reg).copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.HAMMERING.getSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.HAMMERING.getType();
    }

    public static class Serializer implements RecipeSerializer<HammeringRecipe> {
        private static final Codec<HammeringRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Block.CODEC.fieldOf("block").forGetter(r -> r.block),
                ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
        ).apply(inst, HammeringRecipe::new));

        @Override
        public Codec<HammeringRecipe> codec() {
            return CODEC;
        }

        @Override
        public HammeringRecipe fromNetwork(FriendlyByteBuf buff) {
            ItemStack output = buff.readItem();
            Block block = buff.readById(BuiltInRegistries.BLOCK::byId);
            return new HammeringRecipe(block, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, HammeringRecipe recipe) {
            buff.writeById(BuiltInRegistries.BLOCK::getId, recipe.block);
            buff.writeItem(recipe.output);
        }
    }
}
