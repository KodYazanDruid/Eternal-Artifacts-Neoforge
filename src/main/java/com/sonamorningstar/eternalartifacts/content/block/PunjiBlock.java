package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PunjiBlock extends Block implements SimpleWaterloggedBlock {

    public PunjiBlock(Properties props) {
        super(props.forceSolidOn().pushReaction(PushReaction.DESTROY));
        registerDefaultState(getStateDefinition().any().setValue(STICKS, MIN_STICKS).setValue(WATERLOGGED, false));
    }

    public static final int MIN_STICKS = 1;
    public static final int MAX_STICKS = 5;
    public static final IntegerProperty STICKS = IntegerProperty.create("sticks", 1, 5);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape ONE = BlockHelper.generateByArea(2, 4, 2, 7, 0, 7);
    private static final VoxelShape TWO = BlockHelper.generateByArea(4, 4, 4, 6, 0, 6);
    private static final VoxelShape THREE = BlockHelper.generateByArea(6, 4, 6, 5, 0, 5);
    private static final VoxelShape FOUR = BlockHelper.generateByArea(8, 4, 8, 4, 0, 4);
    private static final VoxelShape FIVE = BlockHelper.generateByArea(10, 4, 10, 3, 0, 3);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(STICKS)) {
            case 1 -> ONE;
            case 2 -> TWO;
            case 3 -> THREE;
            case 4 -> FOUR;
            case 5 -> FIVE;
            default -> BlockHelper.generateByArea(1, 1, 1, 0, 0, 0);
        };
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        int sticks = state.getValue(STICKS);
        if(entity instanceof LivingEntity) {
            float slowness = 0.1F + (sticks * 0.05F);
            entity.makeStuckInBlock(state, new Vec3(slowness, 1.0F, slowness));
            entity.hurt(level.damageSources().stalagmite(), sticks * 0.5F);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(STICKS, WATERLOGGED); }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if(state.is(this)) {
            return state.cycle(STICKS);
        } else {
            FluidState fluidstate = ctx.getLevel().getFluidState(ctx.getClickedPos());
            return defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        }
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
        return !ctx.isSecondaryUseActive() && ctx.getItemInHand().getItem() == this.asItem() && state.getValue(STICKS) < MAX_STICKS || super.canBeReplaced(state, ctx);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        return super.updateShape(state, dir, neighbor, levelAccessor, pos, neighborPos);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {return RenderShape.MODEL;}

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) { return Block.canSupportCenter(pLevel, pPos.below(), Direction.UP); }

    @Override
    protected MapCodec<? extends PunjiBlock> codec() { return simpleCodec(PunjiBlock::new); }

    @Override
    public float getShadeBrightness(BlockState p_308911_, BlockGetter p_308952_, BlockPos p_308918_) {return 1.0F;}

    @Override
    public boolean propagatesSkylightDown(BlockState p_309084_, BlockGetter p_309133_, BlockPos p_309097_) { return true; }
}
