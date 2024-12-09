package com.sonamorningstar.eternalartifacts.api.charm;

import com.sonamorningstar.eternalartifacts.capabilities.item.CharmStorage;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Predicate;

public class PlayerCharmManager {

    public static ItemStack findCharm(LivingEntity living, Item charm) {
        CharmStorage charms = living.getData(ModDataAttachments.CHARMS);
        for (int i = 0; i < charms.getSlots(); i++) {
            ItemStack c = charms.getStackInSlot(i);
            if (c.is(charm)) return c;
        }
        return ItemStack.EMPTY;
    }
    public static boolean findCharm(LivingEntity living, ItemStack charm) {
        CharmStorage charms = living.getData(ModDataAttachments.CHARMS);
        return charms.containsStack(charm);
    }
    public static ItemStack findCharm(LivingEntity living, CharmStorage.CharmType type) {
        CharmStorage charms = living.getData(ModDataAttachments.CHARMS);
        for (int i = 0; i < charms.getSlots(); i++) {
            ItemStack c = charms.getStackInSlot(i);
            if (type.test(c)) return c;
        }
        return ItemStack.EMPTY;
    }
    public static ItemStack findCharm(LivingEntity living, Predicate<ItemStack>  predicate) {
        CharmStorage charms = living.getData(ModDataAttachments.CHARMS);
        for (int i = 0; i < charms.getSlots(); i++) {
            ItemStack c = charms.getStackInSlot(i);
            if (predicate.test(c)) return c;
        }
        return ItemStack.EMPTY;
    }
    public static ItemStack findCharmWithTag(LivingEntity living, Item charm, CompoundTag compound) {
        ItemStack c = findCharm(living, charm);
        if (!c.isEmpty()) {
            CompoundTag charmTags = c.getTag();
            if (charmTags != null) {
                for(String key : charmTags.getAllKeys()) {
                    if(compound.contains(key) && Objects.equals(charmTags.get(key), compound.get(key))) {
                        return c;
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }
    public static ItemStack findCharmWithTag(LivingEntity living, Item charm, Tag tag) {
        ItemStack c = findCharm(living, charm);
        if (!c.isEmpty()) {
            CompoundTag charmTags = c.getTag();
            if (charmTags != null) {
                for(String key : charmTags.getAllKeys()) {
                    Tag charmTag = charmTags.get(key);
                    if(Objects.equals(charmTag, tag)) return c;
                }
            }
        }
        return ItemStack.EMPTY;
    }
    public static ItemStack findInPlayer(Player player, Item item) {
        ItemStack charm = findCharm(player, item);
        return charm.isEmpty() ? PlayerHelper.findItem(player, item) : charm;
    }
    public static boolean findInPlayer(Player player, ItemStack stack) {
        return findCharm(player, stack) || PlayerHelper.findStack(player, stack);
    }
    public static ItemStack findInPlayerWithTag(Player player, Item item, CompoundTag tag) {
        ItemStack charm = findCharmWithTag(player, item, tag);
        return charm.isEmpty() ? PlayerHelper.findItemWithTag(player, item, tag) : charm;
    }
    public static ItemStack findInPlayerWithTag(Player player, Item item, Tag tag) {
        ItemStack charm = findCharmWithTag(player, item, tag);
        return charm.isEmpty() ? PlayerHelper.findItemWithTag(player, item, tag) : charm;
    }
}
