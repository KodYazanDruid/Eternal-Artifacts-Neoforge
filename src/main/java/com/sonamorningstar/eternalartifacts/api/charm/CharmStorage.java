package com.sonamorningstar.eternalartifacts.api.charm;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.content.item.PortableBatteryItem;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.event.custom.charms.CharmTickEvent;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.charm.UpdateCharmsToClient;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.*;
import java.util.function.Consumer;

@Getter
public class CharmStorage extends ItemStackHandler {
    private final List<Consumer<Integer>> listeners = new ArrayList<>();
    private final LivingEntity owner;
    public static final Map<Integer, CharmType> slotTypes = new HashMap<>(12);
    public static final Set<CharmAttributes> itemAttributes = new HashSet<>();
    private final Multimap<Item, Pair<Attribute, AttributeModifier>> nbtAttributes = HashMultimap.create();

    public CharmStorage(IAttachmentHolder holder) {
        super(12);
        this.owner = (LivingEntity) holder;
    }

    public static CharmStorage get(LivingEntity living) {
        return living.getData(ModDataAttachments.CHARMS);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (contains(stack.getItem())) return false;
        if (slotTypes.containsKey(slot)) return slotTypes.get(slot).test(stack);
        return false;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack stack = getStackInSlot(slot);
        return EnchantmentHelper.hasBindingCurse(stack) ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (owner != null) {
            if (!owner.level().isClientSide){
                syncSelf();
                listeners.forEach(listener -> listener.accept(slot));
            }
        }
    }

    public void addListener(Consumer<Integer> listener) {
        listeners.add(listener);
    }

    public void syncSelf() {
        if (owner instanceof ServerPlayer sp && Config.CHARMS_ENABLED.getAsBoolean()) {
            Channel.sendToPlayer(new UpdateCharmsToClient(sp.getId(), this.stacks), sp);
            updateCharmAttributes();
        }
    }

    public static void syncFor(Player player) {
        if (player instanceof ServerPlayer sp && Config.CHARMS_ENABLED.getAsBoolean()) {
            CharmStorage storage = get(sp);
            Channel.sendToPlayer(new UpdateCharmsToClient(sp.getId(), storage.stacks), sp);
            storage.updateCharmAttributes();
        }
    }

