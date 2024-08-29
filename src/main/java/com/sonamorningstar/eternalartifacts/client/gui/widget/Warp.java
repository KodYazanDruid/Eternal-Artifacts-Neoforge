package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.EnderNotebookTeleportToServer;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

@Getter
public class Warp {
    private final String label;
    private final ResourceKey<Level> dimension;
    private final BlockPos position;

    private final CustomRenderButton teleportButton;
    private final CustomRenderButton deleteButton;
    public volatile BiConsumer<CustomRenderButton, Integer> deleteLogic = (a, b) -> {};

    public Warp(String label, ResourceKey<Level> dimension, BlockPos position) {
        this.label = label;
        this.dimension = dimension;
        this.position = position;

        this.teleportButton = CustomRenderButton.builder(Component.empty(), this::onTeleportPress).build();
        this.deleteButton = CustomRenderButton.builder(Component.empty(), this::delete).build();
    }

    private void onTeleportPress(CustomRenderButton button, int key){
        Channel.sendToServer(new EnderNotebookTeleportToServer(dimension, position));
    }

    private void delete(CustomRenderButton button, int key) {
        deleteLogic.accept(button, key);
    }

}
