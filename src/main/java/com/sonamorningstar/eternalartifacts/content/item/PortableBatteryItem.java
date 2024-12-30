package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class PortableBatteryItem extends BatteryItem implements Equipable {
    public PortableBatteryItem(Properties props) {
        super(props);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    @Override
    public void inventoryTick(ItemStack battery, Level level, Entity entity, int slot, boolean isSelected) {
        if (slot == 37 && entity instanceof Player player) {
            chargeSlots(player, battery);
        }
    }

    public void chargeSlots(Player player, ItemStack battery) {
        if (!(battery.getItem() instanceof PortableBatteryItem pbi)) return;
        if (!pbi.isCharging(battery)) return;
        loop: for (SlotType slotType : SlotType.values()) {
            if (canCharge(battery, slotType)) {
                int max = Integer.MAX_VALUE;
                switch (slotType) {
                    case HELD_MAIN -> {
                        if (chargeItem(player.getMainHandItem(), battery, max)) break loop;
                    }
                    case INVENTORY -> player.getInventory().items.subList(9, 36).forEach(stack -> chargeItem(stack, battery, max));
                    case ARMOR -> player.getInventory().armor.forEach(stack -> {
                        if (stack != battery) chargeItem(stack, battery, max);
                    });
                    case HELD_OFF -> chargeItem(player.getOffhandItem(), battery, max);
                    case HOTBAR -> player.getInventory().items.subList(0, 9).forEach(stack -> chargeItem(stack, battery, max));
                    case CHARMS -> CharmStorage.get(player).getStacks().forEach(stack -> {
                        if (stack != battery) chargeItem(stack, battery, max);
                    });
                }
            }
        }
    }
    private static boolean chargeItem(ItemStack stack, ItemStack battery, int amount) {
        if (stack.isEmpty() || battery.isEmpty()) return false;
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        IEnergyStorage batteryEnergy = battery.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null && batteryEnergy != null) {
            int transferred = Math.min(batteryEnergy.extractEnergy(amount, true), energy.receiveEnergy(amount, true));
            if (transferred > 0) {
                batteryEnergy.extractEnergy(transferred, false);
                energy.receiveEnergy(transferred, false);
                return true;
            }
        }
        return false;
    }

    private boolean canCharge(ItemStack stack, SlotType type) {
        if (!stack.hasTag()) return false;
        CompoundTag tag = stack.getTag();
        if (!tag.contains(SlotType.COMPOUND)) return false;
        CompoundTag slots = tag.getCompound(SlotType.COMPOUND);
        if (slots.isEmpty()) return false;
        return slots.getBoolean(type.getKey());
    }

    public enum SlotType {
        INVENTORY("Inventory"),
        HELD_MAIN("MainHand"),
        HELD_OFF("OffHand"),
        ARMOR("Armor"),
        HOTBAR("Hotbar"),
        CHARMS("Charms");

        @Getter
        private final String key;
        public static final String COMPOUND = "ChargeSlots";

        SlotType(String key) {
            this.key = key;
        }

        public void toggle(ItemStack stack) {
            if (stack.hasTag()){
                CompoundTag tag = stack.getTag();
                CompoundTag slots = stack.getOrCreateTagElement(COMPOUND);
                if (!slots.getBoolean(key)) slots.putBoolean(key, true);
                else slots.remove(key);
                if (slots.isEmpty()) tag.remove(COMPOUND);
                else tag.put(COMPOUND, slots);
            } else {
                CompoundTag tag = stack.getOrCreateTag();
                CompoundTag slots = new CompoundTag();
                slots.putBoolean(key, true);
                tag.put(COMPOUND, slots);
            }
        };
    }
}
