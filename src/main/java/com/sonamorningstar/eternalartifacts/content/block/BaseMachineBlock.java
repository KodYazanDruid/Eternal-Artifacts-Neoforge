package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.container.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableClient;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableServer;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class BaseMachineBlock<T extends MachineBlockEntity<?>> extends BaseEntityBlock {
    private final BiFunction<BlockPos, BlockState, T> fun;
    protected BaseMachineBlock(Properties pProperties, BiFunction<BlockPos, BlockState, T> fun) {
        super(pProperties);
        this.fun = fun;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof MachineBlockEntity machine) {
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
        if (level.isClientSide) {
            return (lvl, pos, st, be) -> {
                if (be instanceof ITickableClient entity) {
                    entity.tickClient(lvl, pos, st);
                }
            };
        } else {
            return (lvl, pos, st, be) -> {
                if (be instanceof ITickableServer entity) {
                    entity.tickServer(lvl, pos, st);
                }
            };
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if(entity instanceof MenuProvider) {
                IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
                if(fluidHandler == null) AbstractMachineMenu.openContainer((ServerPlayer) player, pos);
                else if(!FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) AbstractMachineMenu.openContainer((ServerPlayer) player, pos);
            }else {
                throw new IllegalStateException("Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return fun.apply(pPos, pState);
    }
}
