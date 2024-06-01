package com.sonamorningstar.eternalartifacts.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class Channel {

    public static void onRegisterPayloadHandler(RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(MODID)
                .versioned("1.0")
                .optional();
        registrar.play(PacketAnvilatorSwitchToServer.ID,
                PacketAnvilatorSwitchToServer::create, handler -> handler.server(PacketAnvilatorSwitchToServer::handle));

        registrar.play(ItemActivationToClient.ID,
                ItemActivationToClient::create, handler -> handler.client(ItemActivationToClient::handle));

        registrar.play(EnderNotebookAddNbtToServer.ID,
                EnderNotebookAddNbtToServer::create, handler -> handler.server(EnderNotebookAddNbtToServer::handle));
        registrar.play(EnderNotebookRemoveNbtToServer.ID,
                EnderNotebookRemoveNbtToServer::create, handler -> handler.server(EnderNotebookRemoveNbtToServer::handle));
        registrar.play(EnderNotebookOpenToClient.ID,
                EnderNotebookOpenToClient::create, handler -> handler.client(EnderNotebookOpenToClient::handle));
        registrar.play(EnderNotebookTeleportToServer.ID,
                EnderNotebookTeleportToServer::create, handler -> handler.server(EnderNotebookTeleportToServer::handle));
        registrar.play(SidedTransferSideSaveToServer.ID,
                SidedTransferSideSaveToServer::create, handler -> handler.server(SidedTransferSideSaveToServer::handle));
        registrar.play(SidedTransferAutoSaveToServer.ID,
                SidedTransferAutoSaveToServer::create, handler -> handler.server(SidedTransferAutoSaveToServer::handle));
        registrar.play(SidedTransferRedstoneToServer.ID,
                SidedTransferRedstoneToServer::create, handler -> handler.server(SidedTransferRedstoneToServer::handle));
    }

    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.SERVER.noArg().send(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToPlayer(MSG message, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(message);
    }
}
