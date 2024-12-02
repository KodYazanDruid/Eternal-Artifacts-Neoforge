package com.sonamorningstar.eternalartifacts.api.charm;

import com.sonamorningstar.eternalartifacts.capabilities.item.CharmStorage;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.mixins_interfaces.ICharmProvider;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class PlayerCharmManager {

    public static ItemStack findCharm(Player player, Item charm) {
        CharmStorage charms = player.getData(ModDataAttachments.CHARMS);
        for (int i = 0; i < charms.getSlots(); i++) {
            ItemStack c = charms.getStackInSlot(i);
            if (c.is(charm)) return c;
        }
        return ItemStack.EMPTY;
    }

    public static boolean findCharm(Player player, ItemStack charm) {
        CharmStorage charms = player.getData(ModDataAttachments.CHARMS);
        return charms.containsStack(charm);
    }

    public static ItemStack findCharmWithTag(Player player, Item charm, CompoundTag compound) {
        ItemStack c = findCharm(player, charm);
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
    public static ItemStack findCharmWithTag(Player player, Item charm, Tag tag) {
        ItemStack c = findCharm(player, charm);
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
