package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.InductionFurnaceMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.InductionFurnace;
import com.sonamorningstar.eternalartifacts.client.render.ItemRendererHelper;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

public class InductionFurnaceScreen extends AbstractSidedMachineScreen<InductionFurnaceMenu> {
    public InductionFurnaceScreen(InductionFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
    
    @Override
    protected void init() {
        super.init();
        int currValue = menu.getBlockEntity() instanceof InductionFurnace furnace ? furnace.recipeTypeId : 0;
        addRenderableWidget(new ExtendedSlider(leftPos + 100, topPos + 10, 50, 10, Component.empty(), Component.empty(),
            0, 3, currValue, 1, 1, true) {
            @Override
            protected void applyValue() {
                if (menu.getBlockEntity() instanceof InductionFurnace furnace) {
                    furnace.setRecipeTypeId((short) getValueInt());
                }
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, getValueInt());
            }
        });
    }
    
    @Override
    protected void drawExtraBg(GuiGraphics gui, float tickDelta, int x, int y) {
        if (menu.getBlockEntity() instanceof InductionFurnace furnace) {
            ItemStack recipeItem;
            switch (furnace.recipeTypeId) {
                case 1 -> recipeItem = Items.BLAST_FURNACE.getDefaultInstance();
                case 2 -> recipeItem = Items.SMOKER.getDefaultInstance();
                case 3 -> recipeItem = Items.CAMPFIRE.getDefaultInstance();
                default -> recipeItem = Items.FURNACE.getDefaultInstance();
            }
            ItemRendererHelper.renderFakeItemTransparent(gui, recipeItem, leftPos + 25, topPos + 5, 96,
                5, 5, 5, 150);
        }
        gui.pose().translate(0, 0, 200);
        super.drawExtraBg(gui, tickDelta, x, y);
    }
    
    @Override
    public void renderBackground(GuiGraphics gui, int mx, int my, float deltaTick) {
        PoseStack pose = gui.pose();
        pose.pushPose();
        pose.translate(0, 0, -200);
        super.renderBackground(gui, mx, my, deltaTick);
        pose.popPose();
        renderProgressArrow(gui, leftPos + 81, topPos + 41, mx, my);
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
        renderDefaultEnergyBar(gui);
        renderHeat(gui, leftPos + 30, topPos + 56);
    }

    private void renderHeat(GuiGraphics gui, int x, int y) {
        double heat = ((InductionFurnace) menu.getBlockEntity()).getHeatPercentage();
        gui.drawString(font, ModConstants.GUI.withSuffixTranslatable("heat").append(": ")
                .append(String.format("%.2f", heat)).append("%"), x, y, labelColor, false);
    }
}
