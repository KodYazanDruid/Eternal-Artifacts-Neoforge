package com.sonamorningstar.eternalartifacts.compat.recipeviewer;

import java.util.List;

/**
 * Base interface for recipe viewer plugins (EMI, JEI, REI, etc.)
 * This interface defines common operations that all recipe viewers should implement.
 */
public interface RecipeViewerPlugin {
    
    /**
     * Called when the recipe viewer is initializing.
     * Register categories, recipes, and workstations here.
     */
    void register();
    
    /**
     * Get all registered categories for this plugin.
     * @return List of category IDs
     */
    List<String> getRegisteredCategories();
}

