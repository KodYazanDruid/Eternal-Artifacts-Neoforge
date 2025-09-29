package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.container.InductionFurnaceMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.InductionFurnace;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class InductionFurnaceScreen extends MultiFurnaceScreen<InductionFurnaceMenu> {
    public InductionFurnaceScreen(InductionFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
    
    @Override
    protected void renderLabels(GuiGraphics gui, int mx, int my) {
        super.renderLabels(gui, mx, my);
        PoseStack pose = gui.pose();
        pose.pushPose();
        pose.translate(0, 0, 150);
        pose.popPose();
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
        renderHeat(gui, leftPos + 30, topPos + 56);
    }

    private void renderHeat(GuiGraphics gui, int x, int y) {
        double heat = ((InductionFurnace) menu.getBlockEntity()).getHeatPercentage();
        gui.drawString(font, ModConstants.GUI.withSuffixTranslatable("heat").append(": ")
                .append(String.format("%.2f", heat)).append("%"), x, y, labelColor, false);
    }
}
