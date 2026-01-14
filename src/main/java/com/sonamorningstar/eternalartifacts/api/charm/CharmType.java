package com.sonamorningstar.eternalartifacts.api.charm;

import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public enum CharmType {
    HEAD(ModTags.Items.CHARMS_HEAD),
    NECKLACE(ModTags.Items.CHARMS_NECKLACE),
    RING(ModTags.Items.CHARMS_RING),
    BELT(ModTags.Items.CHARMS_BELT),
    BRACELET(ModTags.Items.CHARMS_BRACELET),
    HAND(ModTags.Items.CHARMS_HAND),
    FEET(ModTags.Items.CHARM_FEET),
    BACK(ModTags.Items.CHARMS_BACK),
    CHARM(ModTags.Items.CHARMS_CHARM);

    public static final Map<Item, List<CharmType>> itemCharmTypes = new HashMap<>();
    public static final String CHARM_KEY = "StackCharmType";

    private final TagKey<Item> tag;

    CharmType(TagKey<Item> tag) {
        this.tag = tag;
    }
    
    public boolean test(ItemStack stack) {
        return stack.is(tag);
    }
    public boolean test(Item item) { return item.builtInRegistryHolder().is(tag);}

    public String getLowerCaseName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static List<CharmType> getTypesOfItem(Item item) {
        if (itemCharmTypes.containsKey(item)) return itemCharmTypes.get(item);

        List<CharmType> types = new ArrayList<>();
        for (CharmType type : values()) {
            if (type.test(item)) {
                types.add(type);
                itemCharmTypes.put(item, types);
            }
        }
        return types;
    }

    public MutableComponent getDisplayName() {
        return ModConstants.CHARM_TYPE.withSuffixTranslatable(getLowerCaseName());
    }
    
    public static MutableComponent getWildcardDisplayName() {
        return ModConstants.CHARM_TYPE.withSuffixTranslatable("wildcard");
    }
}