package com.sonamorningstar.eternalartifacts.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;

public record OpenMenuToServer(Function<Void, Player> declare) {

    /*public static OpenMenuToServer create(FriendlyByteBuf buf) {
        return new OpenMenuToServer(buf.read)
    }*/
}
