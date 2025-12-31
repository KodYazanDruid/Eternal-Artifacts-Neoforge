package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.content.item.PortableBatteryItem;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record SwitchBatteryChargeToServer(Item item) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "switch_charge");

    public static SwitchBatteryChargeToServer create(FriendlyByteBuf buff) {
        return new SwitchBatteryChargeToServer(BuiltInRegistries.ITEM.byId(buff.readVarInt()));
    }
    public static SwitchBatteryChargeToServer create(Item item) { return new SwitchBatteryChargeToServer(item); }

    @Override
    public void write(FriendlyByteBuf buff) {
        buff.writeVarInt(Item.getId(item));
    }

    @Override
    public ResourceLocation id() {return ID;}

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
            if (item instanceof PortableBatteryItem battery) {
                ItemStack stack = CharmManager.findCharm(player, battery);
                battery.switchCharge(stack, player.level(), player.blockPosition());
            }
        }));
    }
}
