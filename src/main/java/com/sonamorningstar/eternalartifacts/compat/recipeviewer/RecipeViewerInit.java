package com.sonamorningstar.eternalartifacts.compat.recipeviewer;

/**
 * Main initializer for the recipe viewer compatibility layer.
 * Call this during mod client setup to register all categories and recipes.
 */
public final class RecipeViewerInit {
    
    private static boolean initialized = false;
    
    private RecipeViewerInit() {}
    
    /**
     * Initialize the recipe viewer registry with all categories and recipes.
     * This should be called once during client setup.
     * Safe to call multiple times - will only initialize once.
     */
    public static void init() {
        if (initialized) return;
        
        // Register categories first
        ModRecipeViewerCategories.registerAll();
        
        // Then register recipes
        ModRecipeViewerRecipes.registerAll();
        
        initialized = true;
    }
    
    /**
     * Check if the registry has been initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Reset the initialization state (for testing purposes)
     */
    public static void reset() {
        RecipeViewerRegistry.clearAll();
        initialized = false;
    }
}

