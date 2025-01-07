package com.sonamorningstar.eternalartifacts.content.item;

import com.google.common.collect.Multimap;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.endernotebook.OpenItemStackScreenToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class LightSaberItem extends SwordItem {
    public LightSaberItem(Tier tier) {
        super(tier, 5, -2.0F, new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(player instanceof ServerPlayer serverPlayer) {
            Channel.sendToPlayer(new OpenItemStackScreenToClient(itemstack), serverPlayer);
        }
        return super.use(level, player, hand);
    }

    public void changeColor(ItemStack stack, int color) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt("Color", color);
    }

    public void toggleGlint(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (!nbt.getBoolean("SuppressGlint")) nbt.putBoolean("SuppressGlint", true);
        else stack.removeTagKey("SuppressGlint");
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().getBoolean("SuppressGlint")) return false;
        else return super.isFoil(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null) {
            return energy.getEnergyStored() > 0;
        }
        return false;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float charge = getChargeLevel(stack);
        return (int) Math.round(charge * 13.0);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x880808;
    }

    private static float getChargeLevel(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energy != null ? energy.getEnergyStored() / (float) energy.getMaxEnergyStored() : 0;
    }

    public boolean canSupply(ItemStack stack, int amount) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energy != null && energy.getEnergyStored() >= amount;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        int maxDamage = (damage - stack.getDamageValue()) * 15;
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null) {
            energy.extractEnergy(maxDamage, false);
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return super.getAttributeModifiers(slot, stack);
    }
}
