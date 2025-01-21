package com.sonamorningstar.eternalartifacts.mixin_helper;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This class handles the rendering overrides for items with custom render layers.
 * It ensures that these items won't be rendered as normal items in the head or cancels the armor rendering of other parts.
 */
public class RenderOverrides {
    private static final Map<EquipmentSlot, Set<Predicate<ItemStack>>> PREDICATES_TO_SKIP = new HashMap<>();
    private static final Map<EquipmentSlot, Set<Item>> ITEMS_TO_SKIP = new HashMap<>();
    private static final Map<EquipmentSlot, Set<TagKey<Item>>> ITEM_TAGS_TO_SKIP = new HashMap<>();

    public static boolean shouldRender(EquipmentSlot slot, ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        if (itemStack.getItem() instanceof Equipable) return true;

        Set<Item> itemsSet = ITEMS_TO_SKIP.get(slot);
        if (itemsSet != null) {
            for (Item item : itemsSet) {
                if (itemStack.is(item)) {
                    return false;
                }
            }
        }

        Set<TagKey<Item>> tagsSet = ITEM_TAGS_TO_SKIP.get(slot);
        if (tagsSet != null) {
            for (TagKey<Item> tagKey : tagsSet) {
                if (itemStack.is(tagKey)) {
                    return false;
                }
            }
        }

        Set<Predicate<ItemStack>> predicatesSet = PREDICATES_TO_SKIP.get(slot);
        if (predicatesSet != null) {
            for (Predicate<ItemStack> predicate : predicatesSet) {
                if (predicate.test(itemStack)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void addSkipRender(EquipmentSlot slot, Item item) {
        Set<Item> set = ITEMS_TO_SKIP.get(slot);
        if (set != null) set.add(item);
        else ITEMS_TO_SKIP.put(slot, new HashSet<>(Set.of(item)));
    }
    public static void addSkipRender(EquipmentSlot slot, TagKey<Item> tagKey) {
        Set<TagKey<Item>> set = ITEM_TAGS_TO_SKIP.get(slot);
        if (set != null) set.add(tagKey);
        else ITEM_TAGS_TO_SKIP.put(slot, new HashSet<>(Set.of(tagKey)));
    }
    public static void addSkipRender(EquipmentSlot slot, Predicate<ItemStack> predicate) {
        Set<Predicate<ItemStack>> set = PREDICATES_TO_SKIP.get(slot);
        if (set != null) set.add(predicate);
        else PREDICATES_TO_SKIP.put(slot, new HashSet<>(Set.of(predicate)));
    }
}
