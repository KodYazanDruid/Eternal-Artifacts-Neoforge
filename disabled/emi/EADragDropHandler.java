package com.sonamorningstar.eternalartifacts.compat.emi;

import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.FilterSlotWidget;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SimpleDraggablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SlotWidget;
import com.sonamorningstar.eternalartifacts.container.BlockInteractorMenu;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.container.base.FilterSyncable;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.container.slot.FilterFakeSlot;
import com.sonamorningstar.eternalartifacts.content.block.entity.BlockBreaker;
import com.sonamorningstar.eternalartifacts.event.client.ClientEvents;
import com.sonamorningstar.eternalartifacts.network.BlockStateFilterToServer;
import com.sonamorningstar.eternalartifacts.network.FluidStackFilterToServer;
import com.sonamorningstar.eternalartifacts.network.UpdateFakeSlotToServer;
import com.sonamorningstar.eternalartifacts.network.Channel;
import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

import java.util.List;

public class EADragDropHandler implements EmiDragDropHandler<Screen> {
    @Override
    public boolean dropStack(Screen screen, EmiIngredient emiIngredient, int mX, int mY) {
        if (screen instanceof AbstractModContainerScreen<?> amcs) {
            AbstractModContainerMenu menu = amcs.getMenu();
            List<FakeSlot> allFakeSlots = amcs.getAllFakeSlots();
            for (FakeSlot fakeSlot : allFakeSlots) {
                if (amcs.isWidgetManagedSlot(fakeSlot)) {
                    SlotWidget widgetForSlot = amcs.getWidgetForSlot(fakeSlot);
                    if (widgetForSlot != null) {
                        int xPos = widgetForSlot.getX();
                        int yPos = widgetForSlot.getY();
                        int width = widgetForSlot.getWidth();
                        int height = widgetForSlot.getHeight();
                        boolean isWidgetAndHovered = widgetForSlot.isMouseOver(mX, mY);
                        Rect2i slotArea = new Rect2i(xPos, yPos, width, height);
                        if (slotArea.contains(mX, mY) && isWidgetAndHovered) {
                            if (fakeSlot instanceof FilterFakeSlot filterFakeSlot && widgetForSlot instanceof FilterSlotWidget filterSlotWidget)
                                return addFilter(emiIngredient, filterFakeSlot, filterSlotWidget, menu);
                            else return setFakeSlot(emiIngredient, fakeSlot, menu);
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private static boolean addFilter(EmiIngredient emiIngredient, FilterFakeSlot fakeSlot, FilterSlotWidget widget, AbstractModContainerMenu menu) {
        EmiStack emiStack = emiIngredient.getEmiStacks().get(0);
        ItemStack stack = emiStack.getItemStack();
        Object key = emiStack.getKey();
        int slotIndex = fakeSlot.getSlotIndex();
        
        if (menu instanceof AbstractMachineMenu machineMenu) {
            FilterEntry existing = fakeSlot.getFilter();
            if (existing instanceof FluidFilterEntry && key instanceof Fluid fluid) {
                FluidStack fluidStack = new FluidStack(fluid, 1000);
                FluidStackEntry entry = new FluidStackEntry(fluidStack, machineMenu.isFluidIgnoresNbt());
                fakeSlot.setFilter(entry);
                fakeSlot.set(ItemStack.EMPTY);
                machineMenu.getFluidFilterEntries().set(slotIndex, entry);
                widget.setFilterEntry(entry);
                Channel.sendToServer(new FluidStackFilterToServer(menu.containerId, slotIndex, fluidStack));
                return true;
            } else if (existing instanceof FluidFilterEntry && key instanceof Item item) {
                FluidStack fluidInItem = FluidUtil.getFluidContained(stack.copy()).orElse(FluidStack.EMPTY);
                if (!fluidInItem.isEmpty()) {
                    FluidStackEntry entry = new FluidStackEntry(fluidInItem, machineMenu.isFluidIgnoresNbt());
                    fakeSlot.setFilter(entry);
                    fakeSlot.set(ItemStack.EMPTY);
                    machineMenu.getFluidFilterEntries().set(slotIndex, entry);
                    widget.setFilterEntry(entry);
                    Channel.sendToServer(new FluidStackFilterToServer(menu.containerId, slotIndex, fluidInItem));
                    return true;
                }
            }else if (existing instanceof BlockFilterEntry && key instanceof BlockItem bi) {
                BlockState state = bi.getBlock().defaultBlockState();
                BlockStateEntry entry = BlockStateEntry.matchBlockOnly(state);
                fakeSlot.setFilter(entry);
                fakeSlot.set(ItemStack.EMPTY);
                machineMenu.getBlockFilterEntries().set(slotIndex, entry);
                widget.setFilterEntry(entry);
                Channel.sendToServer(new BlockStateFilterToServer(menu.containerId, slotIndex, state));
                return true;
            } else if (existing instanceof ItemFilterEntry && key instanceof Item) {
                ItemStack filterStack = stack.copyWithCount(1);
                ItemStackEntry entry = new ItemStackEntry(filterStack, machineMenu.isItemIgnoresNbt());
                fakeSlot.setFilter(entry);
                fakeSlot.set(filterStack);
                machineMenu.getItemFilterEntries().set(slotIndex, entry);
                widget.setFilterEntry(entry);
                Channel.sendToServer(new UpdateFakeSlotToServer(menu.containerId, slotIndex, filterStack));
                return true;
            }
        }
        return false;
    }
    
    private static boolean setFakeSlot(EmiIngredient emiIngredient, FakeSlot fakeSlot, AbstractModContainerMenu menu) {
        EmiStack emiStack = emiIngredient.getEmiStacks().get(0);
        ItemStack stack = emiStack.getItemStack();
        int slotIndex = fakeSlot.getSlotIndex();
        fakeSlot.set(stack);
        Channel.sendToServer(new UpdateFakeSlotToServer(menu.containerId, slotIndex, stack));
        return true;
    }
    
    @Override
    public void render(Screen screen, EmiIngredient dragged, GuiGraphics draw, int mouseX, int mouseY, float delta) {
        List<EmiStack> emiStacks = dragged.getEmiStacks();
        EmiStack emiStack = emiStacks.get(0);
        ItemStack stack = emiStack.getItemStack();
        Object key = emiStack.getKey();
        
        if (screen instanceof AbstractModContainerScreen<?> amcs && (key instanceof Item || key instanceof Fluid)) {
            List<FakeSlot> allFakeSlots = amcs.getAllFakeSlots();
            
            for (FakeSlot fakeSlot : allFakeSlots) {
                if (!amcs.isWidgetManagedSlot(fakeSlot)) {
                    int minX = amcs.getGuiLeft() + fakeSlot.x;
                    int minY = amcs.getGuiTop() + fakeSlot.y;
                    int maxX = minX + 16;
                    int maxY = minY + 16;
                    draw.fill(minX, minY, maxX, maxY, 0x807497EA);
                }
            }
            
            for (FakeSlot fakeSlot : allFakeSlots) {
                if (amcs.isWidgetManagedSlot(fakeSlot)) {
                    SlotWidget widgetForSlot = amcs.getWidgetForSlot(fakeSlot);
                    if (widgetForSlot instanceof FilterSlotWidget filterSlotWidget) {
                        SimpleDraggablePanel panel = amcs.getPanelForWidget(widgetForSlot);
                        if (panel != null && panel.visible && panel.active) {
                            filterSlotWidget.setDraggingOnRV(true);
                        }
                    }
                }
            }
            
            if (amcs.isAnyPanelOpen()) {
                if (key instanceof Item) ClientEvents.recipeViewDraggedStack = stack;
                if (key instanceof Fluid) ClientEvents.recipeViewDraggedFluid = new FluidStack((Fluid) key, 1000);
            }
        }
    }
}
