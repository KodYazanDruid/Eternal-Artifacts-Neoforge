package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Objects;

//No it does not help you find a girlfriend.
public class PlayerHelper {
    public static boolean findItem(Player player, Item item) {
        IItemHandler playerItemCapability = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if(playerItemCapability == null) return false;
        for (int i = 0; i < playerItemCapability.getSlots(); i++) {
            if(playerItemCapability.getStackInSlot(i).getItem() == item) return true;
        }
        return false;
    }

    public static boolean findStack(Player player, ItemStack stack) {
        IItemHandler playerItemCapability = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if(playerItemCapability == null) return false;
        for (int i = 0; i < playerItemCapability.getSlots(); i++) {
            if(Objects.equals(playerItemCapability.getStackInSlot(i), stack)) return true;
        }
        return false;
    }

    public static boolean findInStackWithTag(Player player, Item item, CompoundTag tag) {
        IItemHandler playerItemCapability = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if(playerItemCapability == null) return false;
        for(int i = 0; i < playerItemCapability.getSlots(); i++) {
            if(playerItemCapability.getStackInSlot(i).getItem() == item) {
                ItemStack found = playerItemCapability.getStackInSlot(i);
                CompoundTag foundTag = found.getTag();
                for(String key : foundTag.getAllKeys()) {
                    if(found.hasTag() && foundTag.contains(key) && foundTag.get(key).equals(tag.get(key))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void teleportToDimension(ServerPlayer player, ServerLevel level, Vec3 targetVec) {
        level.getChunk(new BlockPos((int)targetVec.x, (int)targetVec.y, (int)targetVec.z));
        player.teleportTo(level, targetVec.x(), targetVec.y(), targetVec.z(), player.getYRot(), player.getXRot());
    }
}