    public void syncSelfAndTracking() {
        if (owner instanceof ServerPlayer sp && Config.CHARMS_ENABLED.getAsBoolean()) {
            Channel.sendToSelfAndTracking(new UpdateCharmsToClient(sp.getId(), this.stacks), sp);
            updateCharmAttributes();
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

    public void updateCharmAttributes() {
        for (int i = 0; i < getSlots(); i++) calculateAttributeForSlot(i);
    }
    private void calculateAttributeForSlot(int slot) {
        ItemStack stack = getStackInSlot(slot);
        if (stack.hasTag() && stack.getTag().contains(CharmAttributes.ATTR_KEY, 9)){
            /*var nbtAttributes = getAttributesFromNBT(stack, slot);
            nbtAttributes.forEach((attr, mod) -> {
                var attrInstance = owner.getAttribute(attr);
                if (attrInstance != null && !attrInstance.hasModifier(mod)) {
                    attrInstance.addTransientModifier(mod);
                    this.nbtAttributes.put(stack.getItem(), Pair.of(attr, mod));
                }
            });*/
        } else {
            /*nbtAttributes.forEach((item, pair) -> {
                for (int i = 0; i < getSlots(); i++) {
                    if (i != slot) {
                        ItemStack otherStack = getStackInSlot(i);
                        if (otherStack.is(item)) {
                            AttributeInstance instance = owner.getAttribute(pair.getFirst());
                            if (instance != null) {
                                instance.removeModifier(pair.getSecond().getId());
                            }
                        }
                    }
                }
            });*/
            itemAttributes.forEach(charmAttr -> charmAttr.getModifiers().forEach((attribute, modifier) -> {
                AttributeInstance instance = owner.getAttribute(attribute);
                CharmType type = slotTypes.get(slot);
                if (instance != null && charmAttr.getTypes().contains(type)) {
                    if (charmAttr.isStackCorrect(stack)) {
                        if (!instance.hasModifier(modifier)) {
                            instance.addTransientModifier(modifier);
                        }
                    } else {
                        boolean shouldRemove = true;
                        for (int i = 0; i < getSlots(); i++) {
                            if (i != slot) {
                                ItemStack otherStack = getStackInSlot(i);
                                if (charmAttr.isStackCorrect(otherStack)) {
                                    shouldRemove = false;
                                    break;
                                }
                            }
                        }
                        if (shouldRemove) {
                            instance.removeModifier(modifier.getId());
                        }
                    }
                }
            }));
        }
    }

    private Multimap<Attribute, AttributeModifier> getAttributesFromNBT(ItemStack stack, int slot) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        if (stack.hasTag() && stack.getTag().contains(CharmAttributes.ATTR_KEY, 9)) {
            ListTag listtag = stack.getTag().getList(CharmAttributes.ATTR_KEY, 10);
            for(int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag = listtag.getCompound(i);
                if (!compoundtag.contains("Slot", 8) || compoundtag.getString("Slot").equals(slotTypes.get(slot).getLowerCaseName())) {
                    Optional<Attribute> optional = BuiltInRegistries.ATTRIBUTE.getOptional(ResourceLocation.tryParse(compoundtag.getString("AttributeName")));
                    if (optional.isPresent()) {
                        AttributeModifier attributemodifier = AttributeModifier.load(compoundtag);
                        if (attributemodifier != null
                                && attributemodifier.getId().getLeastSignificantBits() != 0L
                                && attributemodifier.getId().getMostSignificantBits() != 0L) {
                            multimap.put(optional.get(), attributemodifier);
                        }
                    }
                }
            }
        }
        return multimap;
    }

    /*private void removeAttributesFromNBT(ItemStack stack, int slot) {
        if (stack.hasTag() && stack.getTag().contains(CharmAttributes.ATTR_KEY, 9)) {
            ListTag listtag = stack.getTag().getList(CharmAttributes.ATTR_KEY, 10);
            for (int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag = listtag.getCompound(i);
                if (!compoundtag.contains("Slot", 8) || compoundtag.getString("Slot").equals(slotTypes.get(slot).getLowerCaseName())) {
                    Optional<Attribute> optional = BuiltInRegistries.ATTRIBUTE.getOptional(ResourceLocation.tryParse(compoundtag.getString("AttributeName")));
                    if (optional.isPresent()) {
                        AttributeModifier attributemodifier = AttributeModifier.load(compoundtag);
                        if (attributemodifier != null
                                && attributemodifier.getId().getLeastSignificantBits() != 0L
                                && attributemodifier.getId().getMostSignificantBits() != 0L) {
                            AttributeInstance instance = owner.getAttribute(optional.get());
                            if (instance != null) {
                                instance.removeModifier(attributemodifier.getId());
                            }
                        }
                    }
                }
            }
        }
    }*/


    public static void charmTick(CharmTickEvent event) {
        LivingEntity living = event.getEntity();
        ItemStack charm = event.getCharm();
        int slot = event.getSlot();
        if (charm.is(ModItems.MEDKIT) && living instanceof Player player) {
            if (!player.getCooldowns().isOnCooldown(ModItems.MEDKIT.get()) && !player.hasEffect(MobEffects.REGENERATION)) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 25, 1, false, false, false));
            }
        }
        if (charm.getItem() instanceof MapItem mapItem) {
            mapItem.inventoryTick(charm, living.level(), living, -1, true);
        }
        if (charm.getItem() instanceof CompassItem compassItem) {
            compassItem.inventoryTick(charm, living.level(), living, -1, true);
        }
        if (charm.getItem() instanceof PortableBatteryItem battery && living instanceof Player player) {
            battery.chargeSlots(player, charm);
        }
    }
}
