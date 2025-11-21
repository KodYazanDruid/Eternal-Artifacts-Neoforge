package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.event.custom.GetMachineEnchantmentLevelEvent;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.*;

public class ModBlockEntity extends BlockEntity {
    public final Object2IntMap<Enchantment> enchantments = new Object2IntOpenHashMap<>();
    public static final String CONFIG_TAG_KEY = "Config";
    @Getter
	private final MachineConfiguration configuration = new MachineConfiguration();
    
    public ModBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        registerConfigs();
    }
    
    public void registerConfigs() {}
    
    public void registerCapabilityConfigs(BlockCapability<?,?> cap) {}
    
    protected boolean shouldSyncOnUpdate() {
        return false;
    }

    protected void findRecipe() {}

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return shouldSyncOnUpdate() ? ClientboundBlockEntityDataPacket.create(this) : super.getUpdatePacket();
    }

    protected void saveSynced(CompoundTag tag) {}
    
    /**
     * Used to update some variables like max progress, capacity etc.
     */
    public void onEnchanted(Enchantment enchantment, int level) {}
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveSynced(tag);
        saveAdditional(tag);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveSynced(tag);
        if (!enchantments.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Object2IntMap.Entry<Enchantment> entry : enchantments.object2IntEntrySet()) {
                CompoundTag enchTag = EnchantmentHelper.storeEnchantment(
                    EnchantmentHelper.getEnchantmentId(entry.getKey()),
                    (byte) entry.getIntValue()
                );
                listTag.add(enchTag);
            }
            tag.put("Enchantments", listTag);
        }
        CompoundTag configTag = new CompoundTag();
        configuration.save(configTag);
        tag.put(CONFIG_TAG_KEY, configTag);
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        enchantments.forEach(this::onEnchanted);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadEnchants(tag.getList("Enchantments", Tag.TAG_COMPOUND));
        CompoundTag configTag = tag.getCompound(CONFIG_TAG_KEY);
        configuration.load(configTag);
    }
    
    public void loadConfiguration(ItemStack drive) {
        if (drive.hasTag()){
            getConfiguration().load(drive.getTag().getCompound(CONFIG_TAG_KEY));
            sendUpdate();
        }
    }
    
    public void sendUpdate(){
        setChanged();
        if(level != null && !isRemoved() && level.hasChunkAt(worldPosition))
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }
    
    public void enchant(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        onEnchanted(enchantment, level);
        sendUpdate();
    }
    
    public void loadEnchants(ListTag listTag) {
        enchantments.clear();
        var enchs = EnchantmentHelper.deserializeEnchantments(listTag);
        if (!enchs.isEmpty()) enchantments.putAll(enchs);
    }
    
    public int getEnchantmentLevel(Enchantment enchantment) {
        var event = new GetMachineEnchantmentLevelEvent(this, enchantments, enchantment);
        NeoForge.EVENT_BUS.post(event);
        return event.getEnchantments().getOrDefault(enchantment, 0);
    }
    
    public int getVolumeLevel() {
        return getEnchantmentLevel(ModEnchantments.VOLUME.get());
    }
    
    public boolean isVersatile() {
        return getEnchantmentLevel(ModEnchantments.VERSATILITY.get()) > 0;
    }

    protected ModFluidStorage createDefaultTank() {return createBasicTank(16000);}
    protected ModFluidStorage createBasicTank(int size, Runnable... listeners) {
        int volume = getVolumeLevel();
        return new ModFluidStorage(size * (volume + 1)) {
            @Override
            protected void onContentsChanged() {
                if (ModBlockEntity.this instanceof Machine<?> machine) {
                    machine.setProcessCondition(new ProcessCondition(machine), RecipeCache.getCachedRecipe(machine));
                }
                sendUpdate();
                for (Runnable runnable : listeners) runnable.run();
            }
        };
    }
    protected ModFluidStorage createRecipeFinderTank(int size) {
        int volume = getVolumeLevel();
        return new ModFluidStorage(size * (volume + 1)) {
            @Override
            protected void onContentsChanged() {
                findRecipe();
                if (ModBlockEntity.this instanceof Machine<?> machine) {
                    machine.setProcessCondition(new ProcessCondition(machine), RecipeCache.getCachedRecipe(machine));
                }
                sendUpdate();
            }
        };
    }
    protected ModFluidStorage createBasicTank(int size, boolean canDrain, boolean canFill, Runnable... listeners) {
        int volume = getVolumeLevel();
        return new ModFluidStorage(size * (volume + 1)) {
            @Override
            protected void onContentsChanged() {
                if (ModBlockEntity.this instanceof Machine<?> machine) {
                    machine.setProcessCondition(new ProcessCondition(machine), RecipeCache.getCachedRecipe(machine));
                }
                sendUpdate();
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
    protected ModFluidStorage createRecipeFinderTank(int size, boolean canDrain, boolean canFill) {
        int volume = getVolumeLevel();
        return new ModFluidStorage(size * (volume + 1)) {
            @Override
            protected void onContentsChanged() {
                findRecipe();
                if (ModBlockEntity.this instanceof Machine<?> machine) {
                    machine.setProcessCondition(new ProcessCondition(machine), RecipeCache.getCachedRecipe(machine));
                }
                sendUpdate();
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
    protected ModFluidStorage createBasicTank(int size, Predicate<FluidStack> validator, boolean canDrain, boolean canFill, Runnable... run) {
        int volume = getVolumeLevel();
        return new ModFluidStorage(size * (volume + 1), validator) {
            @Override
            protected void onContentsChanged() {
                if (ModBlockEntity.this instanceof Machine<?> machine) {
                    machine.setProcessCondition(new ProcessCondition(machine), RecipeCache.getCachedRecipe(machine));
                }
                sendUpdate();
                for (Runnable runnable : run) runnable.run();
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
    protected ModFluidStorage createRecipeFinderTank(int size, Predicate<FluidStack> validator, boolean canDrain, boolean canFill) {
        int volume = getVolumeLevel();
        return new ModFluidStorage(size * (volume + 1), validator) {
            @Override
            protected void onContentsChanged() {
                findRecipe();
                if (ModBlockEntity.this instanceof Machine<?> machine) {
                    machine.setProcessCondition(new ProcessCondition(machine), RecipeCache.getCachedRecipe(machine));
                }
                sendUpdate();
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

    protected ModEnergyStorage createDefaultEnergy() {return createBasicEnergy(50000, 2500, true, false);}
    protected ModEnergyStorage createBasicEnergy(int size, int transfer, boolean canReceive, boolean canExtract) {
        return createBasicEnergy(size, transfer, transfer, canReceive, canExtract);
    }
    protected ModEnergyStorage createBasicEnergy(int size, int maxReceive, int maxExtract, boolean canReceive, boolean canExtract) {
        int volume = getVolumeLevel();
        return new ModEnergyStorage(size * (volume + 1), maxReceive, maxExtract) {
            @Override
            public void onEnergyChanged() {
                sendUpdate();
            }
            @Override
            public boolean canReceive() {return canReceive;}
            @Override
            public boolean canExtract() {return canExtract;}
        };
    }

    @SafeVarargs
    protected final ModItemStorage createBasicInventory(int size, boolean canInsert, Consumer<Integer>... consumers) {
        return new ModItemStorage(size) {
            @Override
            protected void onContentsChanged(int slot) {
                if (ModBlockEntity.this instanceof Machine<?> machine) {
                    machine.setProcessCondition(new ProcessCondition(machine), RecipeCache.getCachedRecipe(machine));
                }
                for (Consumer<Integer> consumer : consumers) consumer.accept(slot);
                ModBlockEntity.this.sendUpdate();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return canInsert;
            }
        };
    }
    @SafeVarargs
    protected final ModItemStorage createBasicInventory(int size, List<Integer> outputSlots, BiFunction<Integer, ItemStack, Boolean> isValid, Consumer<Integer>... consumers) {
        return new ModItemStorage(size) {
            @Override
            protected void onContentsChanged(int slot) {
                if (ModBlockEntity.this instanceof Machine<?> machine) {
                    machine.setProcessCondition(new ProcessCondition(machine), RecipeCache.getCachedRecipe(machine));
                }
                for (Consumer<Integer> consumer : consumers) consumer.accept(slot);
                ModBlockEntity.this.sendUpdate();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return !outputSlots.contains(slot) && isValid.apply(slot, stack);
            }
        };
    }
    protected final ModItemStorage createRecipeFinderInventory(int size, List<Integer> outputSlots) {
        return new ModItemStorage(size) {
            @Override
            protected void onContentsChanged(int slot) {
                if (!outputSlots.contains(slot)) {
                    findRecipe();
                }
                if (ModBlockEntity.this instanceof Machine<?> machine) {
                    machine.setProcessCondition(new ProcessCondition(machine), RecipeCache.getCachedRecipe(machine));
                }
                super.onContentsChanged(slot);
                ModBlockEntity.this.sendUpdate();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {return !outputSlots.contains(slot);}
        };
    }
    protected ModItemStorage createBasicInventory(int size, BiPredicate<Integer, ItemStack> isValid) {
        return createBasicInventory(size, isValid, limit -> 64);
    }
    protected ModItemStorage createBasicInventory(int size, BiPredicate<Integer, ItemStack> isValid, Int2IntFunction slotLimit) {
        return new ModItemStorage(size) {
            @Override
            protected void onContentsChanged(int slot) {
                if (ModBlockEntity.this instanceof Machine<?> machine) {
                    machine.setProcessCondition(new ProcessCondition(machine), RecipeCache.getCachedRecipe(machine));
                }
                super.onContentsChanged(slot);
                ModBlockEntity.this.sendUpdate();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return isValid.test(slot, stack);
            }
            
            @Override
            public int getSlotLimit(int slot) {
                return slotLimit.get(slot);
            }
        };
    }
    protected ModItemStorage createRecipeFinderInventory(int size, BiPredicate<Integer, ItemStack> isValid) {
        return new ModItemStorage(size) {
            @Override
            protected void onContentsChanged(int slot) {
                findRecipe();
                if (ModBlockEntity.this instanceof Machine<?> machine) {
                    machine.setProcessCondition(new ProcessCondition(machine), RecipeCache.getCachedRecipe(machine));
                }
                super.onContentsChanged(slot);
                ModBlockEntity.this.sendUpdate();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return isValid.test(slot, stack);
            }
        };
    }
}
