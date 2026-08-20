package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.SqueezingRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class SqueezingCategory extends EAEmiRecipe {
    public static final EmiRecipeCategory SQUEEZING_CATEGORY = createCategory(ModRecipes.SQUEEZING, ModMachines.MATERIAL_SQUEEZER);
    private final int fluidAmount;
    
    public SqueezingCategory(SqueezingRecipe recipe, ResourceLocation id) {
        super(SQUEEZING_CATEGORY, id, 94, 56);
        inputs.add(EmiIngredient.of(recipe.getInput()));
        outputs.add(EmiStack.of(recipe.getOutput()));
        this.fluidAmount = recipe.getOutputFluid().getAmount();
        outputs.add(EmiStack.of(recipe.getOutputFluid().getFluid(), fluidAmount));
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        widgetHolder.addSlot(inputs.get(0), 0, 19);
        widgetHolder.addFillingArrow(24, 19, 10000);
        widgetHolder.addSlot(outputs.get(0), 52, 19).recipeContext(this);
        addLargeTank(widgetHolder, outputs.get(1), 76, 0, fluidAmount * 2).recipeContext(this);
    }

    public static void fillRecipes(EmiRegistry registry) {
        for(SqueezingRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.SQUEEZING.getType()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
            String resultPath = BuiltInRegistries.FLUID.getKey(recipe.getOutputFluid().getFluid()).getPath();
            String path;
            if (recipe.getInput().values[0] instanceof Ingredient.TagValue tagValue) path = tagValue.tag().location().getPath();
            else path = BuiltInRegistries.ITEM.getKey(recipe.getInput().getItems()[0].getItem()).getPath();
            registry.addRecipe(new SqueezingCategory(recipe,
                    new ResourceLocation(id.getNamespace(), "/squeezing/"+path+"_to_"+resultPath)));

        }
    }
}
