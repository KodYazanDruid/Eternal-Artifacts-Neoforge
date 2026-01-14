package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.content.block.DrumBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class DrumBlockEntity extends ModBlockEntity {
    public ModFluidStorage tank;
    public Supplier<ModFluidStorage> tankSetter;
    public DrumBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRUM.get(), pos, state);
        this.tankSetter = () -> createBasicTank(((DrumBlock) state.getBlock()).getCapacity());
        this.tank = tankSetter.get();
    }
    
    @Override
    public void onEnchanted(Enchantment enchantment, int level) {
        super.onEnchanted(enchantment, level);
        if (enchantment == ModEnchantments.VOLUME.get()) {
            CompoundTag oldData = new CompoundTag();
            oldData.put("Fluid", tank.serializeNBT());
            this.tank = tankSetter.get();
            this.tank.deserializeNBT(oldData.getCompound("Fluid"));
        }
    }
    
    @Override
    protected boolean shouldSyncOnUpdate() { return true; }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tank.deserializeNBT(tag.getCompound("Fluid"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Fluid", tank.serializeNBT());
    }
}
