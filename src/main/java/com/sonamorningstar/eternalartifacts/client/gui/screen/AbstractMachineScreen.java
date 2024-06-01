package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.capabilities.IHasEnergy;
import com.sonamorningstar.eternalartifacts.capabilities.IHasFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.IHasMultiFluidTank;
import com.sonamorningstar.eternalartifacts.client.gui.widget.CustomRenderButton;
import com.sonamorningstar.eternalartifacts.container.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.SidedTransferBlockEntity;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.SidedTransferAutoSaveToServer;
import com.sonamorningstar.eternalartifacts.network.SidedTransferRedstoneToServer;
import com.sonamorningstar.eternalartifacts.network.SidedTransferSideSaveToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
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
import java.util.HashMap;
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
    private final Map<String, Integer> energyLoc = new HashMap<>();
    private final Map<Integer, Map<String, Integer>> fluidLocs = new HashMap<>();
    private boolean sidedTransferBarActive;
    private final List<CustomRenderButton> sideSetters = new ArrayList<>(6);
    private final List<CustomRenderButton> autoSetters = new ArrayList<>(4);
    private final List<CustomRenderButton> redstoneSetters = new ArrayList<>(1);
    private static final ResourceLocation allow = new ResourceLocation(MODID,"textures/gui/sprites/allow.png");
    private static final ResourceLocation deny = new ResourceLocation(MODID,"textures/gui/sprites/deny.png");
    private static final ResourceLocation input = new ResourceLocation(MODID,"textures/gui/sprites/input.png");
    private static final ResourceLocation output = new ResourceLocation(MODID,"textures/gui/sprites/output.png");
    private static final ResourceLocation auto_input = new ResourceLocation(MODID,"textures/gui/sprites/auto_input.png");
    private static final ResourceLocation auto_output = new ResourceLocation(MODID,"textures/gui/sprites/auto_output.png");
    private static final ResourceLocation auto_input_enabled = new ResourceLocation(MODID,"textures/gui/sprites/auto_input_enabled.png");
    private static final ResourceLocation auto_output_enabled = new ResourceLocation(MODID,"textures/gui/sprites/auto_output_enabled.png");
    private static final ResourceLocation item_transfer = new ResourceLocation(MODID,"textures/gui/sprites/item_transfer.png");
    private static final ResourceLocation fluid_transfer = new ResourceLocation(MODID,"textures/gui/sprites/fluid_transfer.png");
    private static final ResourceLocation item_transfer_disabled = new ResourceLocation(MODID,"textures/gui/sprites/item_transfer_disabled.png");
    private static final ResourceLocation fluid_transfer_disabled = new ResourceLocation(MODID,"textures/gui/sprites/fluid_transfer_disabled.png");
    private static final ResourceLocation redstone_active = new ResourceLocation(MODID,"textures/gui/sprites/redstone_active.png");
    private static final ResourceLocation redstone_passive = new ResourceLocation(MODID,"textures/gui/sprites/redstone_passive.png");
    private static final ResourceLocation redstone_ignored = new ResourceLocation(MODID,"textures/gui/sprites/redstone_ignored.png");

    public AbstractMachineScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        if(menu.getBlockEntity() instanceof SidedTransferBlockEntity<?>){
            for (int i = 0; i < 6; i++) {
                int finalI = i;
                sideSetters.add(CustomRenderButton.builder(Component.empty(), button -> buttonSideSet(button, finalI), allow).size(9, 9).build());
                addRenderableWidget(sideSetters.get(i));
            }
            for (int i = 0; i < 4; i++) {
                int finalI = i;
                autoSetters.add(CustomRenderButton.builderNoTexture(Component.empty(), button -> buttonAutoSet(button, finalI)).size(9, 9).build());
                addRenderableWidget(autoSetters.get(i));
            }
            for(int i = 0; i < 1; i++) {
                int finalI = i;
                redstoneSetters.add(CustomRenderButton.builderNoTexture(Component.empty(), button -> buttonRedstoneSet(button, finalI)).size(9, 9).build());
                addRenderableWidget(redstoneSetters.get(i));
            }
        }

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
            Channel.sendToServer(new SidedTransferAutoSaveToServer(index, !auto, sided.getBlockPos()));
        }
    }

    private void buttonRedstoneSet(Button button, int index) {
        Channel.sendToServer(new SidedTransferRedstoneToServer(
                index,
                SidedTransferBlockEntity.RedstoneType.cycleNext(index, ((SidedTransferBlockEntity<?>) menu.getBlockEntity())),
                menu.getBlockEntity().getBlockPos()));
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
        if(menu.getBlockEntity() instanceof SidedTransferBlockEntity<?> sided) renderSidedTransferTab(gui, sided);

    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float pPartialTick) {
        inventoryLabelX = 46;
        sidedTransferBarActive = mx >= x+5 && mx <= x+101 && my >= y-29 && my <= y+3;
        renderBackground(gui, mx, my, pPartialTick);
        super.render(gui, mx, my, pPartialTick);
        renderTooltip(gui, mx, my);
        if(!energyLoc.isEmpty() && mx >= energyLoc.get("x") && mx <= energyLoc.get("x") + energyLoc.get("width") &&
            my >= energyLoc.get("y") && my <= energyLoc.get("y") + energyLoc.get("height") && menu.getBlockEntity() instanceof IHasEnergy en) {
            gui.renderTooltip(font,
                    Component.translatable(ModConstants.GUI.withSuffix("energy")).append(": ")
                            .append(String.valueOf(en.getEnergy().getEnergyStored())).append("/").append(String.valueOf(en.getEnergy().getMaxEnergyStored())),
                    mx, my);
        }

        fluidLocs.forEach( (tank, fluidLoc) -> {
            if (!fluidLoc.isEmpty() && mx >= fluidLoc.get("x") && mx <= fluidLoc.get("x") + fluidLoc.get("width") &&
                    my >= fluidLoc.get("y") && my <= fluidLoc.get("y") + fluidLoc.get("height")) {
                if(menu.getBlockEntity() instanceof  IHasFluidTank ft) {
                    gui.renderTooltip(font,
                            Component.translatable(ModConstants.GUI.withSuffix("fluid")).append(": ").append(ft.getTank().getFluid().getDisplayName()).append(" ")
                                    .append(String.valueOf(ft.getTank().getFluidAmount())).append(" / ").append(String.valueOf(ft.getTank().getCapacity())),
                            mx, my);
                }else if(menu.getBlockEntity() instanceof IHasMultiFluidTank mft) {
                    gui.renderTooltip(font,
                            Component.translatable(ModConstants.GUI.withSuffix("fluid")).append(": ").append(mft.getTanks().get(tank).getFluid().getDisplayName()).append(" ")
                                    .append(String.valueOf(mft.getTanks().get(tank).getFluidAmount())).append(" / ").append(String.valueOf(mft.getTanks().get(tank).getCapacity())),
                            mx, my);
                }
            }
        });
    }

    protected void renderDefaultEnergyAndFluidBar(GuiGraphics gui) {
        renderDefaultEnergyBar(gui);
        renderDefaultFluidBar(gui);
    }
    protected void renderDefaultEnergyBar(GuiGraphics gui) {
        renderEnergyBar(gui, x + 5, y + 20);
    }
    protected void renderDefaultFluidBar(GuiGraphics gui) { renderDefaultFluidBar(gui, 0); }

    protected void renderDefaultFluidBar(GuiGraphics gui, int tankSlot) { renderFluidBar(gui, x + 24, y + 20, tankSlot); }

    protected void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(bars, x, y, 0, 0, 18, 56);
        guiGraphics.blit(bars, x + 3, y + 53 - menu.getEnergyProgress(), 18, 53 - menu.getEnergyProgress(), 12, menu.getEnergyProgress());
        energyLoc.put("x", x);
        energyLoc.put("y", y);
        energyLoc.put("width", 18);
        energyLoc.put("height", 56);
    }

    protected void renderFluidBar(GuiGraphics guiGraphics, int x, int y) { renderFluidBar(guiGraphics,  x,  y, 0); }

    protected void renderFluidBar(GuiGraphics guiGraphics, int x, int y, int tankSlot) {
        Map<String, Integer> fluidLoc = new HashMap<>();
        fluidLoc.put("x", x);
        fluidLoc.put("y", y);
        fluidLoc.put("width", 18);
        fluidLoc.put("height", 56);
        fluidLocs.put(tankSlot, fluidLoc);

        IFluidHandler tank = menu.getBlockEntity().getLevel().getCapability(Capabilities.FluidHandler.BLOCK, menu.getBlockEntity().getBlockPos(), menu.getBlockEntity().getBlockState(), menu.getBlockEntity(), null);
        FluidStack stack = FluidStack.EMPTY;
        if(tank != null) stack = tank.getFluidInTank(tankSlot);
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(stack.getFluid());
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(stack);
        if(stillTexture != null){
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
                    y + 53 - menu.getFluidProgress(tankSlot),
                    0, //z - layer
                    12,
                    menu.getFluidProgress(tankSlot),
                    0, // these are offsets for atlas x
                    0, //  y
                    16, // Sprite dimensions to cut.
                    16, //
                    16, // Resolutions. 16x16 works fine.
                    16);
            guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        guiGraphics.blit(bars, x, y, 30, 0, 18, 56);
    }

    protected void renderBurn(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(bars, x + 1, y + 1, 48, 10, 13, 13);
        if(menu.isWorking()) guiGraphics.blit(bars, x, y + 14 - menu.getScaledProgress(14), 48,  37 - menu.getScaledProgress(14), 14, menu.getScaledProgress(14));
    }

    protected void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(bars, x, y, 0, 56, 22, 15);
        if(menu.isWorking()) guiGraphics.blit(bars, x, y, 22, 56, menu.getScaledProgress(22), 15);
    }

    protected void renderLArraow(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(bars, x, y, 53, 0, 10, 9);
    }

    protected void renderSidedTransferTab(GuiGraphics guiGraphics, SidedTransferBlockEntity<?> sided) {
        int sidedX = x + 5;
        int sidedY = y - 29;
        Map<Integer, SidedTransferBlockEntity.TransferType> side = sided.getSideConfigs();
        Map<Integer, Boolean> auto = sided.getAutoConfigs();
        Map<Integer, SidedTransferBlockEntity.RedstoneType> redstone = sided.getRedstoneConfigs();
        sideSetters.forEach(button -> button.visible = sidedTransferBarActive);
        autoSetters.forEach(button -> button.visible = sidedTransferBarActive);
        redstoneSetters.forEach(button -> button.visible = sidedTransferBarActive);
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
                        else autoSetters.get(i).setTextures(auto_output);
                    }
                    case 2 -> {
                        autoSetters.get(i).setPosition(sidedX + 47, sidedY + 7);
                        if(auto.get(i) != null && auto.get(i)) autoSetters.get(i).setTextures(item_transfer_disabled);
                        else autoSetters.get(i).setTextures(item_transfer);
                    }
                    case 3 -> {
                        autoSetters.get(i).setPosition(sidedX + 47, sidedY + 17);
                        if(auto.get(i) != null && auto.get(i)) autoSetters.get(i).setTextures(fluid_transfer_disabled);
                        else autoSetters.get(i).setTextures(fluid_transfer);
                    }
                }
            }
            redstoneSetters.get(0).setPosition(sidedX + 57, sidedY + 12);
            redstoneSetters.get(0).setTextures(getTextureForRedstoneType(redstone.get(0)));

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

    private ResourceLocation getTextureForRedstoneType(SidedTransferBlockEntity.RedstoneType redstoneType) {
        if(redstoneType == SidedTransferBlockEntity.RedstoneType.IGNORED) return redstone_ignored;
        if(redstoneType == SidedTransferBlockEntity.RedstoneType.HIGH) return redstone_active;
        if(redstoneType == SidedTransferBlockEntity.RedstoneType.LOW) return redstone_passive;
        return redstone_ignored;
    }

}
