package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.container.KnapsackMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class KnapsackScreen extends AbstractModContainerScreen<KnapsackMenu> {
    public KnapsackScreen(KnapsackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        setModular(true);
        int column = Config.KNAPSACK_SLOT_IN_ROW.get();
        int knapsackSize = menu.slots.size() - 36;
        int rowSize = 4;
        rowSize += Mth.ceil((float) knapsackSize / column);
        setImageSize((Math.max(column, 9) * 18) + 14, (rowSize * 18) + 40);
        this.inventoryLabelY = this.imageHeight - 92;
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float pPartialTick) {
        super.render(gui, mx, my, pPartialTick);
        renderTooltip(gui, mx, my);
    }
}
