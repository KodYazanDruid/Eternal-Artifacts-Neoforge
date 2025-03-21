package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SimpleDraggablePanel;
import com.sonamorningstar.eternalartifacts.container.FishMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
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
    private SimpleDraggablePanel draggablePanel;
    public FishScreen(FishMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    
    @Override
    protected void init() {
        super.init();
        draggablePanel = new SimpleDraggablePanel(10, 10, 150, 150, SimpleDraggablePanel.Bounds.of(0, 0, width, height));
        draggablePanel.addChildren((x, y, width, height) -> Button.builder(Component.empty(), b -> System.out.println("Button clicked"))
			.bounds(x + 10, y + 10, 20, 20).build());
        draggablePanel.addChildren((x, y, width, height) ->
            new SimpleDraggablePanel(x + 24, y + 10, 16, 16, SimpleDraggablePanel.Bounds.of(x, y, width, height))
        );
        addUpperLayerChild(draggablePanel);
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
