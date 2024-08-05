package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.OilRefineryBlockEntity;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OilRefineryBlock extends MachineFourWayBlock<OilRefineryBlockEntity> {
    public OilRefineryBlock(Properties pProperties) {
        super(pProperties, OilRefineryBlockEntity::new);
    }

    private static final VoxelShape BOTTOM = BlockHelper.generateByArea(16, 9, 16, 0, 0, 0);
    private static final VoxelShape UP_NORTH = BlockHelper.generateByArea(16, 7, 7, 0, 9, 9);
    private static final VoxelShape UP_SOUTH = BlockHelper.generateByArea(16, 7, 7, 0, 9, 0);
    private static final VoxelShape UP_EAST = BlockHelper.generateByArea(7, 7, 16, 0, 9, 0);
    private static final VoxelShape UP_WEST = BlockHelper.generateByArea(7, 7, 16, 9, 9, 0);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        VoxelShape shape;
        Direction direction = pState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        switch (direction) {
            case SOUTH -> shape = Shapes.or(BOTTOM, UP_SOUTH);
            case EAST -> shape = Shapes.or(BOTTOM, UP_EAST);
            case WEST -> shape = Shapes.or(BOTTOM, UP_WEST);
            default -> shape = Shapes.or(BOTTOM, UP_NORTH);
        }
        return shape;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_309084_, BlockGetter p_309133_, BlockPos p_309097_) {
        return true;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }
}
