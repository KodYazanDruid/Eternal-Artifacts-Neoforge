package com.sonamorningstar.eternalartifacts.client.gui.overlay;

import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;

public class CompassOverlay extends ModGuiOverlay{
    @Override
    public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Player player = gui.getMinecraft().player;
        if (shouldReturn(gui.getMinecraft())) return;
        ItemStack compass = PlayerCharmManager.findCharm(player, Items.COMPASS);
        if (!compass.isEmpty()) {
            int x = 20;
            int y = 20;
            String loc = getLocation(compass, player);
            renderBlankBlack(guiGraphics, x - 5, y - 5, 26 + loc.length() * 6, 26, 0.5F);
            guiGraphics.renderItem(compass, x, y);
            guiGraphics.drawString(gui.getFont(), loc, x + 20, y + 5, 16777215, false);
        }
    }

    private String getLocation(ItemStack compass, Player player) {
        String loc = "";
        boolean isLodestone = CompassItem.isLodestoneCompass(compass);
        if (isLodestone) {
            GlobalPos pos = CompassItem.getLodestonePosition(compass.getOrCreateTag());
            if (pos != null) {
                BlockPos blockPos = pos.pos();
                ResourceKey<Level> dimension = pos.dimension();
                if (dimension != player.level().dimension()) return loc;
                loc = "Lodestone: " + blockPos.toShortString();
            }
        }else {
            GlobalPos pos = CompassItem.getSpawnPosition(player.level());
            if (pos != null) {
                BlockPos blockPos = pos.pos();
                loc = "Spawn: " + blockPos.toShortString();
            }
        }
        return loc;
    }
}
