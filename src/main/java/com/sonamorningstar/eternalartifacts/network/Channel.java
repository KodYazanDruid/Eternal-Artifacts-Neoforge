package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.network.charm.CycleWildcardToClient;
import com.sonamorningstar.eternalartifacts.network.charm.UpdateCharmsToClient;
import com.sonamorningstar.eternalartifacts.network.endernotebook.EnderNotebookAddNbtToServer;
import com.sonamorningstar.eternalartifacts.network.endernotebook.OpenItemStackScreenToClient;
import com.sonamorningstar.eternalartifacts.network.endernotebook.EnderNotebookRemoveNbtToServer;
import com.sonamorningstar.eternalartifacts.network.endernotebook.EnderNotebookRenameWarpToServer;
import com.sonamorningstar.eternalartifacts.network.movement.ConsumeDashTokenToServer;
import com.sonamorningstar.eternalartifacts.network.movement.ConsumeJumpTokenToServer;
import com.sonamorningstar.eternalartifacts.network.protocol.BlockEntityButtonPress;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class Channel {

    public static void onRegisterPayloadHandler(RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(MODID)
                .versioned("1.0");

        registrar.play(PacketAnvilatorSwitchToServer.ID,
                PacketAnvilatorSwitchToServer::create,
                handler -> handler.server(PacketAnvilatorSwitchToServer::handle));
        registrar.play(ItemActivationToClient.ID,
                ItemActivationToClient::create,
                handler -> handler.client(ItemActivationToClient::handle));
        registrar.play(BlockPlaceOnClient.ID,
                BlockPlaceOnClient::create,
                handler -> handler.client(BlockPlaceOnClient::handle));

        registrar.play(EnderNotebookAddNbtToServer.ID,
                EnderNotebookAddNbtToServer::create,
                handler -> handler.server(EnderNotebookAddNbtToServer::handle));
        registrar.play(EnderNotebookRemoveNbtToServer.ID,
                EnderNotebookRemoveNbtToServer::create,
                handler -> handler.server(EnderNotebookRemoveNbtToServer::handle));
        registrar.play(OpenItemStackScreenToClient.ID,
                OpenItemStackScreenToClient::create,
                handler -> handler.client(OpenItemStackScreenToClient::handle));
        registrar.play(EnderNotebookRenameWarpToServer.ID,
                EnderNotebookRenameWarpToServer::create,
                handler -> handler.server(EnderNotebookRenameWarpToServer::handle));
        registrar.play(SwitchBatteryChargeToServer.ID,
                SwitchBatteryChargeToServer::create,
                handler -> handler.server(SwitchBatteryChargeToServer::handle));

        registrar.play(BlueprintUpdateSlotToServer.ID,
                BlueprintUpdateSlotToServer::create,
                handler -> handler.server(BlueprintUpdateSlotToServer::handle));

        registrar.play(PlayerTeleportToServer.ID,
                PlayerTeleportToServer::create,
                handler -> handler.server(PlayerTeleportToServer::handle));
        registrar.play(SidedTransferSideSaveToServer.ID,
                SidedTransferSideSaveToServer::create,
                handler -> handler.server(SidedTransferSideSaveToServer::handle));
        registrar.play(SidedTransferAutoSaveToServer.ID,
                SidedTransferAutoSaveToServer::create,
                handler -> handler.server(SidedTransferAutoSaveToServer::handle));
        registrar.play(SidedTransferRedstoneToServer.ID,
                SidedTransferRedstoneToServer::create,
                handler -> handler.server(SidedTransferRedstoneToServer::handle));
        registrar.play(ShootSkullsToServer.ID,
                ShootSkullsToServer::create,
                handler -> handler.server(ShootSkullsToServer::handle));
        registrar.play(TankKnapsackTransferToServer.ID,
                TankKnapsackTransferToServer::create,
                handler -> handler.server(TankKnapsackTransferToServer::handle));
        registrar.play(OpenMenuToServer.ID,
                OpenMenuToServer::create,
                handler -> handler.server(OpenMenuToServer::handle));
        registrar.play(ConsumeJumpTokenToServer.ID,
            ConsumeJumpTokenToServer::create,
                handler -> handler.server(ConsumeJumpTokenToServer::handle));
        registrar.play(ConsumeDashTokenToServer.ID,
            ConsumeDashTokenToServer::create,
            handler -> handler.server(ConsumeDashTokenToServer::handle));

        registrar.play(BlockEntityButtonPress.ID,
                BlockEntityButtonPress::create,
                handler -> handler.server(BlockEntityButtonPress::handle));

        registrar.play(UpdateCharmsToClient.ID,
                UpdateCharmsToClient::create,
                handler -> handler.client(UpdateCharmsToClient::handle));
        registrar.play(CycleWildcardToClient.ID,
                CycleWildcardToClient::create,
                handler -> handler.client(CycleWildcardToClient::handle));
        registrar.play(UpdateEntityEnergyToClient.ID,
                UpdateEntityEnergyToClient::create,
                handler -> handler.client(UpdateEntityEnergyToClient::handle));
        registrar.play(SavePlayerDataToClient.ID,
                SavePlayerDataToClient::create,
                handler -> handler.client(SavePlayerDataToClient::handle));
        registrar.play(RebuildTesseractPanelToClient.ID,
            RebuildTesseractPanelToClient::create,
            handler -> handler.client(RebuildTesseractPanelToClient::handle));
    }

    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.SERVER.noArg().send(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToPlayer(MSG message, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToAll(MSG message) {
        PacketDistributor.ALL.noArg().send(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToSelfAndTracking(MSG message, Entity tracking) {
        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(tracking).send(message);
    }
    
    public static <MSG extends CustomPacketPayload> void sendToAllTracking(MSG message, Entity tracked) {
        PacketDistributor.TRACKING_ENTITY.with(tracked).send(message);
    }
}
