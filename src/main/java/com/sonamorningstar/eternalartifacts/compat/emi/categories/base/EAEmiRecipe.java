package com.sonamorningstar.eternalartifacts.compat.emi.categories.base;

import com.sonamorningstar.eternalartifacts.compat.emi.widgets.EtarTankWidget;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.registrar.MachineDeferredHolder;
import com.sonamorningstar.eternalartifacts.registrar.RecipeDeferredHolder;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TankWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public abstract class EAEmiRecipe extends BasicEmiRecipe {
    public EAEmiRecipe(EmiRecipeCategory category, ResourceLocation id, int width, int height) {
        super(category, id, width, height);
    }
    
    protected static void addFluidIngredient(List<EmiIngredient> io, FluidIngredient ingredient) {
        for (FluidIngredient.Value value : ingredient.getValues()) {
            if (value instanceof FluidIngredient.TagValue tagValue)
                io.add(EmiIngredient.of(tagValue.tag(), tagValue.amount()));
            else if (value instanceof FluidIngredient.FluidValue fluidValue) {
                FluidStack stack = fluidValue.fluidStack();
                io.add(EmiStack.of(stack.getFluid(), stack.getAmount()));
            }
        }
    }
    
    protected TankWidget addEtarTank(WidgetHolder widgets, EmiIngredient stack, int x, int y, int width, int height, int capacity) {
        return widgets.add(new EtarTankWidget(stack, x, y, width, height, capacity));
    }

    protected static EmiRecipeCategory createCategory(RecipeDeferredHolder<?, ?> recipeHolder, MachineDeferredHolder<?, ?, ?, ?> machineHolder) {
        return new EmiRecipeCategory(recipeHolder.getKey(), EmiStack.of(machineHolder));
    }
}
