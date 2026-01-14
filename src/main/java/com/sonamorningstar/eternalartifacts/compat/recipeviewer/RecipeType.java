package com.sonamorningstar.eternalartifacts.compat.recipeviewer;

/**
 * Enum defining the type of recipe for visual representation.
 * Different types may have different layouts in recipe viewers.
 */
public enum RecipeType {
    /**
     * Standard machine recipe with inputs -> outputs
     */
    MACHINE,
    
    /**
     * In-world crafting (dropping items, using items on blocks, etc.)
     */
    IN_WORLD,
    
    /**
     * Cauldron-based interactions
     */
    CAULDRON,
    
    /**
     * Block transformation (block A becomes block B)
     */
    BLOCK_TRANSFORMATION,
    
    /**
     * Entity interaction recipes
     */
    ENTITY_INTERACTION
}

