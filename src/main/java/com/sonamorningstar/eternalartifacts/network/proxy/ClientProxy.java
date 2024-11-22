package com.sonamorningstar.eternalartifacts.network.proxy;

import com.sonamorningstar.eternalartifacts.client.gui.screen.EnderNotebookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ClientProxy {

    static Minecraft mc = Minecraft.getInstance();

    public static void openEnderNotebook(ItemStack notebook) {
        openScreen(new EnderNotebookScreen(notebook));
    }

    public static void openScreen(Screen screen) {
        mc.setScreen(screen);
    }

    @Nullable
    public static Entity getPlayerFromId(int id) {
        ClientLevel level = mc.level;
        return level != null ? level.getEntity(id) : null;
    }
}
