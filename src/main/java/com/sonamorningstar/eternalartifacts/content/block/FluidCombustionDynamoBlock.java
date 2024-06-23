package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.entity.FluidCombustionDynamoBlockEntity;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FluidCombustionDynamoBlock extends BaseMachineBlock<FluidCombustionDynamoBlockEntity>{
    public FluidCombustionDynamoBlock(Properties pProperties) {
        super(pProperties, FluidCombustionDynamoBlockEntity::new);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    private static final VoxelShape UP_AABB;
    private static final VoxelShape DOWN_AABB;
    private static final VoxelShape NORTH_AABB;
    private static final VoxelShape EAST_AABB;
    private static final VoxelShape WEST_AABB;
    private static final VoxelShape SOUTH_AABB;

    static {
        UP_AABB = BlockHelper.generateByArea(16, 8, 16, 0, 0, 0);
        DOWN_AABB = BlockHelper.generateByArea(16, 8, 16, 0, 8, 0);
        NORTH_AABB = BlockHelper.generateByArea(16, 16, 8, 0, 0, 8);
        EAST_AABB = BlockHelper.generateByArea(8, 16, 16, 0, 0, 0);
        WEST_AABB = BlockHelper.generateByArea(8, 16, 16, 8, 0, 0);
        SOUTH_AABB = BlockHelper.generateByArea(16, 16, 8, 0, 0, 0);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pState.getValue(BlockStateProperties.FACING);
        VoxelShape shape;
        switch (direction) {
            case UP -> shape = UP_AABB;
            case DOWN -> shape = DOWN_AABB;
            case EAST -> shape = EAST_AABB;
            case WEST -> shape = WEST_AABB;
            case SOUTH -> shape = SOUTH_AABB;
            default -> shape = NORTH_AABB;
        }
        return shape;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(BlockStateProperties.FACING, ctx.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(BlockStateProperties.FACING);
    }
}
