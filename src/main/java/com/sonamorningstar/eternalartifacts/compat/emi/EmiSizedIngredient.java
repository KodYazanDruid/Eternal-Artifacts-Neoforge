package com.sonamorningstar.eternalartifacts.compat.emi;

import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.SizedIngredient;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class EmiSizedIngredient implements EmiIngredient {

    /*public static EmiSizedIngredient of(SizedIngredient ingredient) {
        if (ingredient != null && !ingredient.isEmpty()) {
            ItemStack[] stacks = ingredient.getItems();
            int amount = 1;
            if (stacks.length != 0) {
                amount = stacks[0].getCount();

                for(int i = 1; i < stacks.length; ++i) {
                    if (stacks[i].getCount() != amount) {
                        amount = 1;
                        break;
                    }
                }
            }

            return EmiIngredient.of(ingredient.toIngredient(), ((long) ingredient.getItems()[0].getCount()));
        } else {
            return EmiStack.EMPTY;
        }
    }*/

    @Override
    public List<EmiStack> getEmiStacks() {
        return List.of();
    }

    @Override
    public EmiIngredient copy() {
        return null;
    }

    @Override
    public long getAmount() {
        return 0;
    }

    @Override
    public EmiIngredient setAmount(long l) {
        return null;
    }

    @Override
    public float getChance() {
        return 0;
    }

    @Override
    public EmiIngredient setChance(float v) {
        return null;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int i1, float v, int i2) {

    }

    @Override
    public List<ClientTooltipComponent> getTooltip() {
        return List.of();
    }
}
