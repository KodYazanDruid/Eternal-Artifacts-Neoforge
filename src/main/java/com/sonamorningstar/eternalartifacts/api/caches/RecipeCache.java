package com.sonamorningstar.eternalartifacts.api.caches;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RecipeCache {
    private final MachineBlockEntity<?> machine;
    private static final Map<MachineBlockEntity<?>, Recipe<? extends Container>> recipeMap = new ConcurrentHashMap<>();

    public RecipeCache(MachineBlockEntity<?> machine) {
        this.machine = machine;
    }

    protected Recipe<? extends Container> recipe = null;
    protected Container container = null;

    @SuppressWarnings("unchecked")
    public void findRecipe(RecipeType<? extends Recipe<? extends Container>> recipeType, Container container, Level level) {
        if(level == null) return;

        if(recipeMap.containsKey(machine)) {
            recipe = recipeMap.get(machine);
            this.container = container;
            return;
        }

        List<Recipe<Container>> recipeList = level.getRecipeManager()
                .getAllRecipesFor((RecipeType<Recipe<Container>>) recipeType).stream().map(RecipeHolder::value).toList();

        for(var r : recipeList) {
            if(r.matches(container, level)) {
                this.recipe = r;
                this.container = container;
                recipeMap.put(machine, r);
                return;
            }
        }

        recipe = null;
    }

    public <C extends Container,R extends Recipe<C>> R getRecipe(Class<R> recipeClass) {
        return recipeClass.cast(recipe);
    }

    public void clearRecipe(MachineBlockEntity<?> machine) {
        recipeMap.remove(machine);
    }

    public static void clearCache() {
        recipeMap.clear();
    }
}
