package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.MachineSixWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DynamoBlock<T extends AbstractDynamo<?>> extends MachineSixWayBlock<T> {
    
    public DynamoBlock(Properties pProperties, BlockEntityType.BlockEntitySupplier<T> fun) {
        super(pProperties, fun);
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
    private static final VoxelShape UP_COIL_AABB;
    private static final VoxelShape DOWN_COIL_AABB;
    private static final VoxelShape NORTH_COIL_AABB;
    private static final VoxelShape EAST_COIL_AABB;
    private static final VoxelShape WEST_COIL_AABB;
    private static final VoxelShape SOUTH_COIL_AABB;

    static {
        UP_AABB = BlockHelper.generateByArea(16, 12, 16, 0, 0, 0);
        DOWN_AABB = BlockHelper.generateByArea(16, 12, 16, 0, 4, 0);
        NORTH_AABB = BlockHelper.generateByArea(16, 16, 12, 0, 0, 4);
        EAST_AABB = BlockHelper.generateByArea(12, 16, 16, 0, 0, 0);
        WEST_AABB = BlockHelper.generateByArea(12, 16, 16, 4, 0, 0);
        SOUTH_AABB = BlockHelper.generateByArea(16, 16, 12, 0, 0, 0);
        UP_COIL_AABB = BlockHelper.generateByArea(6, 4, 6, 5, 12, 5);
        DOWN_COIL_AABB = BlockHelper.generateByArea(6, 4, 6, 5, 0, 5);
        NORTH_COIL_AABB = BlockHelper.generateByArea(6, 6, 4, 5, 5,0);
        EAST_COIL_AABB = BlockHelper.generateByArea(4, 6, 6, 12, 5, 5);
        WEST_COIL_AABB = BlockHelper.generateByArea(4, 6, 6, 0, 5, 5);
        SOUTH_COIL_AABB = BlockHelper.generateByArea(6, 6, 4, 5, 5, 12);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pState.getValue(BlockStateProperties.FACING);
        VoxelShape shape;
        switch (direction) {
            case UP -> shape = Shapes.or(UP_AABB, UP_COIL_AABB);
            case DOWN -> shape =Shapes.or(DOWN_AABB, DOWN_COIL_AABB);
            case EAST -> shape = Shapes.or(EAST_AABB, EAST_COIL_AABB);
            case WEST -> shape = Shapes.or(WEST_AABB, WEST_COIL_AABB);
            case SOUTH -> shape = Shapes.or(SOUTH_AABB, SOUTH_COIL_AABB);
            default -> shape = Shapes.or(NORTH_AABB, NORTH_COIL_AABB);
        }
        return shape;
    }
}
