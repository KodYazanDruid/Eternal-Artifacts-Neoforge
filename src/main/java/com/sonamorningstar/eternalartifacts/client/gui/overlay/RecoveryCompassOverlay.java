package com.sonamorningstar.eternalartifacts.client.gui.overlay;

import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;

import java.util.Optional;

public class RecoveryCompassOverlay extends ModGuiOverlay {
    public RecoveryCompassOverlay() {
        super(25, 26);
    }
    
    @Override
    public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Player player = gui.getMinecraft().player;
        if (shouldReturn(gui.getMinecraft())) return;
        ItemStack compass = PlayerCharmManager.findCharm(player, Items.RECOVERY_COMPASS);
        if (!compass.isEmpty()) {
            int x = 20;
            int y = 20;
            Component loc = getLocation(player);
            int strWidth = getComponentWidth(loc);
            setStrWidth(strWidth);
            renderBlankBlack(guiGraphics, x - 3, y - 5, strWidth + 25, 26, 0.5F);
            guiGraphics.renderItem(compass, x, y);
            guiGraphics.drawString(gui.getFont(), loc, x + 20, y + 5, 16777215, false);
        }
    }

    private static Component getLocation(Player player) {
        Component loc = Component.empty();
        Optional<GlobalPos> deathLoc = player.getLastDeathLocation();
        if (deathLoc.isPresent()) {
            GlobalPos pos = deathLoc.get();
            BlockPos blockPos = pos.pos();
            ResourceKey<Level> dimension = pos.dimension();
            if (dimension != player.level().dimension()) return loc;
            loc = ModConstants.GUI.withSuffixTranslatable("death_location").append(": ").append(blockPos.toShortString());
        }else loc = ModConstants.GUI.withSuffixTranslatable("death_location").append(": ").append(ModConstants.GUI.withSuffixTranslatable("not_found"));
        return loc;
    }
}
