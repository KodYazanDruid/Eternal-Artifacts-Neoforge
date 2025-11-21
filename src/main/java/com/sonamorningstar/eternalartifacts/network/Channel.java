package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.network.charm.CycleWildcardToClient;
import com.sonamorningstar.eternalartifacts.network.charm.UpdateCharmsToClient;
import com.sonamorningstar.eternalartifacts.network.endernotebook.EnderNotebookAddNbtToServer;
import com.sonamorningstar.eternalartifacts.network.endernotebook.OpenItemStackScreenToClient;
import com.sonamorningstar.eternalartifacts.network.endernotebook.EnderNotebookRemoveNbtToServer;
import com.sonamorningstar.eternalartifacts.network.endernotebook.EnderNotebookRenameWarpToServer;
import com.sonamorningstar.eternalartifacts.network.movement.ConsumeDashTokenToServer;
import com.sonamorningstar.eternalartifacts.network.movement.ConsumeJumpTokenToServer;
import com.sonamorningstar.eternalartifacts.network.tesseract.AddTesseractNetworkToServer;
import com.sonamorningstar.eternalartifacts.network.tesseract.RebuildTesseractPanelToClient;
import com.sonamorningstar.eternalartifacts.network.tesseract.TesseractNetworksToClient;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
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

        registrar.play(UpdateFakeSlotToServer.ID,
                UpdateFakeSlotToServer::create,
                handler -> handler.server(UpdateFakeSlotToServer::handle));
        registrar.play(SendStringToServer.ID,
                SendStringToServer::create,
                handler -> handler.server(SendStringToServer::handle));
        
        registrar.play(ItemTagFilterToServer.ID,
                ItemTagFilterToServer::create,
                handler -> handler.server(ItemTagFilterToServer::handle));
        registrar.play(FluidStackFilterToServer.ID,
                FluidStackFilterToServer::create,
                handler -> handler.server(FluidStackFilterToServer::handle));
        registrar.play(FluidTagFilterToServer.ID,
                FluidTagFilterToServer::create,
                handler -> handler.server(FluidTagFilterToServer::handle));

        registrar.play(PlayerTeleportToServer.ID,
                PlayerTeleportToServer::create,
                handler -> handler.server(PlayerTeleportToServer::handle));
        registrar.play(MachineConfigurationToServer.ID,
            MachineConfigurationToServer::create,
            handler -> handler.server(MachineConfigurationToServer::handle));
        registrar.play(ShootSkullsToServer.ID,
                ShootSkullsToServer::create,
                handler -> handler.server(ShootSkullsToServer::handle));
        registrar.play(FluidSlotTransferToServer.ID,
                FluidSlotTransferToServer::create,
                handler -> handler.server(FluidSlotTransferToServer::handle));
        registrar.play(OpenTabMenuToServer.ID,
                OpenTabMenuToServer::create,
                handler -> handler.server(OpenTabMenuToServer::handle));
        registrar.play(ConsumeJumpTokenToServer.ID,
            ConsumeJumpTokenToServer::create,
                handler -> handler.server(ConsumeJumpTokenToServer::handle));
        registrar.play(ConsumeDashTokenToServer.ID,
            ConsumeDashTokenToServer::create,
            handler -> handler.server(ConsumeDashTokenToServer::handle));

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
        registrar.play(AddTesseractNetworkToServer.ID,
                AddTesseractNetworkToServer::create,
                handler -> handler.server(AddTesseractNetworkToServer::handle));
        registrar.play(SelectEntityMessageToServer.ID,
                SelectEntityMessageToServer::create,
                handler -> handler.server(SelectEntityMessageToServer::handle));
        registrar.play(TesseractNetworksToClient.ID,
            TesseractNetworksToClient::create,
            handler -> handler.server(TesseractNetworksToClient::handle));
        
        registrar.play(BlueprintIngredientsToClient.ID,
            BlueprintIngredientsToClient::create,
            handler -> handler.client(BlueprintIngredientsToClient::handle));
        registrar.play(ForcedChunksToClient.ID,
            ForcedChunksToClient::create,
            handler -> handler.client(ForcedChunksToClient::handle));
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
    
    public static <MSG extends CustomPacketPayload> void sendToChunk(MSG message, LevelChunk chunk) {
        PacketDistributor.TRACKING_CHUNK.with(chunk).send(message);
    }
}
