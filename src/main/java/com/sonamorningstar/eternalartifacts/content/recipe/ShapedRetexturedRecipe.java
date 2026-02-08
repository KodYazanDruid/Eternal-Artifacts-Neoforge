package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.item.block.base.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class ShapedRetexturedRecipe extends CustomRecipe {
    private final Item item;
    private final TagKey<Item> texture;
    private ItemStack result;

    public ShapedRetexturedRecipe(CraftingBookCategory pCategory, Item item, TagKey<Item> texture) {
        super(pCategory);
        this.item = item;
        this.texture = texture;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level pLevel) {
        return !getOutput(inv).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess pRegistryAccess) {
        return getOutput(inv);
    }

    private ItemStack getOutput(CraftingContainer inv) {
        if(!(item instanceof RetexturedBlockItem)) return ItemStack.EMPTY;
        //this is a lot of checks
        if(inv.getItem(0).isEmpty() && inv.getItem(2).isEmpty() &&
            inv.getItem(6).isEmpty() && inv.getItem(8).isEmpty() &&
            inv.getItem(1).is(Items.BONE_MEAL) &&
            inv.getItem(3).is(texture) &&
            inv.getItem(4).is(Items.DIRT) &&
            inv.getItem(5).is(texture) &&
            inv.getItem(7).is(texture) &&
            inv.getItem(3).is(inv.getItem(5).getItem()) && inv.getItem(5).is(inv.getItem(7).getItem())
        ) {
            ItemStack result = new ItemStack(item);
            if(inv.getItem(3).getItem() instanceof BlockItem bi) result = RetexturedBlockItem.setTexture(result, bi.getBlock());
            this.result = result;
            return result;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result != null ? result : ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= 3 && pHeight >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SHAPED_RETEXTURED_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<ShapedRetexturedRecipe> {
        private static final Codec<ShapedRetexturedRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(recipe -> recipe.item),
                TagKey.codec(BuiltInRegistries.ITEM.key()).fieldOf("texture").forGetter(recipe -> recipe.texture)
            ).apply(instance, ShapedRetexturedRecipe::new));

        @Override
        public Codec<ShapedRetexturedRecipe> codec() {
            return CODEC;
        }

        @Override
        public ShapedRetexturedRecipe fromNetwork(FriendlyByteBuf buffer) {
            CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
            Item item = buffer.readById(BuiltInRegistries.ITEM::byId);
            TagKey<Item> texture = buffer.readJsonWithCodec(TagKey.codec(BuiltInRegistries.ITEM.key()));
            return new ShapedRetexturedRecipe(category, item, texture);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapedRetexturedRecipe recipe) {
            buffer.writeEnum(recipe.category());
            buffer.writeById(BuiltInRegistries.ITEM::getId, recipe.item);
            buffer.writeJsonWithCodec(TagKey.codec(BuiltInRegistries.ITEM.key()), recipe.texture);
        }
    }
}
