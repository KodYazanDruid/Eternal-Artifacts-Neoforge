package com.sonamorningstar.eternalartifacts.capabilities;

import com.sonamorningstar.eternalartifacts.content.item.FeedingCanister;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemNutritionStorage extends NutritionStorage{
    private final ItemStack stack;
    public ItemNutritionStorage(ItemStack stack) {
        super(256 * (stack.getEnchantmentLevel(ModEnchantments.VOLUME.get()) + 1), 256 * (stack.getEnchantmentLevel(ModEnchantments.VOLUME.get()) + 1));
        this.stack = stack;
        CompoundTag tag = stack.getOrCreateTag().getCompound("NutritionValues");
        deserializeNBT(tag);
        if (stack.getItem() instanceof FeedingCanister canister) {
            canister.getFoodPropertiesBuilder().nutrition(getNutritionAmount());
            canister.getFoodPropertiesBuilder().saturationMod(getSaturationMod());
        }
    }

    @Override
    protected void onChange(Type type) {
        stack.getOrCreateTag().put("NutritionValues", serializeNBT());
    }
}
