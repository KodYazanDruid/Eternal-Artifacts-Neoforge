package com.sonamorningstar.eternalartifacts.compat.recipeviewer;

import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Supplier;

/**
 * Central registry for recipe viewer categories and recipes.
 * Both EMI and JEI plugins should use this registry to get common data.
 */
public final class RecipeViewerRegistry {
    private static final Map<ResourceLocation, RecipeViewerCategory> CATEGORIES = new LinkedHashMap<>();
    private static final Map<ResourceLocation, List<Supplier<RecipeViewerRecipe>>> RECIPE_SUPPLIERS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, List<RecipeViewerRecipe>> CACHED_RECIPES = new HashMap<>();
    
    private RecipeViewerRegistry() {}
    
    /**
     * Register a new category
     */
    public static void registerCategory(RecipeViewerCategory category) {
        CATEGORIES.put(category.id(), category);
    }
    
    /**
     * Register a recipe supplier for a category.
     * Using suppliers allows lazy loading and better performance.
     */
    public static void registerRecipeSupplier(ResourceLocation categoryId, Supplier<RecipeViewerRecipe> supplier) {
        RECIPE_SUPPLIERS.computeIfAbsent(categoryId, k -> new ArrayList<>()).add(supplier);
    }
    
    /**
     * Register multiple recipe suppliers for a category
     */
    public static void registerRecipeSuppliers(ResourceLocation categoryId, List<Supplier<RecipeViewerRecipe>> suppliers) {
        RECIPE_SUPPLIERS.computeIfAbsent(categoryId, k -> new ArrayList<>()).addAll(suppliers);
    }
    
    /**
     * Register a recipe directly (will be wrapped in a supplier)
     */
    public static void registerRecipe(RecipeViewerRecipe recipe) {
        registerRecipeSupplier(recipe.getCategoryId(), () -> recipe);
    }
    
    /**
     * Get all registered categories
     */
    public static Collection<RecipeViewerCategory> getCategories() {
        return Collections.unmodifiableCollection(CATEGORIES.values());
    }
    
    /**
     * Get a specific category by ID
     */
    public static Optional<RecipeViewerCategory> getCategory(ResourceLocation id) {
        return Optional.ofNullable(CATEGORIES.get(id));
    }
    
    /**
     * Get all recipes for a category (lazily evaluated and cached)
     */
    public static List<RecipeViewerRecipe> getRecipes(ResourceLocation categoryId) {
        return CACHED_RECIPES.computeIfAbsent(categoryId, id -> {
            List<Supplier<RecipeViewerRecipe>> suppliers = RECIPE_SUPPLIERS.get(id);
            if (suppliers == null) return Collections.emptyList();
            return suppliers.stream()
                .map(Supplier::get)
                .filter(Objects::nonNull)
                .toList();
        });
    }
    
    /**
     * Clear the recipe cache (call this on resource reload)
     */
    public static void clearCache() {
        CACHED_RECIPES.clear();
    }
    
    /**
     * Clear all registrations (usually not needed, but available for testing)
     */
    public static void clearAll() {
        CATEGORIES.clear();
        RECIPE_SUPPLIERS.clear();
        CACHED_RECIPES.clear();
    }
}

