package com.sonamorningstar.eternalartifacts.network.endernotebook;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record EnderNotebookRemoveNbtToServer(int index, ItemStack book) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(MODID, "endernotebook_nbt_remove");

    public static EnderNotebookRemoveNbtToServer create(FriendlyByteBuf buf) {
        return new EnderNotebookRemoveNbtToServer(buf.readInt(), buf.readItem());
    }

    public static EnderNotebookRemoveNbtToServer create(int index, ItemStack book) {
        return new EnderNotebookRemoveNbtToServer(index, book);
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(index);
        pBuffer.writeItem(book);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
            if(book.is(ModItems.ENDER_NOTEBOOK)) {
                ListTag listTag =  book.getTag() != null ? book.getTag().getList("Warps", 10) : new ListTag();
                listTag.remove(index);
                CompoundTag tag = book.getOrCreateTag();
                tag.put("Warps", listTag);
            }
        }));
    }
}
