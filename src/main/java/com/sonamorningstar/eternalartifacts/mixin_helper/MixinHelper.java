package com.sonamorningstar.eternalartifacts.mixin_helper;

import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import com.sonamorningstar.eternalartifacts.core.ModTags;
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
}
