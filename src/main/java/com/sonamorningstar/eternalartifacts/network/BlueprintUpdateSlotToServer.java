package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.BlueprintMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record BlueprintUpdateSlotToServer(int containerId, int index, ItemStack slotItem) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "blueprint_update_slot_to_server");

    public static BlueprintUpdateSlotToServer create(FriendlyByteBuf buf) {
        return new BlueprintUpdateSlotToServer(buf.readByte(), buf.readVarInt(), buf.readItem());
    }

    public static BlueprintUpdateSlotToServer create(int id, int index, ItemStack blueprint) {
        return new BlueprintUpdateSlotToServer(id, index, blueprint);
    }

    @Override
    public void write(FriendlyByteBuf buff) {
        buff.writeByte(containerId);
        buff.writeVarInt(index);
        buff.writeItem(slotItem);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
            AbstractContainerMenu menu = player.containerMenu;
            if (menu.containerId == containerId && menu instanceof BlueprintMenu blueprintMenu) {
                blueprintMenu.getFakeItems().setItem(index, slotItem);
            }
        }));
    }
}
