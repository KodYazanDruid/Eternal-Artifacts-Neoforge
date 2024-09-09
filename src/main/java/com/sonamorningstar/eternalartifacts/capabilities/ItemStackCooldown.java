package com.sonamorningstar.eternalartifacts.capabilities;

import com.sonamorningstar.eternalartifacts.capabilities.handler.IItemCooldown;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class ItemStackCooldown implements IItemCooldown {
    @Getter
    private int cooldown = 0;
    @Getter
    private final int maxCooldown;
    private final ItemStack stack;

    public ItemStackCooldown(ItemStack stack, int maxCooldown) {
        this.stack = stack;
        this.maxCooldown = maxCooldown;

        CompoundTag tag = stack.getTag();
        if (tag != null) cooldown = tag.getInt("Cooldown");
    }

    public void setCooldown(int tick) {
        cooldown = Mth.clamp(tick, 0, maxCooldown);
        onChange();
    }

    @Override
    public int addCooldown(int tick) {
        if (cooldown == maxCooldown) return cooldown;
        cooldown = Math.min(cooldown + tick, maxCooldown);
        onChange();
        return cooldown;
    }

    @Override
    public int lowerCooldown(int tick) {
        if (tick >= cooldown) return 0;
        cooldown = Math.max(0, cooldown - tick);
        onChange();
        return cooldown;
    }

    @Override
    public void resetCooldown() {
        cooldown = 0;
        onChange();
    }

    @Override
    public boolean isOnCooldown() {return cooldown > 0;}

    @Override
    public void onChange() {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("Cooldown", cooldown);
    }
}
