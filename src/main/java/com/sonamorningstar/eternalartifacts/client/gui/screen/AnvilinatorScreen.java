package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.AnvilinatorMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.Anvilinator;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.PacketAnvilatorSwitchToServer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class AnvilinatorScreen extends AbstractSidedMachineScreen<AnvilinatorMenu> {
    private static final ResourceLocation enabled = new ResourceLocation("container/anvil/text_field");
    private static final ResourceLocation disabled = new ResourceLocation("container/anvil/text_field_disabled");
    private EditBox name;
    private final Anvilinator anvilinatorBlockEntity;

    public AnvilinatorScreen(AnvilinatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        inventoryLabelX = 46;
        this.anvilinatorBlockEntity = (Anvilinator) menu.getBlockEntity();
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
        addRenderableWidget(this.name);

        addWidget(Button.builder(Component.empty(), this::setNameSwitch).bounds(leftPos + 70, topPos + 21, 88, 19).build());
    }

    private void setNameSwitch(Button button) {
        boolean invertedValue = !anvilinatorBlockEntity.getEnableNaming();
        name.setEditable(anvilinatorBlockEntity.getEnableNaming());

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

    // Returns false when typing on the box.
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        //TODO: Use Open/Close inventory key instead of hardcoding E.
        if(name.isFocused() && pKeyCode == 69) return false;
        else return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void render(GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(gui, pMouseX, pMouseY, pPartialTick);
        boolean flag = anvilinatorBlockEntity.getEnableNaming();
        gui.blitSprite(flag ? enabled : disabled, leftPos + 67, topPos + 17, 0, 110, 16);
        int color;
        String key;
        if(anvilinatorBlockEntity.getEnableNaming()) {
            color = 0x187718;
            key = "key." + MODID + ".anvilinator.enabled_naming";
        } else {
            key = "key." + MODID + ".anvilinator.disabled_naming";
            color = 0x4e0523;
        }
         gui.drawString(font, Component.translatable(key), leftPos + 76, topPos + 38, color, false);

        renderTooltip(gui, pMouseX, pMouseY);
    }

}
