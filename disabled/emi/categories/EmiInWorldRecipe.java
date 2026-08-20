package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.recipeviewer.ModRecipeViewerCategories;
import com.sonamorningstar.eternalartifacts.compat.recipeviewer.RecipeViewerRecipe;
import com.sonamorningstar.eternalartifacts.compat.recipeviewer.RecipeViewerRegistry;
import com.sonamorningstar.eternalartifacts.compat.recipeviewer.RecipeViewerRenderer;
import com.sonamorningstar.eternalartifacts.compat.recipeviewer.recipes.InWorldRecipe;
import com.sonamorningstar.eternalartifacts.util.StringUtils;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * EMI adapter for in-world recipes from the common RecipeViewer system.
 */
public class EmiInWorldRecipe implements EmiRecipe {
    
    public static final EmiRecipeCategory IN_WORLD_CATEGORY = new EmiRecipeCategory(
        ModRecipeViewerCategories.IN_WORLD_ID,
        EmiStack.of(ModRecipeViewerCategories.IN_WORLD.icon())
    );
    
    private final InWorldRecipe recipe;
    private final List<EmiIngredient> inputs = new ArrayList<>();
    private final List<EmiStack> outputs = new ArrayList<>();
    private final List<EmiIngredient> catalysts = new ArrayList<>();
    @Nullable
    private final BlockState containerBlockState;
    private final List<InWorldRecipe.EnvironmentCondition> environmentConditions = new ArrayList<>();
    private final List<BlockState> inputBlockStates = new ArrayList<>();
    private final List<BlockState> outputBlockStates = new ArrayList<>();
    private final List<Component> conditionDescriptions = new ArrayList<>();
    
