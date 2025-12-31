package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.network.base.PacketRegistrar;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class Channel {
    
    // Artık manuel kayıt gerekmiyor!
    // @RegisterPacket annotation'ı olan tüm paketler otomatik olarak taranıp kaydedilir.
    // Sadece annotation ekleyin:
    //
    // @RegisterPacket(side = PacketSide.SERVER)
    // public record MyPacket(...) implements CustomPacketPayload { ... }

    public static void onRegisterPayloadHandler(RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(MODID).versioned("1.0");
        
        // Tüm paketleri otomatik kaydet (@RegisterPacket annotation'ı olanlar)
        PacketRegistrar.registerAll(registrar);
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
