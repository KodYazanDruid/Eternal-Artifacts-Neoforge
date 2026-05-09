package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.api.machine.config.ToggleConfig;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.event.custom.GetMachineEnchantmentLevelEvent;
import com.sonamorningstar.eternalartifacts.util.StorageBuilder;
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
    
    public void registerConfigs() {
        if (this instanceof WorkingAreaProvider) configuration.add(new ToggleConfig("render_area"));
    }
    
    public void registerCapabilityConfigs(BlockCapability<?,?> cap) {}
    
    protected boolean shouldSyncOnUpdate() {
        return false;
    }

    public void findRecipe() {}

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
    public void onLoad() {
        super.onLoad();
        enchantments.forEach(this::onEnchanted);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("Enchantments")) {
            enchantments.clear();
            var enchs = EnchantmentHelper.deserializeEnchantments(tag.getList("Enchantments", Tag.TAG_COMPOUND));
            if (!enchs.isEmpty()) enchantments.putAll(enchs);
        }
        CompoundTag configTag = tag.getCompound(CONFIG_TAG_KEY);
        configuration.load(configTag);
        if (this instanceof Filterable filterable) {
            filterable.loadFilters(tag);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!enchantments.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Object2IntMap.Entry<Enchantment> entry : enchantments.object2IntEntrySet()) {
                CompoundTag enchTag = EnchantmentHelper.storeEnchantment(
                    EnchantmentHelper.getEnchantmentId(entry.getKey()), entry.getIntValue()
                );
                listTag.add(enchTag);
            }
            tag.put("Enchantments", listTag);
        }
        CompoundTag configTag = new CompoundTag();
        configuration.save(configTag);
        tag.put(CONFIG_TAG_KEY, configTag);
        if (this instanceof Filterable filterable) {
            filterable.saveFilters(tag);
        }
    }
    
    public void loadConfiguration(ItemStack drive) {
        if (drive.hasTag()){
            getConfiguration().load(drive.getTag().getCompound(CONFIG_TAG_KEY));
            sendUpdate();
        }
    }
    
    public void sendUpdate() {
        setChanged();
        if(level != null && !isRemoved() && level.hasChunkAt(worldPosition))
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }
    
    public void enchant(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        onEnchanted(enchantment, level);
        sendUpdate();
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

    protected ModFluidStorage createDefaultTank() {
        return StorageBuilder.fluidStorage(this, 16000).build();
    }

    protected ModFluidStorage createBasicTank(int size, Runnable... listeners) {
        return StorageBuilder.fluidStorage(this, size)
                .listeners(listeners)
                .build();
    }

    protected ModFluidStorage createRecipeFinderTank(int size) {
        return StorageBuilder.fluidStorage(this, size)
                .recipeFinder(true)
                .build();
    }

    protected ModFluidStorage createBasicTank(int size, boolean canDrain, boolean canFill, Runnable... listeners) {
        return StorageBuilder.fluidStorage(this, size)
                .canDrain(canDrain)
                .canFill(canFill)
                .listeners(listeners)
                .build();
    }

    protected ModFluidStorage createRecipeFinderTank(int size, boolean canDrain, boolean canFill) {
        return StorageBuilder.fluidStorage(this, size)
                .recipeFinder(true)
                .canDrain(canDrain)
                .canFill(canFill)
                .build();
    }

    protected ModFluidStorage createBasicTank(int size, Predicate<FluidStack> validator, boolean canDrain, boolean canFill, Runnable... run) {
        return StorageBuilder.fluidStorage(this, size)
                .validator(validator)
                .canDrain(canDrain)
                .canFill(canFill)
                .listeners(run)
                .build();
    }

    protected ModFluidStorage createRecipeFinderTank(int size, Predicate<FluidStack> validator, boolean canDrain, boolean canFill) {
        return StorageBuilder.fluidStorage(this, size)
                .recipeFinder(true)
                .validator(validator)
                .canDrain(canDrain)
                .canFill(canFill)
                .build();
    }

    protected ModEnergyStorage createDefaultEnergy() {
        return StorageBuilder.energyStorage(this, 50000, 2500)
                .canReceive(true)
                .canExtract(false)
                .build();
    }

    protected ModEnergyStorage createBasicEnergy(int size, int transfer, boolean canReceive, boolean canExtract) {
        return createBasicEnergy(size, transfer, transfer, canReceive, canExtract);
    }

    protected ModEnergyStorage createBasicEnergy(int size, int maxReceive, int maxExtract, boolean canReceive, boolean canExtract) {
        return StorageBuilder.energyStorage(this, size)
                .maxTransfer(maxReceive, maxExtract)
                .canReceive(canReceive)
                .canExtract(canExtract)
                .build();
    }

    protected final ModItemStorage createBasicInventory(int size, boolean canInsert, IntConsumer... consumers) {
        return StorageBuilder.itemStorage(this, size)
                .canInsert(canInsert)
                .consumers(consumers)
                .build();
    }
    
    protected final ModItemStorage createBasicInventory(int size, List<Integer> outputSlots, BiPredicate<Integer, ItemStack> validator, IntConsumer... consumers) {
        return StorageBuilder.itemStorage(this, size)
                .outputSlots(outputSlots)
                .validator(validator)
                .consumers(consumers)
                .build();
    }

    protected final ModItemStorage createRecipeFinderInventory(int size, List<Integer> outputSlots) {
        return StorageBuilder.itemStorage(this, size)
                .recipeFinder(true)
                .outputSlots(outputSlots)
                .build();
    }

    protected ModItemStorage createBasicInventory(int size, BiPredicate<Integer, ItemStack> isValid) {
        return createBasicInventory(size, isValid, limit -> 64);
    }

    protected ModItemStorage createBasicInventory(int size, BiPredicate<Integer, ItemStack> isValid, Int2IntFunction slotLimit) {
        return StorageBuilder.itemStorage(this, size)
                .validator(isValid)
                .slotLimit(slotLimit)
                .build();
    }

    protected ModItemStorage createRecipeFinderInventory(int size, BiPredicate<Integer, ItemStack> isValid) {
        return StorageBuilder.itemStorage(this, size)
                .recipeFinder(true)
                .validator(isValid)
                .build();
    }
}
