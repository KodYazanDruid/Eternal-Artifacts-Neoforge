package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SimpleDraggablePanel;
import com.sonamorningstar.eternalartifacts.container.FishMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.player.Inventory;

public class FishScreen extends AbstractModContainerScreen<FishMenu> {
    static final Cod fish = new Cod(EntityType.COD, Minecraft.getInstance().level) {
        @Override
        public boolean isInWater() {
            return true;
        }
    };
    public FishScreen(FishMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    
    @Override
    protected void init() {
        super.init();
        var draggable = new SimpleDraggablePanel(Component.empty(), leftPos + 23, topPos + 8, 129, 70,
            SimpleDraggablePanel.Bounds.of(0, 0, width, height));
        draggable.addChildren((fX, fY, fWidth, fHeight) -> Button.builder(Component.empty(), b -> {}).bounds(fX + 10, fY  + 10, 50, 20).build());
        addUpperLayerChild(draggable);
    }
    
    @Override
    public void render(GuiGraphics gui, int mx, int my, float partTick) {
        super.render(gui, mx, my, partTick);
        int tick = Minecraft.getInstance().player.tickCount;
        PoseStack pose = gui.pose();
        pose.pushPose();
        InventoryScreen.renderEntityInInventoryFollowsAngle(
                gui, leftPos + 23, topPos + 8, leftPos + 152, topPos + 78, 60,
                0.0625F, (tick + partTick) / 3, -0.5F, fish
        );
        pose.popPose();
        renderTooltip(gui, mx, my);
    }

}
