package com.sonamorningstar.eternalartifacts.client.gui.overlay;

import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;

public class ClockOverlay extends ModGuiOverlay{
    public ClockOverlay() {
        super(25, 26);
    }
    
    @Override
    public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = gui.getMinecraft();
        Player player = minecraft.player;
        if (shouldReturn(minecraft)) return;
        ItemStack clock = PlayerCharmManager.findCharm(player, Items.CLOCK);
        if (!clock.isEmpty()) {
            int x = 20;
            int y = 20;
            guiGraphics.renderItem(clock, x, y);
            long time = Minecraft.getInstance().level.dayTime();
            int hours = (int) (time / 1000 + 6) % 24;
            int minutes = (int) (time % 1000) * 60 / 1000;
            //each 1000 is 1 hour
            String timeStr = String.format("%02d", hours) + ":"+String.format("%02d", minutes);
            int strWidth = minecraft.font.width(timeStr);
            setStrWidth(strWidth);
            renderBlankBlack(guiGraphics, x - 3, y - 5, strWidth + 25, 26, 0.5F);
            guiGraphics.drawString(gui.getFont(),
                    timeStr, x + 20, y + 5, 16777215, false);
        }
    }
}
