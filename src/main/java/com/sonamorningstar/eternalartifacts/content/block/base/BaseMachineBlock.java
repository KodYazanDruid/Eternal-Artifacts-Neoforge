package com.sonamorningstar.eternalartifacts.content.block.base;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableClient;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableServer;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class BaseMachineBlock<T extends MachineBlockEntity<?>> extends BaseEntityBlock {
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
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof MachineBlockEntity<?> machine) {
                IItemHandler inventory = level.getCapability(Capabilities.ItemHandler.BLOCK, machine.getBlockPos(), machine.getBlockState(), machine, null);
                if(inventory != null) {
                    SimpleContainer container = new SimpleContainer(inventory.getSlots());
                    for (int i = 0; i < inventory.getSlots(); i++) {
                        container.setItem(i, inventory.getStackInSlot(i));
                    }
                    Containers.dropContents(level, pos.immutable(), container);
                }
                machine.invalidateCapabilities();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Nullable
    @Override
    public <B extends BlockEntity> BlockEntityTicker<B> getTicker(Level level, BlockState pState, BlockEntityType<B> pBlockEntityType) {
        return new SimpleTicker<>(level.isClientSide());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        if (fluidHandler != null && FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) return InteractionResult.sidedSuccess(level.isClientSide());
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MachineBlockEntity<?> mbe && mbe.canConstructMenu()) {
            AbstractMachineMenu.openContainer(player, pos);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return supplier.create(pPos, pState);
    }

    private record SimpleTicker<B extends BlockEntity>(boolean isRemote) implements BlockEntityTicker<B> {
        @Override
        public void tick(Level lvl, BlockPos pos, BlockState st, B be) {
            if (isRemote) {
                if (be instanceof ITickableClient en) en.tickClient(lvl, pos, st);
            } else if (be instanceof ITickableServer en) en.tickServer(lvl, pos, st);

        }
    }
}
