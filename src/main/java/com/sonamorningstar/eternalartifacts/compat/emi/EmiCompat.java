package com.sonamorningstar.eternalartifacts.compat.emi;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.MaceratingCategory;
import com.sonamorningstar.eternalartifacts.compat.emi.categories.MeatPackerCategory;
import com.sonamorningstar.eternalartifacts.compat.emi.categories.MeatShredderCategory;
import com.sonamorningstar.eternalartifacts.compat.emi.categories.MobLiquifierCategory;
import com.sonamorningstar.eternalartifacts.compat.emi.recipes.EmiShapedRetexturedRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.MaceratingRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.MeatShredderRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.MobLiquifierRecipe;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
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
        registry.addCategory(MobLiquifierCategory.MOB_LIQUIFIER_CATEGORY);
        registry.addCategory(MaceratingCategory.MACERATING_CATEGORY);

        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ModMachines.ADVANCED_CRAFTER.getItem()));
        registry.addWorkstation(MeatPackerCategory.MEAT_PACKER_CATEGORY, EmiStack.of(ModMachines.MEAT_PACKER.getItem()));
        registry.addWorkstation(MeatShredderCategory.MEAT_SHREDDER_CATEGORY, EmiStack.of(ModMachines.MEAT_SHREDDER.getItem()));
        registry.addWorkstation(MobLiquifierCategory.MOB_LIQUIFIER_CATEGORY, EmiStack.of(ModMachines.MOB_LIQUIFIER.getItem()));
        registry.addWorkstation(MaceratingCategory.MACERATING_CATEGORY, EmiStack.of(ModMachines.INDUSTRIAL_MACERATOR.getItem()));

        registry.addRecipe(new MeatPackerCategory());

        RecipeManager manager = registry.getRecipeManager();
        for(MeatShredderRecipe recipe : manager.getAllRecipesFor(ModRecipes.MEAT_SHREDDING.getType()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(recipe.getInput().getItems()[0].getItem());
            registry.addRecipe(new MeatShredderCategory(recipe, new ResourceLocation(MODID, ("meat_shredding/"+id.toString().replace(":", "/")))));
        }
        for(MobLiquifierRecipe recipe : manager.getAllRecipesFor(ModRecipes.MOB_LIQUIFYING.getType()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(recipe.getEntity().getEntityTypes()[0]);
            registry.addRecipe(new MobLiquifierCategory(recipe, new ResourceLocation(MODID, ("mob_liquifying/"+id.toString().replace(":", "/")))));
        }
        for(MaceratingRecipe recipe : manager.getAllRecipesFor(ModRecipes.MACERATING.getType()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(recipe.getInput().getItems()[0].getItem());
            registry.addRecipe(new MaceratingCategory(recipe, new ResourceLocation(MODID, ("macerating/"+id.toString().replace(":", "/")))));
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

/*    private static <C extends Container, R extends Recipe<C>> void addRecipe(EmiRegistry registry, EmiRecipeCategory category, RecipeType<R> type, EmiIngredient input, EmiStack output) {
        RecipeManager manager = registry.getRecipeManager();
        for(R recipe : manager.getAllRecipesFor(type).stream().map(RecipeHolder::value).toList()) {
            //ResourceLocation id = BuiltInRegistries.ITEM.getKey(recipe.getInput().getItems()[0].getItem());
            String recipePath = BuiltInRegistries.RECIPE_TYPE.getKey(type).getPath();
            registry.addRecipe(new BasicCategory(category, input, output, new ResourceLocation(MODID, (recipePath+"/"+input..toString().replace(":", "/")))), 72, 18);
        }
    }*/
}
