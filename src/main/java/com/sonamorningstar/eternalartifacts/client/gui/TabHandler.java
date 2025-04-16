package com.sonamorningstar.eternalartifacts.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.content.tabs.base.AbstractInventoryTab;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.core.ModInventoryTabs;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.OpenTabMenuToServer;
import com.sonamorningstar.eternalartifacts.registrar.TabType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class TabHandler {
    @Nullable
    public static TabHandler INSTANCE = null;
    public static List<TabType<?>> registeredTabs;
    public static Map<Item, TabType<?>> tabHolders = new IdentityHashMap<>();
    public List<TabType<?>> activeTabs = new ArrayList<>();
    @Nullable
    public TabType<?> currentTab = ModInventoryTabs.INVENTORY.get();
    public int xOff = 4;
    public int yOff = -28;

    public boolean requested = false;

    private TabHandler() { }

    public static final ResourceLocation UNSELECTED = new ResourceLocation("container/creative_inventory/tab_top_unselected_2");
    public static final ResourceLocation SELECTED = new ResourceLocation("container/creative_inventory/tab_top_selected_2");

    public static void onTabsConstruct(InventoryScreen screen) {
        INSTANCE = new TabHandler();
        INSTANCE.reloadTabs();
    }

    public static void onTabsFinalize() {
        INSTANCE = null;
    }

    public void reloadTabs() {
        activeTabs.clear();
        activeTabs.add(ModInventoryTabs.INVENTORY.get());
        activeTabs.add(ModInventoryTabs.CHARMS.get());
        CharmStorage charms = Minecraft.getInstance().player.getData(ModDataAttachments.CHARMS);
        for (int i = 0; i < charms.getSlots(); i++) {
            ItemStack charm = charms.getStackInSlot(i);
            Item item = charm.getItem();
            TabType<?> tabType = tabHolders.get(item);
            if (tabHolders.containsKey(item)) activeTabs.add(tabType);
        }
    }

    public void renderTabs(GuiGraphics gui, int x, int y) {
        for (int i = 0; i < activeTabs.size(); i++) {
            int yPos = y + yOff;
            TabType<?> inventoryTab = activeTabs.get(i);
            ResourceLocation sprite = inventoryTab == currentTab ? SELECTED : UNSELECTED;
            int height = inventoryTab == currentTab ? 32 : 28;
            gui.blitSprite(sprite, x + xOff + 27 * i, yPos, 26, height);
            renderItemAndDecorations(gui, inventoryTab.getIcon().get().getDefaultInstance(), x + 9 + (27 * i), yPos + 8);
        }
    }

    private void renderItemAndDecorations(GuiGraphics gui, ItemStack icon, int x, int y) {
        PoseStack poseStack = gui.pose();
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 100.0F);
        gui.renderItem(icon, x, y);
        gui.renderItemDecorations(Minecraft.getInstance().font, icon, x, y);
        poseStack.popPose();
    }

    public boolean listenClicks(int x, int y, double mouseX, double mouseY, Screen screen) {
        for (int i = 0; i < activeTabs.size(); i++) {
            TabType<? extends AbstractInventoryTab> tab = activeTabs.get(i);
            if (tab == currentTab) continue;
            int xPos = x + 27 * i;
            int yPos = y - 28;
            if (isInBounds(xPos, yPos, 26, 32, mouseX, mouseY)) {
                currentTab = tab;
                requested = true;
                Minecraft instance = Minecraft.getInstance();
                LocalPlayer player = instance.player;
                if (tab == ModInventoryTabs.INVENTORY.get() && player != null) {
                    player.closeContainer();
                    if (instance.gameMode != null && instance.gameMode.isServerControlledInventory()) player.sendOpenInventory();
                    else {
                        instance.getTutorial().onOpenInventory();
                        instance.setScreen(new InventoryScreen(player));
                    }
                } else Channel.sendToServer(new OpenTabMenuToServer(tab));
            }
        }
        return Minecraft.getInstance().screen != screen;
    }
    
    private boolean isInBounds(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
