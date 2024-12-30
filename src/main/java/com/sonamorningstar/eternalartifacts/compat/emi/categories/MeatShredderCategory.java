package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.MeatShredderRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class MeatShredderCategory extends EAEmiRecipe {
    public static final EmiRecipeCategory MEAT_SHREDDER_CATEGORY = createCategory(ModRecipes.MEAT_SHREDDING, ModMachines.MEAT_SHREDDER);
    public MeatShredderCategory(MeatShredderRecipe recipe, ResourceLocation id) {
        super(MEAT_SHREDDER_CATEGORY, id, 72, 18);
        inputs.add(EmiIngredient.of(recipe.getInput()));
        outputs.add(EmiStack.of(recipe.getOutput().getFluid(), recipe.getOutput().getAmount()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addFillingArrow(24, 0, 10000);
        widgets.addSlot(outputs.get(0).setAmount(outputs.get(0).getAmount()), 54, 0).recipeContext(this);
    }

    public static void fillRecipes(EmiRegistry registry) {
        for(MeatShredderRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.MEAT_SHREDDING.getType()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(recipe.getInput().getItems()[0].getItem());
            registry.addRecipe(new MeatShredderCategory(recipe, new ResourceLocation(MODID, ("meat_shredding/"+id.toString().replace(":", "/")))));
        }
    }
}
