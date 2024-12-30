package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.SolidifierRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
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
import net.neoforged.neoforge.fluids.FluidStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class SolidifierCategory extends EAEmiRecipe {
    public static final EmiRecipeCategory SOLIDIFIER_CATEGORY = createCategory(ModRecipes.SOLIDIFYING, ModMachines.SOLIDIFIER);
    public SolidifierCategory(SolidifierRecipe recipe, ResourceLocation id) {
        super(SOLIDIFIER_CATEGORY, id, 72, 18);
        FluidIngredient inputFluid = recipe.getInputFluid();
        for (FluidIngredient.Value value : inputFluid.getValues()) {
            if (value instanceof FluidIngredient.TagValue tagValue)
                inputs.add(EmiIngredient.of(tagValue.tag()));
            else if (value instanceof FluidIngredient.FluidValue fluidValue) {
                FluidStack stack = fluidValue.fluidStack();
                inputs.add(EmiStack.of(stack.getFluid(), stack.getAmount()));
            }
        }
        outputs.add(EmiStack.of(recipe.getOutput()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addFillingArrow(24, 0, 10000);
        widgets.addSlot(outputs.get(0).setAmount(outputs.get(0).getAmount()), 54, 0).recipeContext(this);
    }

    public static void fillRecipes(EmiRegistry registry) {
        for(SolidifierRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.SOLIDIFYING.getType()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.FLUID.getKey(recipe.getInputFluid().getFluidStacks()[0].getFluid());
            registry.addRecipe(new SolidifierCategory(recipe, new ResourceLocation(MODID, ("solidifying/"+id.toString().replace(":", "/")))));
        }
    }
}
