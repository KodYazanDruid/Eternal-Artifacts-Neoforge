package com.sonamorningstar.eternalartifacts.network;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record EnderNotebookAddNbtToServer(String name, ResourceKey<Level> dimension, BlockPos position, ItemStack book) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(MODID, "endernotebook_nbt_add");

    public static EnderNotebookAddNbtToServer create(FriendlyByteBuf buf) {
        return new EnderNotebookAddNbtToServer(buf.readUtf(), buf.readResourceKey(Registries.DIMENSION), buf.readBlockPos(), buf.readItem());
    }

    public static EnderNotebookAddNbtToServer create(String name, ResourceKey<Level> dimension, BlockPos position, ItemStack book) {
        return new EnderNotebookAddNbtToServer(name, dimension, position, book);
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeUtf(name);
        pBuffer.writeResourceKey(dimension);
        pBuffer.writeBlockPos(position);
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

                CompoundTag singleWarp = new CompoundTag();
                singleWarp.putString("Name", name);
                singleWarp.putString("Dimension", dimension.location().toString());
                singleWarp.putInt("X", position.getX());
                singleWarp.putInt("Y", position.getY());
                singleWarp.putInt("Z", position.getZ());
                listTag.add(singleWarp);

                CompoundTag tag = book.getOrCreateTag();
                tag.put("Warps", listTag);
            }
        }));
    }
}

