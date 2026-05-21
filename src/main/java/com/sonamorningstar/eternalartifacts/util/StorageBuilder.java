package com.sonamorningstar.eternalartifacts.util;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.IntConsumer;
import java.util.function.Predicate;

/**
 * Helper class for building fluid, energy, and item storages with fluent API.
 * Supports builder pattern for easier configuration.
 */
public class StorageBuilder {

    /**
     * Fluid Storage Builder
     */
    public static class FluidStorageBuilder {
        private final ModBlockEntity blockEntity;
        private final int baseSize;
        private boolean canDrain = true;
        private boolean canFill = true;
        private boolean isRecipeFinder = false;
        private Predicate<FluidStack> validator = s -> true;
        private Runnable[] listeners = new Runnable[0];

        public FluidStorageBuilder(ModBlockEntity blockEntity, int baseSize) {
            this.blockEntity = blockEntity;
            this.baseSize = baseSize;
        }

        @Contract("_ -> this")
        public FluidStorageBuilder canDrain(boolean canDrain) {
            this.canDrain = canDrain;
            return this;
        }

        @Contract("_ -> this")
        public FluidStorageBuilder canFill(boolean canFill) {
            this.canFill = canFill;
            return this;
        }

        @Contract("_ -> this")
        public FluidStorageBuilder recipeFinder(boolean isRecipeFinder) {
            this.isRecipeFinder = isRecipeFinder;
            return this;
        }

        @Contract("_ -> this")
        public FluidStorageBuilder validator(Predicate<FluidStack> validator) {
            this.validator = validator;
            return this;
        }

        @Contract("_ -> this")
        public FluidStorageBuilder listeners(Runnable... listeners) {
            this.listeners = listeners;
            return this;
        }

        public ModFluidStorage build() {
            int volume = blockEntity.getVolumeLevel();
            int capacity = baseSize * (volume + 1);
            
            return new ModFluidStorage(capacity, validator) {
                @Override
                protected void onContentsChanged() {
                    if (isRecipeFinder) blockEntity.findRecipe();
                    if (blockEntity instanceof Machine<?> machine) {
                        machine.updateProcessCondition();
                    }
                    blockEntity.markDirty();
                    for (Runnable runnable : listeners) runnable.run();
                }

                @Override
                public FluidStack drain(int maxDrain, FluidAction action) {
                    return canDrain ? super.drain(maxDrain, action) : FluidStack.EMPTY;
                }

                @Override
                public int fill(FluidStack resource, FluidAction action) {
                    return canFill ? super.fill(resource, action) : 0;
                }
            };
        }
    }

    /**
     * Energy Storage Builder
     */
    public static class EnergyStorageBuilder {
        private final ModBlockEntity blockEntity;
        private final int baseSize;
        private int maxReceive;
        private int maxExtract;
        private boolean canReceive = true;
        private boolean canExtract = true;

        public EnergyStorageBuilder(ModBlockEntity blockEntity, int baseSize) {
            this.blockEntity = blockEntity;
            this.baseSize = baseSize;
            this.maxReceive = baseSize;
            this.maxExtract = baseSize;
        }

        public EnergyStorageBuilder(ModBlockEntity blockEntity, int baseSize, int transfer) {
            this(blockEntity, baseSize);
            this.maxReceive = transfer;
            this.maxExtract = transfer;
        }

        @Contract("_ -> this")
        public EnergyStorageBuilder maxTransfer(int maxTransfer) {
            this.maxReceive = maxTransfer;
            this.maxExtract = maxTransfer;
            return this;
        }

        @Contract("_, _ -> this")
        public EnergyStorageBuilder maxTransfer(int maxReceive, int maxExtract) {
            this.maxReceive = maxReceive;
            this.maxExtract = maxExtract;
            return this;
        }

        @Contract("_ -> this")
        public EnergyStorageBuilder canReceive(boolean canReceive) {
            this.canReceive = canReceive;
            return this;
        }