    public EmiInWorldRecipe(InWorldRecipe inWorldRecipe) {
        this.recipe = inWorldRecipe;
        
        // Handle InWorldRecipe specific data
        // Container block
        this.containerBlockState = inWorldRecipe.getContainerBlock();
        
        // Environment conditions - keep BlockState for 3D rendering
        for (InWorldRecipe.EnvironmentCondition condition : inWorldRecipe.getEnvironmentConditions()) {
            if (!condition.requiredBlocks().isEmpty()) {
                environmentConditions.add(condition);
            }
            if (condition.description() != null) {
                conditionDescriptions.add(condition.description());
            }
        }
        
        // Convert inputs - keep BlockState separately for 3D rendering
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
                // Store BlockState for 3D rendering
                inputBlockStates.add(slot.blockState());
            }
        }
        
        // Convert outputs - keep BlockState separately for 3D rendering
        for (RecipeViewerRecipe.RecipeSlot slot : recipe.getOutputs()) {
            if (slot.isItem() && slot.itemStack() != null) {
                outputs.add(EmiStack.of(slot.itemStack()));
            } else if (slot.isFluid() && slot.fluidStack() != null) {
                outputs.add(EmiStack.of(slot.fluidStack().getFluid(), slot.fluidStack().getAmount()));
            } else if (slot.isBlock() && slot.blockState() != null) {
                // Store BlockState for 3D rendering
                outputBlockStates.add(slot.blockState());
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
    public List<EmiIngredient> getCatalysts() {
        return catalysts;
    }
    
    @Override
    public int getDisplayWidth() {
        return 160;
    }
    
    @Override
    public int getDisplayHeight() {
        int height = 70; // Base height for items and arrow
        
        // Calculate block structure height
        if (containerBlockState != null || !environmentConditions.isEmpty()) {
            int blockStructureHeight = 20; // Container block
            boolean hasSurrounding = false;
            for (InWorldRecipe.EnvironmentCondition condition : environmentConditions) {
                if (condition.type() == InWorldRecipe.ConditionType.BLOCK_ABOVE) {
                    blockStructureHeight += 18;
                } else if (condition.type() == InWorldRecipe.ConditionType.BLOCK_BELOW) {
                    blockStructureHeight += 18;
                } else if (condition.type() == InWorldRecipe.ConditionType.BLOCK_SURROUNDING) {
                    hasSurrounding = true;
                }
            }
            // Surrounding adds top and bottom blocks
            if (hasSurrounding) {
                blockStructureHeight += 36; // top + bottom
            }
            height = Math.max(height, blockStructureHeight + 20);
        }
        
        if (!conditionDescriptions.isEmpty()) {
            height += conditionDescriptions.size() * 10;
        }
        return height;
    }
    
    @Override
    public void addWidgets(WidgetHolder widgets) {
        Minecraft mc = Minecraft.getInstance();
        
        // Block size for world-like rendering
        final int blockSize = 18;
        final int blockSpacing = 0; // No gap between blocks for world-like appearance
        
        // Find what environment conditions we have
        List<BlockState> blocksAbove = new ArrayList<>();
        List<BlockState> blocksBelow = new ArrayList<>();
        List<BlockState> blocksAdjacent = new ArrayList<>();
        List<BlockState> blocksSurrounding = new ArrayList<>();
        
        for (InWorldRecipe.EnvironmentCondition condition : environmentConditions) {
            List<BlockState> blocks = condition.requiredBlocks();
            if (blocks.isEmpty()) continue;
            
            switch (condition.type()) {
                case BLOCK_ABOVE -> blocksAbove.addAll(blocks);
                case BLOCK_BELOW -> blocksBelow.addAll(blocks);
                case BLOCK_ADJACENT -> blocksAdjacent.addAll(blocks);
                case BLOCK_SURROUNDING -> blocksSurrounding.addAll(blocks);
                default -> {}
            }
        }
        
        int structureX = 5;
        if (!blocksSurrounding.isEmpty()) {
            structureX += blockSize;
        }
        
        int structureY = 5;
        if (!blocksAbove.isEmpty()) {
            structureY += blockSize;
        }
        if (!blocksSurrounding.isEmpty()) {
            structureY += blockSize;
        }
        
        int containerY = structureY;
        
        // Render blocks above container (separate from surrounding)
        if (!blocksAbove.isEmpty()) {
            BlockState aboveState = blocksAbove.get(0);
            int aboveY = containerY - blockSize - blockSpacing;
            if (!blocksSurrounding.isEmpty()) {
                aboveY -= blockSize; // Account for surrounding top block
            }
            widgets.addDrawable(structureX, aboveY, blockSize, blockSize, (gui, mouseX, mouseY, delta) ->
                RecipeViewerRenderer.renderBlock(gui, aboveState, blockSize / 2, blockSize / 2, 0.85f));
            widgets.addTooltipText(StringUtils.getTooltipForBlockState(null, aboveState,
                    mc.level, mc.options.advancedItemTooltips),
                structureX, aboveY, blockSize, blockSize);
        }
        
        // Render surrounding blocks (all 4 directions: top, bottom, left, right)
        if (!blocksSurrounding.isEmpty()) {
            BlockState surroundState = blocksSurrounding.get(0);
            
            // Top
            int topY = containerY - blockSize - blockSpacing;
            widgets.addDrawable(structureX, topY, blockSize, blockSize, (gui, mouseX, mouseY, delta) ->
                RecipeViewerRenderer.renderBlock(gui, surroundState, blockSize / 2, blockSize / 2, 0.85f));
            widgets.addTooltipText(StringUtils.getTooltipForBlockState(null, surroundState,
                    mc.level, mc.options.advancedItemTooltips),
                structureX, topY, blockSize, blockSize);
            
            // Bottom
            int bottomY = containerY + blockSize + blockSpacing;
            widgets.addDrawable(structureX, bottomY, blockSize, blockSize, (gui, mouseX, mouseY, delta) ->
                RecipeViewerRenderer.renderBlock(gui, surroundState, blockSize / 2, blockSize / 2, 0.85f));
            widgets.addTooltipText(StringUtils.getTooltipForBlockState(null, surroundState,
                    mc.level, mc.options.advancedItemTooltips),
                structureX, bottomY, blockSize, blockSize);
            
            // Left
            int leftX = structureX - blockSize - blockSpacing;
            widgets.addDrawable(leftX, containerY, blockSize, blockSize, (gui, mouseX, mouseY, delta) ->
                RecipeViewerRenderer.renderBlock(gui, surroundState, blockSize / 2, blockSize / 2, 0.85f));
            widgets.addTooltipText(StringUtils.getTooltipForBlockState(null, surroundState,
                    mc.level, mc.options.advancedItemTooltips),
                leftX, containerY, blockSize, blockSize);
            
            // Right
            int rightX = structureX + blockSize + blockSpacing;
            widgets.addDrawable(rightX, containerY, blockSize, blockSize, (gui, mouseX, mouseY, delta) ->
                RecipeViewerRenderer.renderBlock(gui, surroundState, blockSize / 2, blockSize / 2, 0.85f));
            widgets.addTooltipText(StringUtils.getTooltipForBlockState(null, surroundState,
                    mc.level, mc.options.advancedItemTooltips),
                rightX, containerY, blockSize, blockSize);
        }
        
        // Render container block (center)
        if (containerBlockState != null) {
            final int contY = containerY;
            widgets.addDrawable(structureX, contY, blockSize, blockSize, (gui, mouseX, mouseY, delta) ->
                RecipeViewerRenderer.renderBlock(gui, containerBlockState, blockSize / 2, blockSize / 2, 0.85f));
            widgets.addTooltipText(StringUtils.getTooltipForBlockState(null, containerBlockState,
                    mc.level, mc.options.advancedItemTooltips),
                structureX, contY, blockSize, blockSize);
        }
        
        // Render blocks below container (separate from surrounding)
        if (!blocksBelow.isEmpty()) {
            Supplier<BlockState> belowStateGetter = () -> {
                long tickCount = mc.clientTickCount;
                int index = (int) ((tickCount / 20) % blocksBelow.size());
                return blocksBelow.get(index);
            };
            //BlockState belowState = blocksBelow.get(0);
            int belowY = containerY + blockSize + blockSpacing;
            if (!blocksSurrounding.isEmpty()) {
                belowY += blockSize; // Account for surrounding bottom block
            }
            
            widgets.addDrawable(structureX, belowY, blockSize, blockSize, (gui, mouseX, mouseY, delta) ->
                RecipeViewerRenderer.renderBlock(gui, belowStateGetter.get(), blockSize / 2, blockSize / 2, 0.85f));
            widgets.addTooltip((mx, my) -> StringUtils.getTooltipForBlockState(null, belowStateGetter.get(),
				mc.level, mc.options.advancedItemTooltips).stream()
				.map(Component::getVisualOrderText)
				.map(ClientTooltipComponent::create)
				.toList(),
                structureX, belowY, blockSize, blockSize);
        }
        
        // Render adjacent block (one side - to the right)
        int adjacentEndX = structureX + blockSize;
        if (!blocksAdjacent.isEmpty()) {
            BlockState adjacentState = blocksAdjacent.get(0);
            int adjX = structureX + blockSize + blockSpacing;
            if (!blocksSurrounding.isEmpty()) {
                adjX += blockSize; // Account for surrounding right block
            }
            widgets.addDrawable(adjX, containerY, blockSize, blockSize, (gui, mouseX, mouseY, delta) ->
                RecipeViewerRenderer.renderBlock(gui, adjacentState, blockSize / 2, blockSize / 2, 0.85f));
            widgets.addTooltipText(StringUtils.getTooltipForBlockState(null, adjacentState,
                    mc.level, mc.options.advancedItemTooltips),
                adjX, containerY, blockSize, blockSize);
            adjacentEndX = adjX + blockSize;
        }
        
        // Input block states (if any, not part of environment)
        int inputBlockX = adjacentEndX + 5;
        if (!blocksSurrounding.isEmpty() && blocksAdjacent.isEmpty()) {
            inputBlockX = structureX + blockSize * 2 + 5;
        }
        
        for (BlockState inputState : inputBlockStates) {
            final int drawX = inputBlockX;
            widgets.addDrawable(drawX, containerY, blockSize, blockSize, (gui, mouseX, mouseY, delta) ->
                RecipeViewerRenderer.renderBlock(gui, inputState, blockSize / 2, blockSize / 2, 0.85f));
            widgets.addTooltipText(StringUtils.getTooltipForBlockState(null, inputState,
                    mc.level, mc.options.advancedItemTooltips),
                drawX, containerY, blockSize, blockSize);
            inputBlockX += blockSize + 2;
        }
        
        // Calculate items area - to the right of block structure
        int itemsStartX = inputBlockX + 5;
        int itemsY = containerY + 1;
        
        // Input items/fluids
        int slotX = itemsStartX;
        for (EmiIngredient input : inputs) {
            widgets.addSlot(input, slotX, itemsY);
            slotX += 18;
        }
        
        for (EmiIngredient catalyst : catalysts) {
            widgets.addSlot(catalyst, slotX, itemsY).catalyst(true);
            slotX += 18;
        }
        
        widgets.addTexture(EmiTexture.EMPTY_ARROW, slotX + 2, itemsY);
        
        // Output items
        int outputX = slotX + 26;
        for (EmiStack output : outputs) {
            widgets.addSlot(output, outputX, itemsY).recipeContext(this);
            outputX += 18;
        }
        
        for (BlockState outputState : outputBlockStates) {
            final int drawX = outputX;
            widgets.addDrawable(drawX, itemsY - 1, blockSize, blockSize, (gui, mouseX, mouseY, delta) ->
                RecipeViewerRenderer.renderBlock(gui, outputState, blockSize / 2, blockSize / 2, 0.85f));
            widgets.addTooltipText(StringUtils.getTooltipForBlockState(null, outputState,
                    mc.level, mc.options.advancedItemTooltips),
                drawX, itemsY - 1, blockSize, blockSize);
            outputX += blockSize + 2;
        }
        
        // Condition descriptions at bottom
        int bottomY = containerY + blockSize;
        if (!blocksBelow.isEmpty()) {
            bottomY += blockSize;
        }
        if (!blocksSurrounding.isEmpty()) {
            bottomY = containerY + blockSize * 2;
        }
        int textY = bottomY + 5;
        for (Component desc : conditionDescriptions) {
            widgets.addText(desc, 0, textY, 0xFF606060, false);
            textY += 10;
        }
        
        // Processing time if available
        if (recipe.getProcessingTime() != null) {
            float seconds = recipe.getProcessingTime() / 20f;
            String timeText = String.format("%.1fs", seconds);
            widgets.addText(Component.literal(timeText), 0, textY, 0xFF808080, false);
        }
    }
    
    public static void registerCategories(EmiRegistry registry) {
        registry.addCategory(IN_WORLD_CATEGORY);
        registry.addWorkstation(IN_WORLD_CATEGORY, EmiStack.of(ModRecipeViewerCategories.IN_WORLD.icon()));
    }
    
    public static void fillRecipes(EmiRegistry registry) {
        for (RecipeViewerRecipe recipe : RecipeViewerRegistry.getRecipes(ModRecipeViewerCategories.IN_WORLD_ID)) {
            if (recipe instanceof InWorldRecipe inWorldRecipe) {
                registry.addRecipe(new EmiInWorldRecipe(inWorldRecipe));
            }
        }
    }
}

