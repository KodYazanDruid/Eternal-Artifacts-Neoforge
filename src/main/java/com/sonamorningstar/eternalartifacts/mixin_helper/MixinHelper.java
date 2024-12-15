package com.sonamorningstar.eternalartifacts.mixin_helper;

import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import com.sonamorningstar.eternalartifacts.capabilities.item.CharmStorage;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MixinHelper {
    public static ItemStack getElytraFly(LivingEntity living) {
        return PlayerCharmManager.findCharm(living, st -> st.canElytraFly(living));
    }
    public static ItemStack getUndyingTotem(LivingEntity living) {
        return PlayerCharmManager.findCharm(living, Items.TOTEM_OF_UNDYING);
    }
    public static ItemStack getPiglinPacifier(LivingEntity living) {
        return PlayerCharmManager.findCharm(living, st -> st.makesPiglinsNeutral(living));
    }
    public static ItemStack getShulkerShell(LivingEntity living) {
        return PlayerCharmManager.findCharm(living, st -> st.is(ModTags.Items.SHULKER_SHELL));
    }
    public static ItemStack getTurtleHelmet(LivingEntity living) {
        return PlayerCharmManager.findCharm(living, Items.TURTLE_HELMET);
    }
    public static ItemStack getHeadEquipment(LivingEntity living) {
        //return PlayerCharmManager.findCharm(living, st -> LivingEntity.getEquipmentSlotForItem(st) == EquipmentSlot.HEAD);
        return PlayerCharmManager.findCharm(living, CharmStorage.CharmType.HEAD);
    }
    public static ItemStack getChestEquipment(LivingEntity living) {
        //return PlayerCharmManager.findCharm(living, st -> LivingEntity.getEquipmentSlotForItem(st) == EquipmentSlot.CHEST);
        return PlayerCharmManager.findCharm(living, CharmStorage.CharmType.BACK);
    }
    public static ItemStack getLegsEquipment(LivingEntity living) {
        //return PlayerCharmManager.findCharm(living, st -> LivingEntity.getEquipmentSlotForItem(st) == EquipmentSlot.LEGS);
        return PlayerCharmManager.findCharm(living, CharmStorage.CharmType.BELT);
    }
    public static ItemStack getFeetEquipment(LivingEntity living) {
        return PlayerCharmManager.findCharm(living, CharmStorage.CharmType.FEET);
    }
}