        @Contract("_ -> this")
        public EnergyStorageBuilder canExtract(boolean canExtract) {
            this.canExtract = canExtract;
            return this;
        }

        public ModEnergyStorage build() {
            int volume = blockEntity.getVolumeLevel();
            int capacity = baseSize * (volume + 1);
            int receive = maxReceive * (volume + 1);
            int extract = maxExtract * (volume + 1);

            return new ModEnergyStorage(capacity, receive, extract) {
                @Override
                public void onEnergyChanged() {
                    blockEntity.markDirty();
                }

                @Override
                public boolean canReceive() {
                    return canReceive;
                }

                @Override
                public boolean canExtract() {
                    return canExtract;
                }
            };
        }
    }

    /**
     * Item Storage Builder
     */
    public static class ItemStorageBuilder {
        private final ModBlockEntity blockEntity;
        private final int size;
        private boolean canInsert = true;
        private boolean isRecipeFinder = false;
        private List<Integer> outputSlots;
        private BiPredicate<Integer, ItemStack> validator;
        private Int2IntFunction slotLimit;
        private IntConsumer[] consumers = new IntConsumer[0];

        public ItemStorageBuilder(ModBlockEntity blockEntity, int size) {
            this.blockEntity = blockEntity;
            this.size = size;
        }

        @Contract("_ -> this")
        public ItemStorageBuilder canInsert(boolean canInsert) {
            this.canInsert = canInsert;
            return this;
        }

        @Contract("_ -> this")
        public ItemStorageBuilder recipeFinder(boolean isRecipeFinder) {
            this.isRecipeFinder = isRecipeFinder;
            return this;
        }

        @Contract("_ -> this")
        public ItemStorageBuilder outputSlots(List<Integer> outputSlots) {
            this.outputSlots = outputSlots;
            return this;
        }

        @Contract("_ -> this")
        public ItemStorageBuilder validator(BiPredicate<Integer, ItemStack> validator) {
            this.validator = validator;
            return this;
        }

        @Contract("_ -> this")
        public ItemStorageBuilder slotLimit(Int2IntFunction slotLimit) {
            this.slotLimit = slotLimit;
            return this;
        }

        @Contract("_ -> this")
        public ItemStorageBuilder consumers(IntConsumer... consumers) {
            this.consumers = consumers;
            return this;
        }

        public ModItemStorage build() {
            return new ModItemStorage(size) {
                @Override
                protected void onContentsChanged(int slot) {
                    if (isRecipeFinder && (outputSlots == null || !outputSlots.contains(slot))) {
                        blockEntity.findRecipe();
                    }
                    if (blockEntity instanceof Machine<?> machine) {
                        machine.updateProcessCondition();
                    }
                    for (IntConsumer consumer : consumers) consumer.accept(slot);
                    blockEntity.markDirty();
                }

                @Override
                public boolean isItemValid(int slot, ItemStack stack) {
                    if (outputSlots != null && outputSlots.contains(slot)) {
                        return false;
                    }
                    if (validator != null) {
                        return validator.test(slot, stack);
                    }
                    return canInsert;
                }

                @Override
                public int getSlotLimit(int slot) {
                    return slotLimit != null ? slotLimit.get(slot) : 64;
                }
            };
        }
    }

    // Factory methods for easier access
    public static FluidStorageBuilder fluidStorage(ModBlockEntity blockEntity, int baseSize) {
        return new FluidStorageBuilder(blockEntity, baseSize);
    }

    public static EnergyStorageBuilder energyStorage(ModBlockEntity blockEntity, int baseSize) {
        return new EnergyStorageBuilder(blockEntity, baseSize);
    }

    public static EnergyStorageBuilder energyStorage(ModBlockEntity blockEntity, int baseSize, int transfer) {
        return new EnergyStorageBuilder(blockEntity, baseSize, transfer);
    }

    public static ItemStorageBuilder itemStorage(ModBlockEntity blockEntity, int size) {
        return new ItemStorageBuilder(blockEntity, size);
    }
}
