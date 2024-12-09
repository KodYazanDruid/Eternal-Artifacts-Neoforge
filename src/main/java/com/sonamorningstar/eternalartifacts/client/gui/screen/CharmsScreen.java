package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.container.CharmsMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.HashMap;
import java.util.Map;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class CharmsScreen extends AbstractModContainerScreen<CharmsMenu> {
    public static final ResourceLocation ENTITY_BACKGROUND = new ResourceLocation(MODID, "entity_background");
    public static final Map<Integer, ResourceLocation> slotTextures = new HashMap<>();

    public CharmsScreen(CharmsMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        setModular(true);
        setImageSize(176, 166);
        this.inventoryLabelY = this.imageHeight - 92;
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
        gui.blitSprite(ENTITY_BACKGROUND, leftPos + 62, topPos + 7, 51, 72);
        InventoryScreen.renderEntityInInventoryFollowsMouse(
                gui, leftPos + 63, topPos + 8, leftPos + 112, topPos + 78,
                30, 0.0625F, mx, my, minecraft.player
        );
        renderTooltip(gui, mx, my);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float tick, int mX, int mY) {
        super.renderBg(gui, tick, mX, mY);
        renderSlotDecorations(gui);
    }

    private void renderSlotDecorations(GuiGraphics gui) {
        int invSize = menu.player.getInventory().items.size();
        for (int i = invSize; i < menu.slots.size(); i++) {
            Slot slot = menu.getSlot(i);
            if (!slot.getItem().isEmpty()) continue;
            ResourceLocation sprite = slotTextures.getOrDefault(i - invSize, null);
            if (sprite != null) {
                gui.blitSprite(sprite, leftPos + slot.x, topPos + slot.y, 16, 16);
            }
        }
    }

    private void renderSlotTooltips(GuiGraphics gui, int mx, int my) {
        /*int invSize = menu.player.getInventory().items.size();
        for (int i = invSize; i < menu.slots.size(); i++) {
            Slot slot = menu.getSlot(i);
            if (!slot.getItem().isEmpty() || !slot.isHovered(mx, my)) continue;
            ResourceLocation sprite = slotTextures.getOrDefault(i - invSize, null);
            if (sprite != null) {
                renderTooltip(gui, slot.getItem(), mx, my);
            }
        }*/
    }
}
