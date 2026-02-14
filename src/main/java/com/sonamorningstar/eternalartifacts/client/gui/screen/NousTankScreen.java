package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SpriteButton;
import com.sonamorningstar.eternalartifacts.container.NousTankMenu;
import com.sonamorningstar.eternalartifacts.util.ExperienceHelper;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class NousTankScreen extends AbstractSidedMachineScreen<NousTankMenu> {
    public NousTankScreen(NousTankMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        Player player = minecraft.player;
        MultiPlayerGameMode gameMode = minecraft.gameMode;
        addRenderableWidget(SpriteButton.builder(Component.empty(), (button, key) -> {
            if (menu.clickMenuButton(player, 0))
                gameMode.handleInventoryButtonClick(menu.containerId, 0);
            })
            .bounds(leftPos + 40, topPos + 20, 5, 7)
            .addSprite(buttons, 0, 0, 5, 7)
            .addTooltipHover(ModConstants.GUI.withSuffixTranslatable("deposit_1_level"))
            .build());
        addRenderableWidget(SpriteButton.builder(Component.empty(), (button, key) -> {
            if (menu.clickMenuButton(player, 1))
                gameMode.handleInventoryButtonClick(menu.containerId, 1);
            })
            .bounds(leftPos + 46, topPos + 20, 8, 7)
            .addSprite(buttons, 5, 0, 8, 7)
            .addTooltipHover(ModConstants.GUI.withSuffixTranslatable("deposit_10_levels"))
            .build());
        addRenderableWidget(SpriteButton.builder(Component.empty(), (button, key) -> {
            if (menu.clickMenuButton(player, 2))
                gameMode.handleInventoryButtonClick(menu.containerId, 2);
            })
            .bounds(leftPos + 55, topPos + 20, 11, 7)
            .addSprite(buttons, 13, 0, 11, 7)
            .addTooltipHover(ModConstants.GUI.withSuffixTranslatable("deposit_all_levels"))
            .build());
        addRenderableWidget(SpriteButton.builder(Component.empty(), (button, key) -> {
            if (menu.clickMenuButton(player, 3))
                gameMode.handleInventoryButtonClick(menu.containerId, 3);
            })
            .bounds(leftPos + 76, topPos + 20, 11, 7)
            .addSprite(buttons, 24, 0, 11, 7)
            .addTooltipHover(ModConstants.GUI.withSuffixTranslatable("take_all_levels"))
            .build());
        addRenderableWidget(SpriteButton.builder(Component.empty(), (button, key) -> {
            if (menu.clickMenuButton(player, 4))
                gameMode.handleInventoryButtonClick(menu.containerId, 4);
            })
            .bounds(leftPos + 88, topPos + 20, 8, 7)
            .addSprite(buttons, 35, 0, 8, 7)
            .addTooltipHover(ModConstants.GUI.withSuffixTranslatable("take_10_levels"))
            .build());
        addRenderableWidget(SpriteButton.builder(Component.empty(), (button, key) -> {
            if (menu.clickMenuButton(player, 5))
                gameMode.handleInventoryButtonClick(menu.containerId, 5);
            })
            .bounds(leftPos + 97, topPos + 20, 5, 7)
            .addSprite(buttons, 43, 0, 5, 7)
            .addTooltipHover(ModConstants.GUI.withSuffixTranslatable("take_1_level"))
            .build());
    }

    @Override
    protected void renderBg(GuiGraphics gui, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(gui, pPartialTick, pMouseX, pMouseY);
        renderFluidBar(gui, leftPos + 5, topPos + 20);
        IFluidHandler tank = menu.getBeTank();
        if (tank != null) {
            int xp = tank.getFluidInTank(0).getAmount() / 20;
            int lvl = ExperienceHelper.totalLevelsFromXp(xp);
            gui.drawString(font, Component.literal(lvl+"L | "+xp+"XP"), leftPos + 40, topPos + 48, labelColor, false);
        }
    }
}
