package com.sonamorningstar.eternalartifacts.compat.emi;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.MeatPackerCategory;
import com.sonamorningstar.eternalartifacts.compat.emi.categories.MeatShredderCategory;
import com.sonamorningstar.eternalartifacts.compat.emi.recipes.EmiShapedRetexturedRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.MeatShredderRecipe;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MeatPackerCategory.MEAT_PACKER_CATEGORY);
        registry.addCategory(MeatShredderCategory.MEAT_SHREDDER_CATEGORY);

        registry.addWorkstation(MeatPackerCategory.MEAT_PACKER_CATEGORY, EmiStack.of(ModBlocks.MEAT_PACKER));
        registry.addWorkstation(MeatShredderCategory.MEAT_SHREDDER_CATEGORY, EmiStack.of(ModBlocks.MEAT_SHREDDER));

        registry.addRecipe(new MeatPackerCategory());

        RecipeManager manager = registry.getRecipeManager();
        for(MeatShredderRecipe recipe : manager.getAllRecipesFor(ModRecipes.MEAT_SHREDDING_TYPE.get()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(recipe.getInput().getItems()[0].getItem());
            registry.addRecipe(new MeatShredderCategory(recipe, new ResourceLocation(MODID, ("meat_shredding/"+id.toString().replace(":", "/")))));
        }

        List<Item> items = BuiltInRegistries.ITEM.getTag(ModTags.Items.GARDENING_POT_SUITABLE)
                .map(holders -> holders.stream().map(Holder::value).toList()).orElseGet(ArrayList::new);
        for(Item texture : items){
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(texture);
            ItemStack stack = new ItemStack(ModItems.GARDENING_POT.get());
            CompoundTag tag = stack.getOrCreateTag();
            RetexturedHelper.setTexture(tag, id.toString());
            stack.setTag(tag);
            registry.addRecipe(new EmiShapedRetexturedRecipe(new ResourceLocation(MODID, ("shaped_retextured_recipe/gardening_pot_"+id).replace(":", "_")), texture, stack));
        }

    }
}
