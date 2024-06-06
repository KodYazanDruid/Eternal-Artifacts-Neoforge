package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.sonamorningstar.eternalartifacts.client.gui.widget.CustomRenderButton;
import com.sonamorningstar.eternalartifacts.container.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.SidedTransferBlockEntity;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.SidedTransferAutoSaveToServer;
import com.sonamorningstar.eternalartifacts.network.SidedTransferRedstoneToServer;
import com.sonamorningstar.eternalartifacts.network.SidedTransferSideSaveToServer;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public abstract class AbstractSidedMachineScreen<T extends AbstractMachineMenu> extends AbstractMachineScreen<T> {
    private final SidedTransferBlockEntity<?> sidedTransferBlockEntity = ((SidedTransferBlockEntity<?>) menu.getBlockEntity());

    @Setter
    protected boolean redstoneControllable = true;
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

    public AbstractSidedMachineScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            sideSetters.add(CustomRenderButton.builder(Component.empty(), button -> buttonSideSet(button, finalI), allow).size(9, 9).build());
            addRenderableWidget(sideSetters.get(i));
        }
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            if(i == 2 && menu.getBeInventory() == null) continue;
            if(i == 3 && menu.getBeTank() == null) continue;
            autoSetters.add(CustomRenderButton.builderNoTexture(Component.empty(), button -> buttonAutoSet(button, finalI)).size(9, 9).build());
            addRenderableWidget(autoSetters.get(i));
        }
        if(redstoneControllable){
            for (int i = 0; i < 1; i++) {
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
        super.renderBg(gui, pPartialTick, pMouseX, pMouseY);
        renderSidedTransferTab(gui, sidedTransferBlockEntity);
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float pPartialTick) {
        super.render(gui, mx, my, pPartialTick);
        sidedTransferBarActive = mx >= x+5 && mx <= x+101 && my >= y-29 && my <= y+3;
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
                        if(menu.getBeInventory() != null){
                            autoSetters.get(i).setPosition(sidedX + 47, sidedY + 7);
                            if (auto.get(i) != null && auto.get(i))
                                autoSetters.get(i).setTextures(item_transfer_disabled);
                            else autoSetters.get(i).setTextures(item_transfer);
                        }
                    }
                    case 3 -> {
                        if(menu.getBeTank() != null){
                            autoSetters.get(i).setPosition(sidedX + 47, sidedY + 17);
                            if (auto.get(i) != null && auto.get(i))
                                autoSetters.get(i).setTextures(fluid_transfer_disabled);
                            else autoSetters.get(i).setTextures(fluid_transfer);
                        }
                    }
                }
            }
            if(redstoneControllable){
                redstoneSetters.get(0).setPosition(sidedX + 57, sidedY + 12);
                redstoneSetters.get(0).setTextures(getTextureForRedstoneType(redstone.get(0)));
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

    private ResourceLocation getTextureForRedstoneType(SidedTransferBlockEntity.RedstoneType redstoneType) {
        if(redstoneType == SidedTransferBlockEntity.RedstoneType.IGNORED) return redstone_ignored;
        if(redstoneType == SidedTransferBlockEntity.RedstoneType.HIGH) return redstone_active;
        if(redstoneType == SidedTransferBlockEntity.RedstoneType.LOW) return redstone_passive;
        return redstone_ignored;
    }
}
