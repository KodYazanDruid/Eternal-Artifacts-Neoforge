package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.sonamorningstar.eternalartifacts.client.gui.widget.SpriteButton;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.SidedTransferAutoSaveToServer;
import com.sonamorningstar.eternalartifacts.network.SidedTransferRedstoneToServer;
import com.sonamorningstar.eternalartifacts.network.SidedTransferSideSaveToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public abstract class AbstractSidedMachineScreen<T extends AbstractMachineMenu> extends AbstractMachineScreen<T> {
    private final SidedTransferMachine<?> sidedTransferMachineBlockEntity = ((SidedTransferMachine<?>) menu.getBlockEntity());

    @Setter
    private boolean redstoneControllable = true;
    private boolean sidedTransferBarActive;
    private final List<SpriteButton> sideSetters = new ArrayList<>(6);
    private final List<SpriteButton> autoSetters = new ArrayList<>(4);
    private final List<SpriteButton> redstoneSetters = new ArrayList<>(1);
    private static final ResourceLocation allow = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/allow.png");
    private static final ResourceLocation deny = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/deny.png");
    private static final ResourceLocation input = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/input.png");
    private static final ResourceLocation output = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/output.png");
    private static final ResourceLocation auto_input = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/auto_input.png");
    private static final ResourceLocation auto_output = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/auto_output.png");
    private static final ResourceLocation auto_input_enabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/auto_input_enabled.png");
    private static final ResourceLocation auto_output_enabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/auto_output_enabled.png");
    private static final ResourceLocation item_transfer = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/item_transfer.png");
    private static final ResourceLocation fluid_transfer = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/fluid_transfer.png");
    private static final ResourceLocation item_transfer_disabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/item_transfer_disabled.png");
    private static final ResourceLocation fluid_transfer_disabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/fluid_transfer_disabled.png");
    private static final ResourceLocation redstone_active = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/redstone_active.png");
    private static final ResourceLocation redstone_passive = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/redstone_passive.png");
    private static final ResourceLocation redstone_ignored = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/redstone_ignored.png");

    public AbstractSidedMachineScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            sideSetters.add(SpriteButton.builder(Component.empty(), (button, key) -> buttonSideSet(button, key, finalI), allow).size(9, 9).build());
            addRenderableWidget(sideSetters.get(i));
        }
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            autoSetters.add(SpriteButton.builderNoTexture(Component.empty(), (button, key) -> buttonAutoSet(button, key, finalI)).size(9, 9).build());
            addRenderableWidget(autoSetters.get(i));
        }
        if(redstoneControllable){
            for (int i = 0; i < 1; i++) {
                int finalI = i;
                redstoneSetters.add(SpriteButton.builderNoTexture(Component.empty(), (button, key) -> buttonRedstoneSet(button, key, finalI)).size(9, 9).build());
                addRenderableWidget(redstoneSetters.get(i));
            }
        }
    }

    private void buttonSideSet(SpriteButton button, int key, int index) {
        SidedTransferMachine.TransferType type;
        switch (key) {
            case 1 -> type = SidedTransferMachine.TransferType.cyclePrev(index, sidedTransferMachineBlockEntity);
            case 2 -> type = SidedTransferMachine.TransferType.DEFAULT;
            default -> type = SidedTransferMachine.TransferType.cycleNext(index, sidedTransferMachineBlockEntity);
        }
        Channel.sendToServer(new SidedTransferSideSaveToServer(index, type, sidedTransferMachineBlockEntity.getBlockPos()));
    }

    private void buttonAutoSet(SpriteButton button, int key, int index) {
        boolean auto = sidedTransferMachineBlockEntity.getAutoConfigs().get(index) != null && sidedTransferMachineBlockEntity.getAutoConfigs().get(index);
        Channel.sendToServer(new SidedTransferAutoSaveToServer(index, !auto, sidedTransferMachineBlockEntity.getBlockPos()));
    }

    private void buttonRedstoneSet(SpriteButton button, int key, int index) {
        SidedTransferMachine.RedstoneType type;
        switch (key) {
            case 1 -> type = SidedTransferMachine.RedstoneType.cyclePrev(index, sidedTransferMachineBlockEntity);
            case 2 -> type = SidedTransferMachine.RedstoneType.IGNORED;
            default -> type = SidedTransferMachine.RedstoneType.cycleNext(index, sidedTransferMachineBlockEntity);
        }
        Channel.sendToServer(new SidedTransferRedstoneToServer(index, type, sidedTransferMachineBlockEntity.getBlockPos()));
    }

    @Override
    protected void renderBg(GuiGraphics gui, float tick, int mx, int my) {
        super.renderBg(gui, tick, mx, my);
        renderSidedTransferTab(gui, sidedTransferMachineBlockEntity);
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
        sidedTransferBarActive = isCursorInBounds(leftPos + 5, topPos - 29, 94, 32, mx, my);
        renderButtonTooltips(gui, mx, my);
    }

    protected void renderButtonTooltips(GuiGraphics guiGraphics, int mx, int my) {
        for (int i = 0; i < sideSetters.size(); i++) {
            if (sideSetters.get(i).visible) {
                Map<Integer, SidedTransferMachine.TransferType> side = sidedTransferMachineBlockEntity.getSideConfigs();
                SpriteButton button = sideSetters.get(i);
                String direction = "";
                switch (i) {
                    case 0 -> direction = "up";
                    case 1 -> direction = "left";
                    case 2 -> direction = "front";
                    case 3 -> direction = "right";
                    case 4 -> direction = "down";
                    case 5 -> direction = "back";
                }
                if(isCursorInBounds(button.getX(), button.getY(), button.getWidth(), button.getHeight(), mx, my)) {
                    guiGraphics.renderTooltip(font,
                        ModConstants.GUI.withSuffixTranslatable(direction)
                            .append(": ").append(ModConstants.GUI.withSuffixTranslatable(ensureType(side.get(i)))),
                        mx, my);
                }
            }
        }
        for (int i = 0; i < autoSetters.size(); i++) {
            if (autoSetters.get(i).visible) {
                Map<Integer, Boolean> auto = sidedTransferMachineBlockEntity.getAutoConfigs();
                SpriteButton button = autoSetters.get(i);
                boolean value = auto.get(i) != null && auto.get(i);
                boolean isAuto = i == 0 || i == 1;
                String type = "";
                switch (i) {
                    case 0 -> type = "auto_input";
                    case 1 -> type = "auto_output";
                    case 2 -> type = "item_transportation";
                    case 3 -> type = "fluid_transportation";
                }
                if(isCursorInBounds(button.getX(), button.getY(), button.getWidth(), button.getHeight(), mx, my)){
                    guiGraphics.renderTooltip(font,
                        ModConstants.GUI.withSuffixTranslatable(type)
                            .append(": ").append(ModConstants.GUI.withSuffixTranslatable(isAuto ? value ? "enabled" : "disabled" : value ? "disabled" : "enabled")),
                        mx, my);
                }
            }
        }
        for (int i = 0; i < redstoneSetters.size(); i++) {
            if (redstoneSetters.get(i).visible) {
                Map<Integer, SidedTransferMachine.RedstoneType> redstone = sidedTransferMachineBlockEntity.getRedstoneConfigs();
                SpriteButton button = redstoneSetters.get(i);
                if(isCursorInBounds(button.getX(), button.getY(), button.getWidth(), button.getHeight(), mx, my)){
                    guiGraphics.renderTooltip(font,
                            ModConstants.GUI.withSuffixTranslatable("redstone")
                                    .append(": ").append(getComponentForRedstone(redstone.get(i))),
                            mx, my);
                }
            }
        }
    }

    private String ensureType(SidedTransferMachine.TransferType type) {
        return type == null ? "default" : type.toString().toLowerCase(Locale.ENGLISH);
    }

    private MutableComponent getComponentForRedstone(SidedTransferMachine.RedstoneType type) {
        if (type == SidedTransferMachine.RedstoneType.LOW) return ModConstants.GUI.withSuffixTranslatable("redstone_passive");
        if (type == SidedTransferMachine.RedstoneType.HIGH) return ModConstants.GUI.withSuffixTranslatable("redstone_active");
        return ModConstants.GUI.withSuffixTranslatable("redstone_default");
    }

    protected void renderSidedTransferTab(GuiGraphics guiGraphics, SidedTransferMachine<?> sided) {
        int sidedX = leftPos + 5;
        int sidedY = topPos - 29;
        Map<Integer, SidedTransferMachine.TransferType> side = sided.getSideConfigs();
        Map<Integer, Boolean> auto = sided.getAutoConfigs();
        Map<Integer, SidedTransferMachine.RedstoneType> redstone = sided.getRedstoneConfigs();
        sideSetters.forEach(button -> button.visible = sidedTransferBarActive);
        autoSetters.forEach(button -> button.visible = sidedTransferBarActive);
        for(int i = 0 ; i < autoSetters.size(); i++) {
            if(i == 2) {
                autoSetters.get(i).visible = sidedTransferBarActive && menu.getBeInventory() != null;
            }
            if(i == 3) {
                autoSetters.get(i).visible = sidedTransferBarActive && menu.getBeTank() != null;
            }
        }
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
                        autoSetters.get(i).visible = true;
                        autoSetters.get(i).setPosition(sidedX + 37, sidedY + 7);
                        if(auto.get(i) != null && auto.get(i)) autoSetters.get(i).setTextures(auto_input_enabled);
                        else autoSetters.get(i).setTextures(auto_input);
                    }
                    case 1 -> {
                        autoSetters.get(i).visible = true;
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

    private ResourceLocation getTextureForTransferType(SidedTransferMachine.TransferType transferType) {
        if(transferType == SidedTransferMachine.TransferType.NONE) return deny;
        if(transferType == SidedTransferMachine.TransferType.PULL) return input;
        if(transferType == SidedTransferMachine.TransferType.PUSH) return output;
        return allow;
    }

    private ResourceLocation getTextureForRedstoneType(SidedTransferMachine.RedstoneType redstoneType) {
        if(redstoneType == SidedTransferMachine.RedstoneType.IGNORED) return redstone_ignored;
        if(redstoneType == SidedTransferMachine.RedstoneType.HIGH) return redstone_active;
        if(redstoneType == SidedTransferMachine.RedstoneType.LOW) return redstone_passive;
        return redstone_ignored;
    }
}
