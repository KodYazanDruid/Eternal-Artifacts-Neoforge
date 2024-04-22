package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

//No it does not help you find a girlfriend.
public class PlayerHelper {
    public static boolean findInStack(Player player, Item item) {
        IItemHandler playerItemCapability = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if(playerItemCapability == null) return false;
        for (int i = 0; i < playerItemCapability.getSlots(); i++) {
            if(playerItemCapability.getStackInSlot(i).getItem() == item) return true;
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
}
