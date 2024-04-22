package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.content.block.entity.ResonatorBlockEntity;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ResonatorBlock extends BaseEntityBlock {
    int generateRate;
    public ResonatorBlock(Properties pProperties, int generateRate) {
        super(pProperties);
        this.generateRate = generateRate;
    }

    private static final VoxelShape UP_AABB;
    private static final VoxelShape DOWN_AABB;
    private static final VoxelShape NORTH_AABB;
    private static final VoxelShape EAST_AABB;
    private static final VoxelShape WEST_AABB;
    private static final VoxelShape SOUTH_AABB;

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

    static {
        UP_AABB = BlockHelper.generateByArea(8, 4, 8, 4, 12, 4);
        DOWN_AABB = BlockHelper.generateByArea(8, 4, 8, 4, 0, 4);
        NORTH_AABB = BlockHelper.generateByArea(8, 8, 4, 4, 4, 0);
        EAST_AABB = BlockHelper.generateByArea(4, 8, 8, 12, 4, 4);
        WEST_AABB = BlockHelper.generateByArea(4, 8, 8, 0, 4, 4);
        SOUTH_AABB = BlockHelper.generateByArea(8, 8, 4, 4, 4, 12);
    }

    @Override
    protected MapCodec<ResonatorBlock> codec() {return simpleCodec( p -> new ResonatorBlock(p, generateRate));}

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ResonatorBlockEntity(blockPos, blockState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.invalidateCapabilities(pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        level.invalidateCapabilities(pos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(BlockStateProperties.FACING, pContext.getClickedFace().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(BlockStateProperties.FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide) {
            return null;
        } else {
            return (lvl, pos, st, be) -> {
                if(be instanceof ResonatorBlockEntity entity) {
                    entity.tick(lvl, pos, st, generateRate);
                }
            };
        }
    }
}
