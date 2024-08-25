package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.CustomRenderButton;
import com.sonamorningstar.eternalartifacts.container.SmartJukeboxMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.SmartJukeboxBlockEntity;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.SmartJukeboxStopResumeToServer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class SmartJukeboxScreen extends AbstractSidedMachineScreen<SmartJukeboxMenu> {
    private CustomRenderButton stopButton;
    public SmartJukeboxScreen(SmartJukeboxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        stopButton = CustomRenderButton.builder(Component.empty(), this::stopResumeMusic, new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/auto_input.png"))
                .size(9, 9).pos(50, 50).build();
        addRenderableWidget(stopButton);
    }

    private void stopResumeMusic(Button b) {
        Channel.sendToServer(new SmartJukeboxStopResumeToServer(menu.getBlockEntity().getBlockPos()));
    }

    @Override
    protected void renderBg(GuiGraphics gui, float tick, int mx, int my) {
        super.renderBg(gui, tick, mx, my);
        renderDefaultEnergyBar(gui);
        if (menu.getBeInventory() != null) {
            ItemStack stack = menu.getBeInventory().getStackInSlot(0);
            if (stack.getItem() instanceof RecordItem record) {
                SoundEvent sound = record.getSound();
                int length = record.getLengthInTicks();
                gui.drawString(font, Component.literal(sound.getLocation() +": "+length), x + 100, y + 30, labelColor, false);
            }
        }
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
        if (menu.getBeInventory() != null) {
            gui.drawString(font, Component.literal(String.valueOf(((SmartJukeboxBlockEntity) menu.getBlockEntity()).getPlayTick())), x + 100, y + 38, labelColor, false);
        }
    }
}
