package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.AnvilinatorMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.AnvilinatorBlockEntity;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.PacketAnvilatorSwitchToServer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class AnvilinatorScreen extends AbstractSidedMachineScreen<AnvilinatorMenu> {
    //private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/anvilator.png");
    //private static final ResourceLocation BARS = new ResourceLocation(MODID, "textures/gui/bars.png");
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
    protected void renderBg(GuiGraphics gui, float pPartialTick, int mx, int my) {
        super.renderBg(gui, pPartialTick, mx, my);
        renderDefaultEnergyAndFluidBar(gui);
        renderProgressArrow(gui, leftPos + 122, topPos + 53, mx, my);
    }

    // For rendering widgets.
    private void renderFg(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.name.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
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
