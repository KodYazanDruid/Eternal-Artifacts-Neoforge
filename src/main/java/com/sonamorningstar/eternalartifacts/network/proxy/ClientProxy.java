package com.sonamorningstar.eternalartifacts.network.proxy;

import com.sonamorningstar.eternalartifacts.client.gui.screen.EnderNotebookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

public class ClientProxy {

    public static void openEnderNotebook(ItemStack notebook) {
        openScreen(new EnderNotebookScreen(notebook));
    }

    public static void openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }
}
