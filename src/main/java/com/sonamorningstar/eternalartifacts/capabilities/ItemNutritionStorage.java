package com.sonamorningstar.eternalartifacts.capabilities;

import com.sonamorningstar.eternalartifacts.content.item.FeedingCanister;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

@Getter
public class ItemNutritionStorage extends NutritionStorage {
    private final ItemStack container;
    public ItemNutritionStorage(ItemStack container) {
        super(256 * (container.getEnchantmentLevel(ModEnchantments.VOLUME.get()) + 1), 256 * (container.getEnchantmentLevel(ModEnchantments.VOLUME.get()) + 1));
        this.container = container;
        CompoundTag tag = container.getOrCreateTag().getCompound("NutritionValues");
        deserializeNBT(tag);
        if (container.getItem() instanceof FeedingCanister canister) {
            canister.getFoodPropertiesBuilder().nutrition(getNutritionAmount());
            canister.getFoodPropertiesBuilder().saturationMod(getSaturationMod());
        }
    }

    @Override
    protected void onChange(Type type) {
        container.getOrCreateTag().put("NutritionValues", serializeNBT());
    }
}
