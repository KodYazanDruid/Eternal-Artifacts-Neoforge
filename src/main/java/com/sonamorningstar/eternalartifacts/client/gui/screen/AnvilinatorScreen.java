package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sonamorningstar.eternalartifacts.container.AnvilinatorMenu;
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

public class AnvilinatorScreen extends AbstractContainerScreen<AnvilinatorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/anvilator.png");
    private static final ResourceLocation BARS = new ResourceLocation(MODID, "textures/gui/bars.png");
    private EditBox name;
    private Button nameSwitchButton;
    private Font switchInfo;

    public AnvilinatorScreen(AnvilinatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        name = new EditBox(this.font, leftPos + 73, topPos + 24, 82, 13, Component.empty());
        name.setTextColor(-1);
        name.setTextColorUneditable(-1);
        name.setBordered(false);
        name.setMaxLength(50);
        name.setValue(this.menu.getBlockEntity().getName());
        name.setEditable(!menu.getBlockEntity().getEnableNaming());
        name.setFocused(!menu.getBlockEntity().getEnableNaming());
        addWidget(this.name);

        nameSwitchButton = addWidget(Button.builder(Component.empty(), this::setNameSwitch).bounds(leftPos + 70, topPos + 38, 85, 7).build());
    }

    private void setNameSwitch(Button button) {
        boolean invertedValue = !menu.getBlockEntity().getEnableNaming();
        name.setEditable(menu.getBlockEntity().getEnableNaming());
        name.setFocused(menu.getBlockEntity().getEnableNaming());

        String naming = "";
        if (invertedValue) {
            naming = name.getValue();
        };

        Channel.sendToServer(new PacketAnvilatorSwitchToServer(invertedValue, menu.getBlockEntity().getBlockPos(), naming));
    }

    @Override
    public void onClose() {
        Channel.sendToServer(new PacketAnvilatorSwitchToServer(menu.getBlockEntity().getEnableNaming(), menu.getBlockEntity().getBlockPos(), name.getValue()));
        super.onClose();
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        if(menu.getBlockEntity().getEnableNaming()) pGuiGraphics.blit(TEXTURE, x + 70, y + 20, 0, 166, 94, 16);
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
        if(menu.getBlockEntity().getEnableNaming()) offset = 0;
        else offset = 5;
        guiGraphics.blit(BARS, x + 69, y + 40, 48, offset, 5, 5, 64, 64);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + 122, y + 53, 179, 24, menu.getScaledProgress(), 16);
        }
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(BARS, x + 5, y + 20, 0, 0, 18, 56, 64, 64);
        guiGraphics.blit(BARS, x + 8, y + 73 - menu.getEnergyProgress(), 18, 53 - menu.getEnergyProgress(), 12, menu.getEnergyProgress(), 64, 64);
    }

    private void renderFluidBar(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(BARS, x + 24, y + 20, 30, 0, 18, 56, 64, 64);

        FluidStack stack = getMenu().getBlockEntity().getFluidStack();
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(stack.getFluid());
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(stack);
        if(stillTexture == null) return;

        TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);

        int tintColor = fluidTypeExtensions.getTintColor(stack);
        float alpha = ((tintColor >> 24) & 0xFF) / 255f;
        float red = ((tintColor >> 16) & 0xFF) / 255f;
        float green = ((tintColor >> 8) & 0xFF) / 255f;
        float blue = ((tintColor) & 0xFF) / 255f;
        guiGraphics.setColor(red, green, blue, alpha);
        guiGraphics.blitTiledSprite(
                sprite,
                x + 27,
                y + 73 - menu.getFluidProgress(),
                0, //isn't this the ACTUAL offset wtf
                12,
                menu.getFluidProgress(),
                0, // these are offsets for atlas x
                0, //  y
                16, // Sprite dimensions to cut.
                16, //
                16, // Resolutions. 16x16 works fine.
                16);
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
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
        inventoryLabelX = 46;

        int color;
        String key;
        if(menu.getBlockEntity().getEnableNaming()) {
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
