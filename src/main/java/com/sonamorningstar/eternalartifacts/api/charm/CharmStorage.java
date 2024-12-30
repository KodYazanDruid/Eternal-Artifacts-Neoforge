package com.sonamorningstar.eternalartifacts.api.charm;

import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.charm.UpdateCharmsToClient;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class CharmStorage extends ItemStackHandler {
    private final List<Consumer<Integer>> listeners = new ArrayList<>();
    private final LivingEntity owner;
    public static final Map<Integer, CharmType> slotTypes = new HashMap<>(12);

    public CharmStorage(IAttachmentHolder holder) {
        super(12);
        this.owner = (LivingEntity) holder;
    }

    public static CharmStorage get(LivingEntity living) {
        return living.getData(ModDataAttachments.CHARMS);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slotTypes.containsKey(slot)) return slotTypes.get(slot).test(stack);
        return false;
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (owner != null && !owner.level().isClientSide) {
            syncSelf();
        }
        listeners.forEach(listener -> listener.accept(slot));
    }

    public void addListener(Consumer<Integer> listener) {
        listeners.add(listener);
    }

    public void syncSelf() {
        if (owner instanceof ServerPlayer sp && Config.CHARMS_ENABLED.getAsBoolean()) {
            Channel.sendToPlayer(new UpdateCharmsToClient(sp.getId(), this.stacks), sp);
        }
    }

    public void syncFor(Player player) {
        if (player instanceof ServerPlayer sp && Config.CHARMS_ENABLED.getAsBoolean()) {
            Channel.sendToPlayer(new UpdateCharmsToClient(sp.getId(), this.stacks), sp);
        }
    }

    public void syncSelfAndTracking() {
        if (owner instanceof ServerPlayer sp && Config.CHARMS_ENABLED.getAsBoolean()) {
            Channel.sendToSelfAndTracking(new UpdateCharmsToClient(sp.getId(), this.stacks), sp);
        }
    }

    public boolean contains(Item item) {
        for (int i = 0; i < getSlots(); i++) {
            if (getStackInSlot(i).is(item)) return true;
        }
        return false;
    }
    public boolean containsStack(ItemStack stack) {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack s = getStackInSlot(i);
            if (ItemStack.isSameItemSameTags(stack, s)) return true;
        }
        return false;
    }

    public NonNullList<ItemStack> getStacks() {
        return stacks;
    }

/*    public enum CharmType {
        HEAD(ModTags.Items.CHARMS_HEAD),
        NECKLACE(ModTags.Items.CHARMS_NECKLACE),
        RING(ModTags.Items.CHARMS_RING),
        BELT(ModTags.Items.CHARMS_BELT),
        BRACELET(ModTags.Items.CHARMS_BRACELET),
        HAND(ModTags.Items.CHARMS_HAND),
        FEET(ModTags.Items.CHARM_FEET),
        BACK(ModTags.Items.CHARMS_BACK),
        CHARM(ModTags.Items.CHARMS_CHARM);

        private final TagKey<Item> tag;

        CharmType(TagKey<Item> tag) {
            this.tag = tag;
        }

        public boolean test(ItemStack stack) {
            return stack.is(tag);
        }

        public String getLowerCaseName() {
            return name().toLowerCase();
        }
    }*/
}
