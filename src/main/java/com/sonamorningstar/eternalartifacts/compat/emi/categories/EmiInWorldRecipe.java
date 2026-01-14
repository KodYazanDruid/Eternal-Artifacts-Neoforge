package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.recipeviewer.ModRecipeViewerCategories;
import com.sonamorningstar.eternalartifacts.compat.recipeviewer.RecipeViewerRecipe;
import com.sonamorningstar.eternalartifacts.compat.recipeviewer.RecipeViewerRegistry;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * EMI adapter for in-world recipes from the common RecipeViewer system.
 */
public class EmiInWorldRecipe implements EmiRecipe {
    
    public static final EmiRecipeCategory IN_WORLD_CATEGORY = new EmiRecipeCategory(
        ModRecipeViewerCategories.IN_WORLD_ID,
        EmiStack.of(ModRecipeViewerCategories.IN_WORLD.icon())
    );
    
    private final RecipeViewerRecipe recipe;
    private final List<EmiIngredient> inputs = new ArrayList<>();
    private final List<EmiStack> outputs = new ArrayList<>();
    private final List<EmiIngredient> catalysts = new ArrayList<>();
    
    public EmiInWorldRecipe(RecipeViewerRecipe recipe) {
        this.recipe = recipe;
        
        // Convert inputs
        for (RecipeViewerRecipe.RecipeSlot slot : recipe.getInputs()) {
            if (slot.isItem()) {
                if (slot.ingredient() != null) {
                    inputs.add(EmiIngredient.of(slot.ingredient()));
                } else if (slot.itemStack() != null) {
                    inputs.add(EmiStack.of(slot.itemStack()));
                }
            } else if (slot.isFluid() && slot.fluidStack() != null) {
                inputs.add(EmiStack.of(slot.fluidStack().getFluid(), slot.fluidStack().getAmount()));
            } else if (slot.isBlock() && slot.blockState() != null) {
                inputs.add(EmiStack.of(slot.blockState().getBlock()));
            }
        }
        
        // Convert outputs
        for (RecipeViewerRecipe.RecipeSlot slot : recipe.getOutputs()) {
            if (slot.isItem() && slot.itemStack() != null) {
                outputs.add(EmiStack.of(slot.itemStack()));
            } else if (slot.isFluid() && slot.fluidStack() != null) {
                outputs.add(EmiStack.of(slot.fluidStack().getFluid(), slot.fluidStack().getAmount()));
            } else if (slot.isBlock() && slot.blockState() != null) {
                outputs.add(EmiStack.of(slot.blockState().getBlock()));
            }
        }
        
        // Convert catalysts
        for (RecipeViewerRecipe.RecipeSlot slot : recipe.getCatalysts()) {
            if (slot.isItem()) {
                if (slot.ingredient() != null) {
                    catalysts.add(EmiIngredient.of(slot.ingredient()));
                } else if (slot.itemStack() != null) {
                    catalysts.add(EmiStack.of(slot.itemStack()));
                }
            }
        }
    }
    
    @Override
    public EmiRecipeCategory getCategory() {
        return IN_WORLD_CATEGORY;
    }
    
    @Override
    public @Nullable ResourceLocation getId() {
        return recipe.getId();
    }
    
    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }
    
    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }
    
    @Override
    public List<EmiIngredient> getCatalysts() {
        return catalysts;
    }
    
    @Override
    public int getDisplayWidth() {
        return 120;
    }
    
    @Override
    public int getDisplayHeight() {
        return 60;
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        int slotX = 0;
        
        // Catalysts on top
        int catX = 0;
        for (EmiIngredient catalyst : catalysts) {
            widgets.addSlot(catalyst, catX, 0).catalyst(true);
            catX += 20;
        }
        
        // Inputs on left
        for (EmiIngredient input : inputs) {
            widgets.addSlot(input, slotX, 21);
            slotX += 20;
        }
        
        // Arrow
        widgets.addTexture(EmiTexture.EMPTY_ARROW, slotX + 4, 22);
        
        // Outputs on right
        int outputX = slotX + 30;
        for (EmiStack output : outputs) {
            widgets.addSlot(output, outputX, 21).recipeContext(this);
            outputX += 20;
        }
        
        // Processing time if available
        if (recipe.getProcessingTime() != null) {
            float seconds = recipe.getProcessingTime() / 20f;
            String timeText = String.format("%.1fs", seconds);
            widgets.addText(net.minecraft.network.chat.Component.literal(timeText), 60, 45, 0xFF808080, false);
        }
    }
    
    /**
     * Register in-world category to EMI
     */
    public static void registerCategories(EmiRegistry registry) {
        registry.addCategory(IN_WORLD_CATEGORY);
        registry.addWorkstation(IN_WORLD_CATEGORY, EmiStack.of(ModRecipeViewerCategories.IN_WORLD.icon()));
    }
    
    /**
     * Fill all in-world recipes from the RecipeViewerRegistry
     */
    public static void fillRecipes(EmiRegistry registry) {
        for (RecipeViewerRecipe recipe : RecipeViewerRegistry.getRecipes(ModRecipeViewerCategories.IN_WORLD_ID)) {
            registry.addRecipe(new EmiInWorldRecipe(recipe));
        }
    }
}

