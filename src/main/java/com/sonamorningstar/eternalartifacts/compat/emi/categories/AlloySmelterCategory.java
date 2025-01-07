package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.AlloyingRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.HashMap;
import java.util.Map;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class AlloySmelterCategory extends EAEmiRecipe {
    public static final EmiRecipeCategory ALLOYING_CATEGORY = createCategory(ModRecipes.ALLOYING, ModMachines.ALLOY_SMELTER);

    public AlloySmelterCategory(AlloyingRecipe recipe, ResourceLocation id) {
        super(ALLOYING_CATEGORY, id, 108,18);
        recipe.getInputs().forEach(input -> inputs.add(EmiIngredient.of(input.toIngredient())));
        outputs.add(EmiStack.of(recipe.getOutput()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < inputs.size(); i++) {
            EmiIngredient input = inputs.get(i);
            widgets.addSlot(input,i * 18, 0);
        }
        widgets.addFillingArrow(60, 0, 10000);
        widgets.addSlot(outputs.get(0).setAmount(outputs.get(0).getAmount()),90,0).recipeContext(this);
    }

    public static void fillRecipes(EmiRegistry registry) {
        Map<Item, Integer> alloyingTrackedItems = new HashMap<>();
        for(AlloyingRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.ALLOYING.getType()).stream().map(RecipeHolder::value).toList()) {
            Item item = recipe.getOutput().getItem();
            int encounter = 0;
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            StringBuilder inputId = new StringBuilder();
            inputId.append("alloying/").append(id.toString().replace(":", "/"));
            if (alloyingTrackedItems.containsKey(item)) {
                encounter = alloyingTrackedItems.get(item);
                inputId.append("_").append(encounter);
                encounter++;
                alloyingTrackedItems.put(item, encounter);
            } else alloyingTrackedItems.put(item, encounter);
            ResourceLocation recipeId = new ResourceLocation(MODID, inputId.toString());
            registry.addRecipe(new AlloySmelterCategory(recipe, recipeId));
        }
    }
}