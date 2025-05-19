package com.sonamorningstar.eternalartifacts.api.caches;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;

@Getter
public class RecipeCache {
    private static final Map<Machine<?>, ArrayList<Recipe<? extends Container>>> recipeMap = new HashMap<>();
    private static final Object2IntMap<Machine<?>> maxRecipeCounts = Util.make(new Object2IntOpenHashMap<>(),
        map -> map.defaultReturnValue(1));
    
    public static void findRecipeFor(Machine<?> machine, RecipeType<? extends Recipe<? extends Container>> recipeType,
									 Container container, Level level, boolean allowDuplicate) {
        findRecipeFor(machine, recipeType, container, level, allowDuplicate, -1);
    }
        
        @SuppressWarnings("unchecked")
    public static void findRecipeFor(Machine<?> machine, RecipeType<? extends Recipe<? extends Container>> recipeType,
									 Container container, Level level, boolean allowDuplicate, int index) {
        if(level == null || container.isEmpty()) {
            return;
        }

        var recipes = getCachedRecipes(machine);
        if (!allowDuplicate){
            for (var r : recipes) {
                if (r.getType() == recipeType) {
                    return;
                }
            }
        }

        List<Recipe<Container>> recipeList = level.getRecipeManager()
                .getAllRecipesFor((RecipeType<Recipe<Container>>) recipeType).stream().map(RecipeHolder::value).toList();

        for(var r : recipeList) {
            if(r.matches(container, level)) {
                ArrayList<Recipe<? extends Container>> machineRecipes = recipeMap.get(machine);
                if (machineRecipes == null) {
                    machineRecipes = new ArrayList<>();
                    if (index >= 0 && index < maxRecipeCounts.getInt(machine)) {
                        while (machineRecipes.size() <= index) {
                            machineRecipes.add(null);
                        }
                        machineRecipes.set(index, r);
                    } else if (index < 0) machineRecipes.add(r);
                    recipeMap.put(machine, machineRecipes);
                } else {
                    if (index >= 0 && index < maxRecipeCounts.getInt(machine)) {
                        while (machineRecipes.size() <= index) {
                            machineRecipes.add(null);
                        }
                        machineRecipes.set(index, r);
                    } else if (index < 0) machineRecipes.add(r);
                }
                return;
            }
        }
    }
    
    public static void setRecipeCount(Machine<?> machine, int count) {
        maxRecipeCounts.put(machine, count);
    }

    public static void clearRecipes(Machine<?> machine) {
        recipeMap.remove(machine);
    }
    
    public static void removeRecipe(Machine<?> machine, Recipe<? extends Container> recipe) {
        var recipeArr = recipeMap.get(machine);
        if (recipeArr != null) recipeArr.remove(recipe);
    }
    
    public static void removeRecipe(Machine<?> machine, int index) {
        var recipeArr = recipeMap.get(machine);
        if (recipeArr != null && recipeArr.size() > index) recipeArr.set(index, null);
    }
    
    @Nullable
    public static Recipe<? extends Container> getCachedRecipe(Machine<?> machine) {
        var recipeArr = recipeMap.get(machine);
        return recipeArr != null && !recipeArr.isEmpty() ? recipeArr.get(0) : null;
    }
    
    @Nullable
    public static Recipe<? extends Container> getCachedRecipe(Machine<?> machine, int index) {
        var recipeArr = recipeMap.get(machine);
        return recipeArr != null && recipeArr.size() > index ? recipeMap.get(machine).get(index) : null;
    }
    
    public static List<Recipe<? extends Container>> getCachedRecipes(Machine<?> machine) {
        return recipeMap.get(machine) != null ? recipeMap.get(machine) : Collections.unmodifiableList(new ArrayList<>());
    }

    public static void clearCache() {
        recipeMap.clear();
    }
}
