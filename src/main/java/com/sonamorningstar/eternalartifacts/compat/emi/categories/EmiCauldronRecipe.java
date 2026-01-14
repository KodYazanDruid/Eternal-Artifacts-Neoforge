package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.recipeviewer.ModRecipeViewerCategories;
import com.sonamorningstar.eternalartifacts.compat.recipeviewer.RecipeViewerRecipe;
import com.sonamorningstar.eternalartifacts.compat.recipeviewer.RecipeViewerRegistry;
import com.sonamorningstar.eternalartifacts.compat.recipeviewer.RecipeViewerRenderer;
import com.sonamorningstar.eternalartifacts.compat.recipeviewer.recipes.CauldronRecipe;
import com.sonamorningstar.eternalartifacts.util.StringUtils;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * EMI adapter for CauldronRecipe from the common RecipeViewer system.
 */
public class EmiCauldronRecipe implements EmiRecipe {
    
    public static final EmiRecipeCategory CAULDRON_CATEGORY = new EmiRecipeCategory(
        ModRecipeViewerCategories.CAULDRON_ID,
        EmiStack.of(ModRecipeViewerCategories.CAULDRON.icon())
    );
    
    private final CauldronRecipe recipe;
    private final EmiRecipeCategory category;
    private final List<EmiIngredient> inputs = new ArrayList<>();
    private final List<EmiStack> outputs = new ArrayList<>();
    
    public EmiCauldronRecipe(CauldronRecipe recipe) {
        this.recipe = recipe;
        this.category = getCategoryForRecipe(recipe);
        
        // Convert inputs
        for (RecipeViewerRecipe.RecipeSlot slot : recipe.getInputs()) {
            if (slot.isItem()) {
                if (slot.ingredient() != null) {
                    inputs.add(EmiIngredient.of(slot.ingredient()));
                } else if (slot.itemStack() != null) {
                    inputs.add(EmiStack.of(slot.itemStack()));
                }
            } else if (slot.isBlock() && slot.blockState() != null) {
            
            }
        }
        
        // Convert outputs
        for (RecipeViewerRecipe.RecipeSlot slot : recipe.getOutputs()) {
            if (slot.isItem() && slot.itemStack() != null) {
                outputs.add(EmiStack.of(slot.itemStack()));
            } else if (slot.isBlock() && slot.blockState() != null) {
                outputs.add(EmiStack.of(slot.blockState().getBlock()));
            }
        }
    }
    
    private static EmiRecipeCategory getCategoryForRecipe(CauldronRecipe recipe) {
        return CAULDRON_CATEGORY;
    }
    
    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }
    
    @Override
    public @Nullable ResourceLocation getId() {
        ResourceLocation id = recipe.getId();
        return new ResourceLocation(id.getNamespace(), "/"+id.getPath());
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
    public int getDisplayWidth() {
        return 120;
    }
    
    @Override
    public int getDisplayHeight() {
        return 60;
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        BlockState cauldronState = recipe.getCauldronState();
        Minecraft mc = Minecraft.getInstance();
        // Cauldron input - render as 3D block
        if (cauldronState != null) {
            widgets.addDrawable(5, 17, 24, 24, (gui, mouseX, mouseY, delta) -> {
                RecipeViewerRenderer.renderBlock(gui, cauldronState, 12, 12, 1.0f);
            });
            widgets.addTooltipText(StringUtils.getTooltipForBlockState(null, cauldronState,
                    mc.level, mc.options.advancedItemTooltips),
                5, 17, 24, 24);
        }
        
        // Item input
        if (!inputs.isEmpty()) {
            widgets.addSlot(inputs.get(0), 28, 21);
        }
        
        // Arrow
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 52, 22);
        
        // Output - check if it's a block or item
        if (!outputs.isEmpty()) {
            EmiStack output = outputs.get(0);
            
            // Check if output is a cauldron block (render 3D)
            if (hasBlockOutput()) {
                BlockState outputState = getOutputBlockState();
                if (outputState != null) {
                    widgets.addDrawable(83, 17, 24, 24, (gui, mouseX, mouseY, delta) -> {
                        RecipeViewerRenderer.renderBlock(gui, outputState, 12, 12, 1.0f);
                    });
                    widgets.addTooltipText(StringUtils.getTooltipForBlockState(null, outputState,
                            mc.level, mc.options.advancedItemTooltips),
                        83, 17, 24, 24);
                } else {
                    widgets.addSlot(output, 84, 21).recipeContext(this);
                }
            } else {
                widgets.addSlot(output, 84, 21).recipeContext(this);
            }
        }
        
        // Show cauldron level change if applicable
        int inputLevel = recipe.getInputLevel();
        int outputLevel = recipe.getOutputLevel();
        if (inputLevel != outputLevel) {
            String levelText = inputLevel + " → " + outputLevel;
            widgets.addText(net.minecraft.network.chat.Component.literal(levelText), 60, 45, 0xFF808080, false);
        }
    }
    
    private boolean hasBlockOutput() {
        for (RecipeViewerRecipe.RecipeSlot slot : recipe.getOutputs()) {
            if (slot.isBlock() && slot.blockState() != null) {
                return true;
            }
        }
        return false;
    }
    
    @Nullable
    private BlockState getOutputBlockState() {
        for (RecipeViewerRecipe.RecipeSlot slot : recipe.getOutputs()) {
            if (slot.isBlock() && slot.blockState() != null) {
                return slot.blockState();
            }
        }
        return null;
    }
    
    /**
     * Register all cauldron categories to EMI
     */
    public static void registerCategories(EmiRegistry registry) {
        registry.addCategory(CAULDRON_CATEGORY);
        registry.addWorkstation(CAULDRON_CATEGORY, EmiStack.of(ModRecipeViewerCategories.CAULDRON.icon()));
    }
    
    /**
     * Fill all cauldron recipes from the RecipeViewerRegistry
     */
    public static void fillRecipes(EmiRegistry registry) {
        for (RecipeViewerRecipe recipe : RecipeViewerRegistry.getRecipes(ModRecipeViewerCategories.CAULDRON_ID)) {
            if (recipe instanceof CauldronRecipe cauldronRecipe) {
                registry.addRecipe(new EmiCauldronRecipe(cauldronRecipe));
            }
        }
    }
}

