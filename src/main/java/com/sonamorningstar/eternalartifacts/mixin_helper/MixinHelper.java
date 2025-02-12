package com.sonamorningstar.eternalartifacts.mixin_helper;

import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.util.collections.DefaultConcurrentHashMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Map;

public class MixinHelper {
    public static final Map<LivingEntity, Integer> jumpTokens = new DefaultConcurrentHashMap<>(1);
    
    public static ItemStack getElytraFly(LivingEntity living) {
        return PlayerCharmManager.findCharm(living, st -> st.canElytraFly(living));
    }
    public static ItemStack getUndyingTotem(LivingEntity living) {
        return PlayerCharmManager.findCharm(living, Items.TOTEM_OF_UNDYING);
    }
    public static ItemStack getPiglinPacifier(LivingEntity living) {
        return PlayerCharmManager.findCharm(living, st -> st.makesPiglinsNeutral(living));
    }
}
