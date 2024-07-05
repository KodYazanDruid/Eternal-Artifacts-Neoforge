package com.sonamorningstar.eternalartifacts.event.hooks;

import com.sonamorningstar.eternalartifacts.content.item.block.JarBlockItem;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.event.custom.JarDrinkEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

public class ModHooks {

/*    public static boolean onJarDrink(FluidIngredient fluidIngredient, ItemStack stack) {
        JarDrinkEvent event = new JarDrinkEvent(fluidIngredient);
        if(NeoForge.EVENT_BUS.post(event).isCanceled()) return false;
        if(event.getFluidIngredient().isEmpty()) return true;
        if(stack.getItem() instanceof JarBlockItem jar) {
            jar.set
        }
        return false;
    }*/
}
