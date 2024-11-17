package com.sonamorningstar.eternalartifacts.capabilities.item;

import com.sonamorningstar.eternalartifacts.core.ModTags;
import lombok.Getter;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PlayerCharmsStorage extends ItemStackHandler {
    private final LivingEntity living;
    public static final Map<Integer, CharmType> slotTypes = new HashMap<>(12);

    public PlayerCharmsStorage(LivingEntity living) {
        super(12);
        this.living = living;
        deserializeNBT(living.getPersistentData().getCompound("Charms"));
    }

    /*public static PlayerCharmsStorage getFromNBT(LivingEntity player) {
        return new PlayerCharmsStorage(player);
    }*/

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slotTypes.containsKey(slot)) return slotTypes.get(slot).test(stack);
        return false;
    }

    @Override
    protected void onContentsChanged(int slot) {
        living.getPersistentData().put("Charms", serializeNBT());
    }

    public enum CharmType {
        HEAD(ModTags.Items.CHARMS_HEAD),
        NECKLACE(ModTags.Items.CHARMS_NECKLACE),
        RING(ModTags.Items.CHARMS_RING),
        BELT(ModTags.Items.CHARMS_BELT),
        BRACELET(ModTags.Items.CHARMS_BRACELET),
        HAND(ModTags.Items.CHARMS_HAND),
        BOOTS(ModTags.Items.CHARMS_BOOTS),
        BACK(ModTags.Items.CHARMS_BACK),
        CHARM(ModTags.Items.CHARMS_CHARM);

        private final TagKey<Item> tag;

        CharmType(TagKey<Item> tag) {
            this.tag = tag;
        }

        public boolean test(ItemStack stack) {
            return stack.is(tag);
        }
    }
}
