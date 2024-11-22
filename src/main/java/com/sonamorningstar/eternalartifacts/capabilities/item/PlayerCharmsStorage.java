package com.sonamorningstar.eternalartifacts.capabilities.item;

import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.client.gui.TabHandler;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.charm.UpdateCharmsToClient;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class PlayerCharmsStorage extends ItemStackHandler {
    private final List<Consumer<Integer>> listeners = new ArrayList<>();
    private final Player player;
    public static final Map<Integer, CharmType> slotTypes = new HashMap<>(12);

    public PlayerCharmsStorage(Player player) {
        super(12);
        this.player = player;
    }

    public static PlayerCharmsStorage get(Player player) {
        return player.getData(ModDataAttachments.PLAYER_CHARMS);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slotTypes.containsKey(slot)) return slotTypes.get(slot).test(stack);
        return false;
    }

    @Override
    protected void onContentsChanged(int slot) {
        syncSelfAndTracking();
        listeners.forEach(listener -> listener.accept(slot));
    }

    public void addListener(Consumer<Integer> listener) {
        listeners.add(listener);
    }

    public void syncSelf() {
        if (player instanceof ServerPlayer sp && Config.CHARMS_ENABLED.getAsBoolean()) {
            Channel.sendToPlayer(new UpdateCharmsToClient(player.getId(), this.stacks), sp);
        }
    }

    public void syncSelfAndTracking() {
        if (player instanceof ServerPlayer && Config.CHARMS_ENABLED.getAsBoolean()) {
            Channel.sendToSelfAndTracking(new UpdateCharmsToClient(player.getId(), this.stacks), player);
        }
    }

    public boolean contains(Item item) {
        for (int i = 0; i < getSlots(); i++) {
            if (getStackInSlot(i).is(item)) return true;
        }
        return false;
    }

    public NonNullList<ItemStack> getStacks() {
        return this.stacks;
    }

    public void setStacks(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
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
