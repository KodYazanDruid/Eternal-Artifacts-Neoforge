package com.sonamorningstar.eternalartifacts.client.gui.overlay;

import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;

public class ClockOverlay extends ModGuiOverlay{
    @Override
    public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Player player = gui.getMinecraft().player;
        if (shouldReturn(gui.getMinecraft())) return;
        ItemStack clock = PlayerCharmManager.findCharm(player, Items.CLOCK);
        if (!clock.isEmpty()) {
            int x = 20;
            int y = 20;
            renderBlankBlack(guiGraphics, x - 5, y - 5, 54, 26, 0.5F);
            guiGraphics.renderItem(clock, x, y);
            long time = Minecraft.getInstance().level.dayTime();
            int hours = (int) (time / 1000 + 6) % 24;
            int minutes = (int) (time % 1000) * 60 / 1000;
            //each 1000 is 1 hour
            guiGraphics.drawString(gui.getFont(),
                String.format("%02d", hours) + ":"+String.format("%02d", minutes),
                x + 20, y + 5, 16777215, false);
        }
    }
}
