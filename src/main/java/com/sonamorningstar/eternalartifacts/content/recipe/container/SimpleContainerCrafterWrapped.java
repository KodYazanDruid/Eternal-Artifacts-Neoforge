package com.sonamorningstar.eternalartifacts.content.recipe.container;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class SimpleContainerCrafterWrapped extends SimpleContainer implements CraftingContainer {

    public SimpleContainerCrafterWrapped(int size) {
        super(size);
    }
    public SimpleContainerCrafterWrapped(ItemStack... items) {
        super(items);
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

}
