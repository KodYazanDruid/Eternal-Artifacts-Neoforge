package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SpriteButton;
import com.sonamorningstar.eternalartifacts.client.gui.widget.records.ButtonDrawContent;
import com.sonamorningstar.eternalartifacts.container.PortableBatteryMenu;
import com.sonamorningstar.eternalartifacts.content.item.BatteryItem;
import com.sonamorningstar.eternalartifacts.content.item.PortableBatteryItem;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.SwitchBatteryChargeToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static com.sonamorningstar.eternalartifacts.content.item.PortableBatteryItem.SlotType;

public class PortableBatteryScreen extends AbstractModContainerScreen<PortableBatteryMenu> {
    private static final ResourceLocation buttons = new ResourceLocation(MODID, "textures/gui/portable_charger_buttons.png");

    private SpriteButton inventory;
    private SpriteButton heldMain;
    private SpriteButton heldOff;
    private SpriteButton armor;
    private SpriteButton hotbar;
    private SpriteButton enableCharging;

    public PortableBatteryScreen(PortableBatteryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        setModular(true);
        setImageSize(176, 166);
        this.inventoryLabelY = this.imageHeight - 92;
    }

    @Override
    protected void init() {
        super.init();
        inventory = SpriteButton.builder(ModConstants.GUI.withSuffixTranslatable("inventory"),
                (b, i) -> toggleChargeForSlot(b, SlotType.INVENTORY))
                .bounds(leftPos + 46, topPos + 8, 126, 36)
                .build();
        heldMain = SpriteButton.builder(ModConstants.GUI.withSuffixTranslatable("main_hand"),
                (b, i) -> toggleChargeForSlot(b, SlotType.HELD_MAIN))
                .bounds(leftPos + 28, topPos + 62, 18, 18)
                .build();
        heldOff = SpriteButton.builder(ModConstants.GUI.withSuffixTranslatable("off_hand"),
                (b, i) -> toggleChargeForSlot(b, SlotType.HELD_OFF))
                .bounds(leftPos + 154, topPos + 62, 18, 18)
                .build();
        armor = SpriteButton.builder(ModConstants.GUI.withSuffixTranslatable("armor"),
                (b, i) -> toggleChargeForSlot(b, SlotType.ARMOR))
                .bounds(leftPos + 28, topPos + 8, 18, 54)
                .build();
        hotbar = SpriteButton.builder(ModConstants.GUI.withSuffixTranslatable("hotbar"),
                (b, i) -> toggleChargeForSlot(b, SlotType.HOTBAR))
                .bounds(leftPos + 46, topPos + 44, 126, 18)
                .build();
        enableCharging = SpriteButton.builder(ModConstants.GUI.withSuffixTranslatable("toggle_charging"),
                (b, i) -> toggleCharging())
                .bounds(leftPos + 73, topPos + 62, 54, 18)
                .build();
        /*setupSprites(inventory, SlotType.INVENTORY.getKey(), 18, 0);
        setupSprites(heldMain, SlotType.HELD_MAIN.getKey(), 0, 54);
        setupSprites(heldOff, SlotType.HELD_OFF.getKey(), 126, 54);
        setupSprites(armor, SlotType.ARMOR.getKey(), 0, 0);
        setupSprites(hotbar, SlotType.HOTBAR.getKey(), 18, 36);
        setupSprites(enableCharging, PortableBatteryItem.KEY_CHARGE, 45, 54);*/
        addRenderableWidget(inventory);
        addRenderableWidget(heldMain);
        addRenderableWidget(heldOff);
        addRenderableWidget(armor);
        addRenderableWidget(hotbar);
        addRenderableWidget(enableCharging);
    }

    private void toggleChargeForSlot(SpriteButton button, SlotType type) {
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, type.ordinal());
    }

    private void toggleCharging() {
        Channel.sendToServer(new SwitchBatteryChargeToServer(ModItems.PORTABLE_BATTERY.get()));
    }

    private void setupSprites(SpriteButton button, String key, int u, int v) {
        var ctx = new ButtonDrawContent(button.getWidth(), button.getHeight());
        ItemStack stack = PlayerCharmManager.findCharm(Minecraft.getInstance().player, PortableBatteryItem.class);
        boolean flag = stack.hasTag();
        if (flag) {
            if (PortableBatteryItem.KEY_CHARGE.equals(key)){
                flag = BatteryItem.isCharging(stack);
            }else {
                CompoundTag tag = stack.getTag().getCompound(SlotType.COMPOUND);
                flag = tag.getBoolean(key);
            }
        }
        v = flag ? v : v + 128;
        ctx.addSprite(buttons, u, v, 256, 256, ButtonDrawContent.SpriteStyle.SINGLE);
        button.setSprites(ctx);
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float delta) {
        setupSprites(inventory, SlotType.INVENTORY.getKey(), 18, 0);
        setupSprites(heldMain, SlotType.HELD_MAIN.getKey(), 0, 54);
        setupSprites(heldOff, SlotType.HELD_OFF.getKey(), 126, 54);
        setupSprites(armor, SlotType.ARMOR.getKey(), 0, 0);
        setupSprites(hotbar, SlotType.HOTBAR.getKey(), 18, 36);
        setupSprites(enableCharging, PortableBatteryItem.KEY_CHARGE, 45, 54);
        super.render(gui, mx, my, delta);
        renderEnergyBar(gui, mx, my);
        renderTooltip(gui, mx, my);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {

    }

    private void renderEnergyBar(GuiGraphics gui, int mx, int my) {
        ItemStack stack = PlayerCharmManager.findCharm(Minecraft.getInstance().player, PortableBatteryItem.class);
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null) {
            GuiDrawer.drawEnergyBar(gui, stack, leftPos + 8, topPos + 16);
            if (isCursorInBounds(leftPos + 8, topPos + 16, 16, 56, mx, my)) {
                Component tooltip = ModConstants.GUI.withSuffixTranslatable("energy").append(": ").append(String.valueOf(energy.getEnergyStored())).append("/").append(String.valueOf(energy.getMaxEnergyStored()));
                gui.renderTooltip(font, tooltip, mx, my);
            }
        }
    }

}
