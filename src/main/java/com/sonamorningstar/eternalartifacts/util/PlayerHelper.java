package com.sonamorningstar.eternalartifacts.util;

import com.sonamorningstar.eternalartifacts.util.collections.ListIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.*;
import java.util.stream.Collectors;

//No it does not help you find a girlfriend.
public class PlayerHelper {
    public static ItemStack findItem(Player player, Item item) {
        IItemHandler playerItemCapability = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if(playerItemCapability == null) return ItemStack.EMPTY;
        for (int i = 0; i < playerItemCapability.getSlots(); i++) {
            if(playerItemCapability.getStackInSlot(i).getItem() == item) return playerItemCapability.getStackInSlot(i);
        }
        return ItemStack.EMPTY;
    }

    public static boolean findStack(Player player, ItemStack stack) {
        return player.getInventory().contains(stack);
    }
    public static ItemStack findItemWithTag(Player player, Item item, CompoundTag tag) {
        IItemHandler playerItemCapability = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if(playerItemCapability == null) return ItemStack.EMPTY;
        for(int i = 0; i < playerItemCapability.getSlots(); i++) {
            if(playerItemCapability.getStackInSlot(i).getItem() == item) {
                ItemStack found = playerItemCapability.getStackInSlot(i);
                CompoundTag foundTag = found.getOrCreateTag();
                for(String key : foundTag.getAllKeys()) {
                    if(found.hasTag() && foundTag.contains(key) && Objects.equals(foundTag.get(key), tag.get(key))) {
                        return found;
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }
    public static ItemStack findItemWithTag(Player player, Item item, Tag tag) {
        IItemHandler playerItemCapability = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if(playerItemCapability == null) return ItemStack.EMPTY;
        for(int i = 0; i < playerItemCapability.getSlots(); i++) {
            if(playerItemCapability.getStackInSlot(i).getItem() == item) {
                ItemStack found = playerItemCapability.getStackInSlot(i);
                CompoundTag foundTag = found.getOrCreateTag();
                for(String key : foundTag.getAllKeys()) {
                    if(found.hasTag() && foundTag.contains(key) && Objects.equals(foundTag.get(key), tag)) {
                        return found;
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }
    public static ItemStack findItemWithClass(Player player, Class<? extends Item> itemClass) {
        IItemHandler playerItemCapability = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if(playerItemCapability == null) return ItemStack.EMPTY;
        for (int i = 0; i < playerItemCapability.getSlots(); i++) {
            ItemStack stack = playerItemCapability.getStackInSlot(i);
            if(itemClass.isInstance(stack.getItem())) return stack;
        }
        return ItemStack.EMPTY;
    }
    public static ListIterator<ItemStack> itemWithClassIterable(Player player, Class<? extends Item> itemClass) {
        return new ListIterator<>(player.getInventory().items.stream()
                .filter(stack -> itemClass.isInstance(stack.getItem()))
                .collect(Collectors.toList())
        );
    }

    public static void teleportToDimension(ServerPlayer player, ServerLevel level, Vec3 targetVec) {
        level.getChunk(new BlockPos((int)targetVec.x, (int)targetVec.y, (int)targetVec.z));
        player.teleportTo(level, targetVec.x(), targetVec.y(), targetVec.z(), player.getYRot(), player.getXRot());
    }

    public static void giveItemOrPop(Player player, ItemStack stack) {
        if (!player.addItem(stack)) {
            popStackInLevel(player.level(), player.getX(), player.getY(), player.getZ(), stack);
        }
    }
    public static void giveItemOrPop(Player player, ItemStack stack, double x, double y, double z) {
        if (!player.addItem(stack)) {
            popStackInLevel(player.level(), x, y, z, stack);
        }
    }
    private static void popStackInLevel(Level level, double x, double y, double z, ItemStack stack) {
        if(stack.isEmpty()) return;
        double d0 = (double) EntityType.ITEM.getHeight() / 2.0;
        double d1 = x + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25);
        double d2 = y + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25) - d0;
        double d3 = z + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25);
        ItemEntity itemEntity = new ItemEntity(level, d1, d2, d3, stack);
        level.addFreshEntity(itemEntity);
    }

    public static Direction getFacingDirection(Player player) {
        //float yaw = player.getYHeadRot();
        //float yaw0 = player.getYRot();
        float pitch = player.getXRot();
        //System.out.println("Yaw: " + yaw + "YRot: " + yaw0 + " Pitch: " + pitch);
        if(pitch > 45) return Direction.DOWN;
        if(pitch < -45) return Direction.UP;
        return player.getDirection();
    }
}
