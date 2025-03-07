package com.sonamorningstar.eternalartifacts.api.charm;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.client.renderer.util.MobModelRenderer;
import com.sonamorningstar.eternalartifacts.content.item.PortableBatteryItem;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.event.custom.charms.CharmTickEvent;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.charm.CycleWildcardToClient;
import com.sonamorningstar.eternalartifacts.network.charm.UpdateCharmsToClient;
import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

@Getter
public class CharmStorage extends ItemStackHandler {
    private final List<Consumer<Integer>> listeners = new ArrayList<>();
    private final LivingEntity owner;
    public static final String WILDCARD_NBT = "CharmWildcard";
    // 12. slot is wildcard slot. Can hold any charm.
    public static final Map<Integer, CharmType> slotTypes = new HashMap<>(12);
    public static final Set<CharmAttributes> itemAttributes = new HashSet<>();
    public static final Multimap<Item, Multimap<Attribute, AttributeModifier>> nbtAttributes = HashMultimap.create();

    public CharmStorage(IAttachmentHolder holder) {
        super(13);
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
        if (hasNBTType(stack, slot)) return true;
        if (slot == 12 && stack.is(ModTags.Items.CHARMS) && !stack.is(ModTags.Items.CHARMS_WILDCARD_BLACKLISTED)) return true;
        if (slotTypes.containsKey(slot)) return slotTypes.get(slot).test(stack);
        return false;
    }
    
    private boolean hasNBTType(ItemStack stack, int slot) {
        if (!stack.hasTag()) return false;
        if (slot == 12) return true;
        CharmType type = slotTypes.get(slot);
        ListTag listTag = stack.getTag().getList(CharmType.CHARM_KEY, 10);
        for (Tag tag : listTag) {
            CompoundTag compound = (CompoundTag) tag;
            if (compound.getString("CharmType").equals(type.getLowerCaseName())) return true;
        }
        return false;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack stack = getStackInSlot(slot);
        return EnchantmentHelper.hasBindingCurse(stack) ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
    }
    
    @Override
    protected void onLoad() {
        super.onLoad();
        //updateCharmAttributes();
    }
    
    @Override
    protected void onContentsChanged(int slot) {
        if (owner != null) {
            if (!owner.level().isClientSide){
                syncSelfAndTracking(owner);
                if (slot == 0) {
                    MobModelRenderer.dummy = null;
                }
                listeners.forEach(listener -> listener.accept(slot));
            }
        }
    }

    public void addListener(Consumer<Integer> listener) {
        listeners.add(listener);
    }

    //region Sync
    public void syncSelf() {
        if (owner instanceof ServerPlayer sp && Config.CHARMS_ENABLED.getAsBoolean()) {
            Channel.sendToPlayer(new UpdateCharmsToClient(sp.getId(), this.stacks), sp);
            Channel.sendToPlayer(new CycleWildcardToClient(sp.getId(), canHaveWildcard(sp)), sp);
            updateCharmAttributes();
        }
    }

    public static void syncFor(Player player) {
        if (player instanceof ServerPlayer sp && Config.CHARMS_ENABLED.getAsBoolean()) {
            CharmStorage storage = get(sp);
            Channel.sendToPlayer(new UpdateCharmsToClient(sp.getId(), storage.stacks), sp);
            Channel.sendToPlayer(new CycleWildcardToClient(sp.getId(), canHaveWildcard(sp)), sp);
            storage.updateCharmAttributes();
        }
    }
    
    public static void syncForAndTracking(LivingEntity entity) {
        if (Config.CHARMS_ENABLED.getAsBoolean()) {
            CharmStorage storage = get(entity);
            Channel.sendToSelfAndTracking(new UpdateCharmsToClient(entity.getId(), storage.stacks), entity);
            Channel.sendToSelfAndTracking(new CycleWildcardToClient(entity.getId(), canHaveWildcard(entity)), entity);
            storage.updateCharmAttributes();
        }
    }

    public void syncSelfAndTracking(LivingEntity tracked) {
        if (Config.CHARMS_ENABLED.getAsBoolean()) {
            Channel.sendToSelfAndTracking(new UpdateCharmsToClient(tracked.getId(), this.stacks), tracked);
            Channel.sendToSelfAndTracking(new CycleWildcardToClient(tracked.getId(), canHaveWildcard(tracked)), tracked);
            updateCharmAttributes();
        }
    }
    
    public void synchTracking() {
        if (Config.CHARMS_ENABLED.getAsBoolean()) {
            Channel.sendToAllTracking(new UpdateCharmsToClient(owner.getId(), this.stacks), owner);
            Channel.sendToAllTracking(new CycleWildcardToClient(owner.getId(), canHaveWildcard(owner)), owner);
            updateCharmAttributes();
        }
    }
    //endregion

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

    public void updateCharmAttributes() {
        for (int i = 0; i < getSlots(); i++) calculateAttributeForSlot(i);
    }
    private void calculateAttributeForSlot(int slot) {
        ItemStack stack = getStackInSlot(slot);
        checkNBTAttributes(stack, slot);
        itemAttributes.forEach(charmAttr -> charmAttr.getModifiers().forEach((attribute, modifier) -> {
            AttributeInstance instance = owner.getAttribute(attribute);
            CharmType type = slotTypes.get(slot);
            if (instance != null && (charmAttr.getTypes().contains(type) || slot == 12)) {
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
    
    //region NBT Attributes
    private void checkNBTAttributes(ItemStack stack, int slot) {
        nbtAttributes.forEach((item, multimap) -> multimap.forEach((attribute, modifier) -> {
			AttributeInstance instance = owner.getAttribute(attribute);
			if (instance != null && instance.hasModifier(modifier)) {
				boolean shouldRemove = true;
				for (int i = 0; i < getSlots(); i++) {
					ItemStack otherStack = getStackInSlot(i);
					if (getAttributesFromNBT(otherStack, i).containsEntry(attribute, modifier)) {
						shouldRemove = false;
						break;
					}
				}
				if (shouldRemove) {
					instance.removeModifier(modifier.getId());
				}
			}
		}));
        getAttributesFromNBT(stack, slot).forEach((attr, mod) -> {
            var attrInstance = owner.getAttribute(attr);
            if (attrInstance != null && !attrInstance.hasModifier(mod)) {
                attrInstance.addTransientModifier(mod);
                Multimap<Attribute, AttributeModifier> hash = HashMultimap.create();
                hash.put(attr, mod);
                nbtAttributes.put(stack.getItem(), hash);
            }
        });
    }
    public static Multimap<Attribute, AttributeModifier> getAttributesFromNBT(ItemStack stack, int slot) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        if (stack.hasTag() && stack.getTag().contains(CharmAttributes.ATTR_KEY, 9)) {
            ListTag listtag = stack.getTag().getList(CharmAttributes.ATTR_KEY, 10);
            for(int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag = listtag.getCompound(i);
                if (slot == 12 || !compoundtag.contains("Slot", 8) ||
                    compoundtag.getString("Slot").equals(slotTypes.get(slot).getLowerCaseName())) {
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
    //endregion

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
    
    public void setWildcardNbt(boolean value) {
        owner.getPersistentData().putBoolean(WILDCARD_NBT, value);
        updateCharmAttributes();
    }

    public static boolean canHaveWildcard(@NotNull LivingEntity living) {
     return living.getPersistentData().contains(WILDCARD_NBT) && living.getPersistentData().getBoolean(WILDCARD_NBT);
    }
}
