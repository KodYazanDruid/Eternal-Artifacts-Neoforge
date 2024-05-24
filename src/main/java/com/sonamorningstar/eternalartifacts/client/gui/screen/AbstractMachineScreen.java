package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sonamorningstar.eternalartifacts.client.gui.widget.CustomRenderButton;
import com.sonamorningstar.eternalartifacts.container.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.SidedTransferBlockEntity;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.SidedTransferAutoSaveToServer;
import com.sonamorningstar.eternalartifacts.network.SidedTransferSideSaveToServer;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class AbstractMachineScreen<T extends AbstractMachineMenu> extends AbstractContainerScreen<T> {
    private static final ResourceLocation bars = new ResourceLocation(MODID, "textures/gui/bars.png");
    @Nonnull
    @Setter
    protected static ResourceLocation texture = new ResourceLocation(MODID, "textures/gui/template.png");
    protected int x;
    protected int y;
    private boolean sidedTransferBarActive;
    private final List<CustomRenderButton> sideSetters = new ArrayList<>(6);
    private final List<CustomRenderButton> autoSetters = new ArrayList<>(2);
    private static final ResourceLocation allow = new ResourceLocation(MODID,"textures/gui/sprites/allow.png");
    private static final ResourceLocation deny = new ResourceLocation(MODID,"textures/gui/sprites/deny.png");
    private static final ResourceLocation input = new ResourceLocation(MODID,"textures/gui/sprites/input.png");
    private static final ResourceLocation output = new ResourceLocation(MODID,"textures/gui/sprites/output.png");
    private static final ResourceLocation auto_input = new ResourceLocation(MODID,"textures/gui/sprites/auto_input.png");
    private static final ResourceLocation auto_output = new ResourceLocation(MODID,"textures/gui/sprites/auto_output.png");
    private static final ResourceLocation auto_input_enabled = new ResourceLocation(MODID,"textures/gui/sprites/auto_input_enabled.png");
    private static final ResourceLocation auto_output_enabled = new ResourceLocation(MODID,"textures/gui/sprites/auto_output_enabled.png");
    public AbstractMachineScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        for(int i = 0; i < 6; i++) {
            int finalI = i;
            sideSetters.add(CustomRenderButton.builder(Component.empty(), button -> buttonSideSet(button, finalI), allow).size(9, 9).build());
            addRenderableWidget(sideSetters.get(i));
        }
        for(int i = 0; i < 2; i++) {
            int finalI = i;
            autoSetters.add(CustomRenderButton.builderNoTexture(Component.empty(), button -> buttonAutoSet(button, finalI)).size(9, 9).build());
            addRenderableWidget(autoSetters.get(i));
        }
        autoSetters.get(0).setTextures(auto_input);
        autoSetters.get(1).setTextures(auto_output);
    }

    private void buttonSideSet(Button button, int index) {
        Channel.sendToServer(new SidedTransferSideSaveToServer(
                index,
                SidedTransferBlockEntity.TransferType.cycleNext(index, ((SidedTransferBlockEntity<?>) menu.getBlockEntity())),
                menu.getBlockEntity().getBlockPos()));
    }

    private void buttonAutoSet(Button button, int index) {
        BlockEntity be = menu.getBlockEntity();
        if(be instanceof SidedTransferBlockEntity<?> sided) {
            boolean auto = sided.getAutoConfigs().get(index) != null && sided.getAutoConfigs().get(index);
            Channel.sendToServer(new SidedTransferAutoSaveToServer(index, !auto, menu.getBlockEntity().getBlockPos()));
        }
    }

    @Override
    protected void renderBg(GuiGraphics gui, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        this.x = (width - imageWidth) / 2;
        this.y = (height - imageHeight) / 2;
        gui.blit(texture, x, y, 0, 0, imageWidth, imageHeight);
        for(Slot slot : menu.slots) {
            gui.blit(bars, x + slot.x-1, y + slot.y-1, 48, 37, 18, 18);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float pPartialTick) {
        inventoryLabelX = 46;
        sidedTransferBarActive = mx >= x+5 && mx <= x+101 && my >= y-29 && my <= y+3;
        renderBackground(gui, mx, my, pPartialTick);
        super.render(gui, mx, my, pPartialTick);
        renderTooltip(gui, mx, my);
    }

    protected void renderDefaultEnergyAndFluidBar(GuiGraphics gui) {
        renderDefaultEnergyBar(gui);
        renderDefaultFluidBar(gui);
    }
    protected void renderDefaultEnergyBar(GuiGraphics gui) {
        renderEnergyBar(gui, x + 5, y + 20);
    }
    protected void renderDefaultFluidBar(GuiGraphics gui) {
        renderFluidBar(gui, x + 24, y + 20);
    }

    protected void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(bars, x, y, 0, 0, 18, 56);
        guiGraphics.blit(bars, x + 3, y + 53 - menu.getEnergyProgress(), 18, 53 - menu.getEnergyProgress(), 12, menu.getEnergyProgress());
    }

    protected void renderFluidBar(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(bars, x, y, 30, 0, 18, 56);
        IFluidHandler tank = menu.getBlockEntity().getLevel().getCapability(Capabilities.FluidHandler.BLOCK, menu.getBlockEntity().getBlockPos(), null);
        FluidStack stack = FluidStack.EMPTY;
        if(tank != null) stack = tank.getFluidInTank(0);
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
                x + 3,
                y + 53 - menu.getFluidProgress(),
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

    protected void renderBurn(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(bars, x + 1, y + 1, 48, 10, 13, 13);
        if(menu.isWorking()) guiGraphics.blit(bars, x, y + menu.getScaledProgress(14), 0,  72 + menu.getScaledProgress(14), 14, 14 - menu.getScaledProgress(14));
    }

    protected void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(bars, x, y, 0, 56, 22, 15);
        if(menu.isWorking()) guiGraphics.blit(bars, x, y, 22, 56, menu.getScaledProgress(22), 15);

    }

    protected void renderLArraow(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(bars, x, y, 53, 0, 10, 9);
    }

    // 0 -> top
    // 1 -> left
    // 2 -> front
    // 3 -> right
    // 4 -> bottom
    // 5 -> back
    protected void renderSidedTransferTab(GuiGraphics guiGraphics, SidedTransferBlockEntity<?> sided) {
        int sidedX = x + 5;
        int sidedY = y - 29;
        Map<Integer, SidedTransferBlockEntity.TransferType> side = sided.getSideConfigs();
        Map<Integer, Boolean> auto = sided.getAutoConfigs();
        sideSetters.forEach(button -> button.visible = sidedTransferBarActive);
        autoSetters.forEach(button -> button.visible = sidedTransferBarActive);
        if(sidedTransferBarActive) {
            guiGraphics.blit(bars, sidedX, sidedY, 0, 84, 96, 32);
            for(int i = 0; i < sideSetters.size(); i++) {
                switch (i) {
                    case 0 -> sideSetters.get(i).setPosition(sidedX + 13, sidedY + 3);
                    case 1 -> sideSetters.get(i).setPosition(sidedX + 3, sidedY + 13);
                    case 2 -> sideSetters.get(i).setPosition(sidedX + 13, sidedY + 13);
                    case 3 -> sideSetters.get(i).setPosition(sidedX + 23, sidedY + 13);
                    case 4 -> sideSetters.get(i).setPosition(sidedX + 13, sidedY + 23);
                    case 5 -> sideSetters.get(i).setPosition(sidedX + 23, sidedY + 23);
                }
                sideSetters.get(i).setTextures(getTextureForTransferType(side.get(i)));
            }
            for(int i = 0; i < autoSetters.size(); i++) {
                switch (i) {
                    case 0 -> {
                        autoSetters.get(i).setPosition(sidedX + 37, sidedY + 7);
                        if(auto.get(i) != null && auto.get(i)) autoSetters.get(i).setTextures(auto_input_enabled);
                        else autoSetters.get(i).setTextures(auto_input);
                    }
                    case 1 -> {
                        autoSetters.get(i).setPosition(sidedX + 37, sidedY + 17);
                        if(auto.get(i) != null && auto.get(i)) autoSetters.get(i).setTextures(auto_output_enabled);
                        else  autoSetters.get(i).setTextures(auto_output);
                    }
                }
            }
        } else {
            guiGraphics.blit(bars, sidedX, sidedY + 26, 0, 84, 96, 6);
        }
    }

    private ResourceLocation getTextureForTransferType(SidedTransferBlockEntity.TransferType transferType) {
        if(transferType == SidedTransferBlockEntity.TransferType.DEFAULT) return allow;
        if(transferType == SidedTransferBlockEntity.TransferType.NONE) return deny;
        if(transferType == SidedTransferBlockEntity.TransferType.PULL) return input;
        if(transferType == SidedTransferBlockEntity.TransferType.PUSH) return output;
        return allow;
    }

}
