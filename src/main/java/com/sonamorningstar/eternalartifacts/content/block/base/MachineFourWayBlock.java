package com.sonamorningstar.eternalartifacts.content.block.base;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class MachineFourWayBlock<T extends Machine<? extends AbstractMachineMenu>> extends BaseMachineBlock<T> {

    public MachineFourWayBlock(Properties pProperties, BlockEntityType.BlockEntitySupplier<T> supplier) {
        super(pProperties, supplier);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
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
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(BlockStateProperties.HORIZONTAL_FACING, pRotation.rotate(pState.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }
    
    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

}
