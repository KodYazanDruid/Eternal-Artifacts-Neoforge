package com.sonamorningstar.eternalartifacts.content.block.base;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModItemEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.MultiFluidTank;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableClient;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableServer;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.content.item.WrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

public class BaseMachineBlock<T extends Machine<?>> extends BaseEntityBlock {
    private final BlockEntityType.BlockEntitySupplier<T> supplier;
    public BaseMachineBlock(Properties pProperties, BlockEntityType.BlockEntitySupplier<T> fun) {
        super(pProperties);
        this.supplier = fun;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
    
    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof Machine<?> machine) {
            int resistance = machine.getEnchantmentLevel(Enchantments.BLAST_PROTECTION) + 1;
            return super.getExplosionResistance(state, level, pos, explosion) * resistance;
        }
        return super.getExplosionResistance(state, level, pos, explosion);
    }
    
    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof Machine<?> machine) {
            int resistance = machine.getEnchantmentLevel(Enchantments.BLAST_PROTECTION) + 1;
            if ((entity instanceof WitherBoss || entity instanceof EnderDragon) && resistance >= 4) return false;
        }
        return super.canEntityDestroy(state, level, pos, entity);
    }
    
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof Machine<?> machine) {
            if (!(player.getMainHandItem().getItem() instanceof WrenchItem)) {
                IItemHandler inventory = level.getCapability(Capabilities.ItemHandler.BLOCK, machine.getBlockPos(), machine.getBlockState(), machine, null);
                if(inventory != null) {
                    SimpleContainer container = new SimpleContainer(inventory.getSlots());
                    for (int i = 0; i < inventory.getSlots(); i++) {
                        container.setItem(i, inventory.getStackInSlot(i));
                    }
                    Containers.dropContents(level, pos.immutable(), container);
                }
            }
            machine.invalidateCapabilities();
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Nullable
    @Override
    public <B extends BlockEntity> BlockEntityTicker<B> getTicker(Level level, BlockState pState, BlockEntityType<B> pBlockEntityType) {
        return new SimpleTicker<>(level.isClientSide());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof Machine<?> mbe && mbe.canConstructMenu()) {
            return mbe.use(state, level, pos, player, hand, hit);
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return supplier.create(pPos, pState);
    }

    public record SimpleTicker<B extends BlockEntity>(boolean isRemote) implements BlockEntityTicker<B> {
        @Override
        public void tick(Level lvl, BlockPos pos, BlockState st, B be) {
            if (isRemote) {
                if (be instanceof ITickableClient en) en.tickClient(lvl, pos, st);
            } else if (be instanceof ITickableServer en) en.tickServer(lvl, pos, st);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        Block block = state.getBlock();
        Level actualLevel = level.getBlockEntity(pos) != null ? level.getBlockEntity(pos).getLevel() : null;
        FluidStack fs = FluidStack.EMPTY;
        ItemStack stack = new ItemStack(block);
        if (actualLevel != null && stack.hasTag()) {
            BlockEntity be = actualLevel.getBlockEntity(pos);
            if (be instanceof ModBlockEntity mbe) {
                mbe.loadEnchants(stack.getEnchantmentTags());
            }
            
            IFluidHandler fluidHandler = actualLevel.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
            if (fluidHandler != null) {
                fs = fluidHandler.getFluidInTank(0);
            }

            Optional<IFluidHandlerItem> optionalTankStack = FluidUtil.getFluidHandler(stack);
            if (optionalTankStack.isPresent()) optionalTankStack.get().fill(fs, IFluidHandler.FluidAction.EXECUTE);

            IEnergyStorage energy = actualLevel.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null);
            if (energy != null) {
                IEnergyStorage energyStack = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                if (energyStack != null) {
                    if (energyStack instanceof ModItemEnergyStorage mes) mes.setEnergy(energy.getEnergyStored());
                    else energyStack.receiveEnergy(energy.getEnergyStored(), false);
                }
            }
        }
        return stack;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof Machine<?> mbe && stack.hasTag()) {
            mbe.loadEnchants(stack.getEnchantmentTags());
            for (Map.Entry<Enchantment, Integer> entry : mbe.enchantments.object2IntEntrySet()) {
                mbe.onEnchanted(entry.getKey(), entry.getValue());
            }
        }
        Optional<IFluidHandlerItem> optionalTankStack = FluidUtil.getFluidHandler(stack);
        if (optionalTankStack.isPresent()) {
            IFluidHandlerItem tankStack = optionalTankStack.get();
            Optional<IFluidHandler> optionalTank = FluidUtil.getFluidHandler(level, pos, null);
            for (int i = 0; i < tankStack.getTanks(); i++) {
                FluidStack fluidStack = tankStack.getFluidInTank(i);
                if (optionalTank.isPresent()) {
                    IFluidHandler tank = optionalTank.get();
                    if (i < tank.getTanks()) {
                        if (tank instanceof MultiFluidTank<?> mft) mft.setFluid(fluidStack, i);
                        else if (i == 0) {
                            if (tank instanceof ModFluidStorage mfs) mfs.setFluid(fluidStack, 0);
                            else tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                }
            }
        }
        IEnergyStorage energyStack = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energyStack != null) {
            IEnergyStorage energy = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null);
            if (energy != null) {
                if (energy instanceof ModEnergyStorage mes) mes.setEnergy(energyStack.getEnergyStored());
                else energy.receiveEnergy(energyStack.getEnergyStored(), false);
            }
        }
        IItemHandler invStack = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (invStack != null) {
            IItemHandler inv = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
            if (inv != null) {
                for (int i = 0; i < invStack.getSlots(); i++) {
                    if (i < inv.getSlots()) {
                        ItemStack stackInSlot = invStack.getStackInSlot(i);
                        if (inv instanceof IItemHandlerModifiable mis) mis.setStackInSlot(i, stackInSlot);
                        else inv.insertItem(i, stackInSlot, false);
                    }
                }
            }
        }

        if (be instanceof Machine<?> mbe && stack.hasTag()) {
            CompoundTag nbt = stack.getTag();
            if (nbt.contains("MachineData")) {
                mbe.loadContents(nbt.getCompound("MachineData"));
            }
        }
    }
}
