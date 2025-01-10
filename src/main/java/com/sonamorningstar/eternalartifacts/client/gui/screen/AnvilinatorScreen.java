package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.AnvilinatorMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.AnvilinatorBlockEntity;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.PacketAnvilatorSwitchToServer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class AnvilinatorScreen extends AbstractSidedMachineScreen<AnvilinatorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/anvilator.png");
    private static final ResourceLocation BARS = new ResourceLocation(MODID, "textures/gui/bars.png");
    private EditBox name;
    private Button nameSwitchButton;
    private Font switchInfo;
    private final AnvilinatorBlockEntity anvilinatorBlockEntity;

    public AnvilinatorScreen(AnvilinatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.anvilinatorBlockEntity = (AnvilinatorBlockEntity) menu.getBlockEntity();
    }

    @Override
    protected void init() {
        super.init();

        name = new EditBox(this.font, leftPos + 73, topPos + 24, 82, 13, Component.empty());
        name.setTextColor(-1);
        name.setTextColorUneditable(-1);
        name.setBordered(false);
        name.setMaxLength(50);
        name.setValue(anvilinatorBlockEntity.getName());
        name.setEditable(!anvilinatorBlockEntity.getEnableNaming());
        name.setFocused(!anvilinatorBlockEntity.getEnableNaming());
        addWidget(this.name);

        nameSwitchButton = addWidget(Button.builder(Component.empty(), this::setNameSwitch).bounds(leftPos + 70, topPos + 38, 85, 7).build());
    }

    private void setNameSwitch(Button button) {
        boolean invertedValue = !anvilinatorBlockEntity.getEnableNaming();
        name.setEditable(anvilinatorBlockEntity.getEnableNaming());
        name.setFocused(anvilinatorBlockEntity.getEnableNaming());

        String naming = "";
        if (invertedValue) {
            naming = name.getValue();
        }

        Channel.sendToServer(new PacketAnvilatorSwitchToServer(invertedValue, menu.getBlockEntity().getBlockPos(), naming));
    }

    @Override
    public void onClose() {
        Channel.sendToServer(new PacketAnvilatorSwitchToServer(anvilinatorBlockEntity.getEnableNaming(), menu.getBlockEntity().getBlockPos(), name.getValue()));
        super.onClose();
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        if(anvilinatorBlockEntity.getEnableNaming()) pGuiGraphics.blit(TEXTURE, x + 70, y + 20, 0, 166, 94, 16);
        else pGuiGraphics.blit(TEXTURE, x + 70, y + 20, 0, 182, 94, 16);
        renderProgressArrow(pGuiGraphics, x, y);
        renderEnergyBar(pGuiGraphics, x, y);
        renderFluidBar(pGuiGraphics,x, y);
        renderSwitch(pGuiGraphics, x, y);
    }

    // For rendering widgets.
    private void renderFg(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.name.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    //Render misc stuff on the screen.
    private void renderSwitch(GuiGraphics guiGraphics, int x, int y) {
        int offset;
        if(anvilinatorBlockEntity.getEnableNaming()) offset = 0;
        else offset = 5;
        guiGraphics.blit(BARS, x + 69, y + 40, 48, offset, 5, 5);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isWorking()) {
            guiGraphics.blit(BARS, x + 122, y + 53, 0, 56, menu.getScaledProgress(22), 16);
        }
    }

    // Returns false when typing on the box.
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        //TODO: Use Open/Close inventory key instead of hardcoding E.
        if(name.isFocused() && pKeyCode == 69) return false;
        else return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        inventoryLabelX = 46;

        int color;
        String key;
        if(anvilinatorBlockEntity.getEnableNaming()) {
            color = 0x187718;
            key = "key." + MODID + ".anvilinator.enabled_naming";
        } else {
            key = "key." + MODID + ".anvilinator.disabled_naming";
            color = 0x4e0523;
        }
         pGuiGraphics.drawString(font, Component.translatable(key), leftPos + 76, topPos + 38, color);

        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderFg(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

}
