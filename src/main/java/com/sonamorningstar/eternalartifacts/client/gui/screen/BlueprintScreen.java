package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.container.BlueprintMenu;
import com.sonamorningstar.eternalartifacts.container.slot.BlueprintFakeSlot;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.content.item.BlueprintItem;
import com.sonamorningstar.eternalartifacts.network.UpdateFakeSlotToServer;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.client.render.ItemRendererHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class BlueprintScreen extends AbstractModContainerScreen<BlueprintMenu> {
    public BlueprintScreen(BlueprintMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        renderEffects = false;
        setGuiTint(0xFF7497ea);
    }
    
    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.empty(), this::toggleTag)
            .bounds(leftPos + 10, topPos + 10, 10, 10)
            .build());
    }
    
    private void toggleTag(Button button) {
        BlueprintItem.toggleUseTags(menu.getBlueprint());
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId,0);
    }
    
    @Override
    public void render(GuiGraphics gui, int mX, int mY, float partTick) {
        super.render(gui, mX, mY, partTick);
        applyGuiTint(gui);
        GuiDrawer.drawEmptyArrow(gui, leftPos + 90, topPos + 35);
        clearGuiTint(gui);
        renderTooltip(gui, mX, mY);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        super.slotClicked(slot, slotId, mouseButton, type);
        if (slot instanceof FakeSlot fakeSlot && !fakeSlot.isDisplayOnly()) {
            ItemStack carried = menu.getCarried();
            if (carried.isEmpty()) {
                ItemStack stack = slot.getItem();
                if (!stack.isEmpty()) {
                    fakeSlot.set(ItemStack.EMPTY);
                    updateItem(menu.containerId, fakeSlot.getSlotIndex(), ItemStack.EMPTY);
                }
            } else {
                ItemStack stack = carried.copyWithCount(1);
                fakeSlot.set(stack);
                updateItem(menu.containerId, fakeSlot.getSlotIndex(), stack);
            }
        }
    }

    private void updateItem(int menuId, int slotIndex, ItemStack stack) {
        Channel.sendToServer(new UpdateFakeSlotToServer(menuId, slotIndex, stack));
    }
    
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        if (menu.getCarried().isEmpty() &&
                this.hoveredSlot != null &&
                this.hoveredSlot.hasItem()) {
            if (hoveredSlot instanceof BlueprintFakeSlot fakeSlot && BlueprintItem.isUsingTags(menu.getBlueprint())) {
                Ingredient ingredient = fakeSlot.getRecipeIngredient();
                long tick = Minecraft.getInstance().clientTickCount;
                ItemStack[] values = ingredient.getItems();
                if (values.length == 0) {
                    super.renderTooltip(guiGraphics, x, y);
                    return;
                }
                ItemStack itemStack = values[(int) ((tick / 20) % ingredient.getItems().length)];
                guiGraphics.renderTooltip(font, getTooltipFromContainerItem(itemStack), itemStack.getTooltipImage(), itemStack, x, y);
            } else {
                ItemStack itemstack = hoveredSlot.getItem();
                guiGraphics.renderTooltip(font, this.getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), itemstack, x, y);
            }
        }
    }
    
    @Override
    protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, int x, int y, @Nullable String countString) {
        if (slot instanceof BlueprintFakeSlot fakeSlot && !fakeSlot.isDisplayOnly() && BlueprintItem.isUsingTags(menu.getBlueprint())) {
            if (!ItemRendererHelper.renderItemCarousel(guiGraphics, fakeSlot.getRecipeIngredient().getItems(),
                x, y, 0x8054FFA3, 1.0F)) {
                super.renderSlotContents(guiGraphics, itemstack, slot, x, y, countString);
			}
        } else {
            super.renderSlotContents(guiGraphics, itemstack, slot, x, y, countString);
        }
    }
}
