package com.sonamorningstar.eternalartifacts.content.block.base;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class MachineFourWayBlock<T extends MachineBlockEntity<?>> extends BaseMachineBlock<T> {

    public MachineFourWayBlock(Properties pProperties, BiFunction<BlockPos, BlockState, T> fun) {
        super(pProperties, fun);
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

}
