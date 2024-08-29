package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.api.caches.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMachineScreen;
import com.sonamorningstar.eternalartifacts.container.FluidCombustionMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.FluidCombustionDynamoBlockEntity;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FluidCombustionScreen extends AbstractMachineScreen<FluidCombustionMenu> {
    public FluidCombustionScreen(FluidCombustionMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float pPartialTick, int mx, int my) {
        super.renderBg(gui, pPartialTick, mx, my);
        renderDefaultEnergyAndFluidBar(gui);
        renderBurn(gui, x+81, y+55, mx, my);
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
        if (menu.getBlockEntity() instanceof FluidCombustionDynamoBlockEntity dynamo){
            DynamoProcessCache cache = dynamo.getCache();
            if (cache != null){
                gui.drawString(font,
                        ModConstants.GUI.withSuffixTranslatable("dynamo_produce_rate")
                                .append(": " + cache.getGeneration()+"RF/T"),
                        x + 44, y + 46, labelColor, false);
            }
        }
    }
}
