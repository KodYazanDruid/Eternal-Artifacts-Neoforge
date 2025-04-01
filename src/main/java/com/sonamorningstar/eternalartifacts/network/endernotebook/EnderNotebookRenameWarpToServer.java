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

public record EnderNotebookRenameWarpToServer(int index, ItemStack book, String rename) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(MODID, "endernotebook_warp_rename");

    public static EnderNotebookRenameWarpToServer create(FriendlyByteBuf buf) {
        return new EnderNotebookRenameWarpToServer(buf.readInt(), buf.readItem(), buf.readUtf());
    }

    public static EnderNotebookRenameWarpToServer create(int index, ItemStack book, String rename) {
        return new EnderNotebookRenameWarpToServer(index, book, rename);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(index);
        buffer.writeItem(book);
        buffer.writeUtf(rename);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
            if(book.is(ModItems.ENDER_NOTEBOOK)) {
                ListTag listTag =  book.getTag() != null ? book.getTag().getList("Warps", 10) : new ListTag();
                CompoundTag warpTag = listTag.getCompound(index);
                warpTag.putString("Name", rename);
                CompoundTag tag = book.getOrCreateTag();
                tag.put("Warps", listTag);
            }
        }));
    }
}
