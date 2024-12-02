package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.content.block.entity.EnergyDockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableServer;
import com.sonamorningstar.eternalartifacts.content.block.properties.DockPart;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class EnergyDockBlock extends BaseEntityBlock {
    public static final EnumProperty<DockPart> DOCK_PART = EnumProperty.create("dock_part", DockPart.class);
    private static final VoxelShape SHAPE = BlockHelper.generateByArea(16, 4, 16, 0, 0, 0);

    public EnergyDockBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(DOCK_PART, DockPart.CENTER));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(EnergyDockBlock::new);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyDockBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DOCK_PART);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neigState, LevelAccessor level, BlockPos pos, BlockPos neigPos) {
        DockPart dockPart = state.getValue(DOCK_PART);
        if (dir.getAxis().isHorizontal() && !shouldIgnoreDir(dockPart, dir)) {
            return neigState.getBlock() instanceof EnergyDockBlock && isInDock(dockPart, neigState.getValue(DOCK_PART))
                    ? state : Blocks.AIR.defaultBlockState();
        } else return super.updateShape(state, dir, neigState, level, pos, neigPos);
    }

    private boolean shouldIgnoreDir(DockPart dockPart, Direction dir) {
        return switch (dockPart) {
            case NORTH_WEST -> dir == Direction.NORTH || dir == Direction.WEST;
            case NORTH -> dir == Direction.NORTH;
            case NORTH_EAST -> dir == Direction.NORTH || dir == Direction.EAST;
            case WEST -> dir == Direction.WEST;
            case CENTER -> false;
            case EAST -> dir == Direction.EAST;
            case SOUTH_WEST -> dir == Direction.SOUTH || dir == Direction.WEST;
            case SOUTH -> dir == Direction.SOUTH;
            case SOUTH_EAST -> dir == Direction.SOUTH || dir == Direction.EAST;
        };
    }

    private boolean isInDock(DockPart dockPart, DockPart neigDockPart) {
        return switch (dockPart) {
            case NORTH_WEST -> neigDockPart == DockPart.NORTH || neigDockPart == DockPart.WEST;
            case NORTH -> neigDockPart == DockPart.NORTH_WEST || neigDockPart == DockPart.NORTH_EAST || neigDockPart == DockPart.CENTER;
            case NORTH_EAST -> neigDockPart == DockPart.NORTH || neigDockPart == DockPart.EAST;
            case WEST -> neigDockPart == DockPart.NORTH_WEST || neigDockPart == DockPart.SOUTH_WEST || neigDockPart == DockPart.CENTER;
            case CENTER -> neigDockPart == DockPart.NORTH || neigDockPart == DockPart.WEST || neigDockPart == DockPart.EAST || neigDockPart == DockPart.SOUTH;
            case EAST -> neigDockPart == DockPart.NORTH_EAST || neigDockPart == DockPart.SOUTH_EAST || neigDockPart == DockPart.CENTER;
            case SOUTH_WEST -> neigDockPart == DockPart.SOUTH || neigDockPart == DockPart.WEST;
            case SOUTH -> neigDockPart == DockPart.SOUTH_WEST || neigDockPart == DockPart.SOUTH_EAST || neigDockPart == DockPart.CENTER;
            case SOUTH_EAST -> neigDockPart == DockPart.SOUTH || neigDockPart == DockPart.EAST;
        };
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && (player.isCreative() || !player.hasCorrectToolForDrops(state))) {
            preventDropsFromSides(level, pos, state, player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    protected static void preventDropsFromSides(Level level, BlockPos pos, BlockState state, Player player) {
        DockPart dockPart = state.getValue(DOCK_PART);
        if (dockPart == DockPart.CENTER) {
            BlockPos nw = pos.north().west();
            BlockPos n = pos.north();
            BlockPos ne = pos.north().east();
            BlockPos w = pos.west();
            BlockPos e = pos.east();
            BlockPos sw = pos.south().west();
            BlockPos s = pos.south();
            BlockPos se = pos.south().east();
            BlockState nwState = level.getBlockState(nw);
            BlockState nState = level.getBlockState(n);
            BlockState neState = level.getBlockState(ne);
            BlockState wState = level.getBlockState(w);
            BlockState eState = level.getBlockState(e);
            BlockState swState = level.getBlockState(sw);
            BlockState sState = level.getBlockState(s);
            BlockState seState = level.getBlockState(se);
            if (isStateCorrect(nwState, DockPart.NORTH_WEST) &&
                isStateCorrect(nState, DockPart.NORTH) &&
                isStateCorrect(neState, DockPart.NORTH_EAST) &&
                isStateCorrect(wState, DockPart.WEST) &&
                isStateCorrect(eState, DockPart.EAST) &&
                isStateCorrect(swState, DockPart.SOUTH_WEST) &&
                isStateCorrect(sState, DockPart.SOUTH) &&
                isStateCorrect(seState, DockPart.SOUTH_EAST)) {
                level.setBlock(nw, Blocks.AIR.defaultBlockState(), 35);
                level.setBlock(n, Blocks.AIR.defaultBlockState(), 35);
                level.setBlock(ne, Blocks.AIR.defaultBlockState(), 35);
                level.setBlock(w, Blocks.AIR.defaultBlockState(), 35);
                level.setBlock(e, Blocks.AIR.defaultBlockState(), 35);
                level.setBlock(sw, Blocks.AIR.defaultBlockState(), 35);
                level.setBlock(s, Blocks.AIR.defaultBlockState(), 35);
                level.setBlock(se, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, nw, Block.getId(nwState));
                level.levelEvent(player, 2001, n, Block.getId(nState));
                level.levelEvent(player, 2001, ne, Block.getId(neState));
                level.levelEvent(player, 2001, w, Block.getId(wState));
                level.levelEvent(player, 2001, e, Block.getId(eState));
                level.levelEvent(player, 2001, sw, Block.getId(swState));
                level.levelEvent(player, 2001, s, Block.getId(sState));
                level.levelEvent(player, 2001, se, Block.getId(seState));

            }

        }
    }

    private static boolean isStateCorrect(BlockState state, DockPart dockPart) {
        return state.getBlock() instanceof EnergyDockBlock && state.getValue(DOCK_PART) == dockPart;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        boolean isPrevented = false;
        for (BlockPos p : BlockPos.betweenClosed(pos.north().west(), pos.south().east())) {
            BlockState st = level.getBlockState(p);
            if (!isPrevented && !st.canBeReplaced(ctx)) isPrevented = true;
        }
        if (!isPrevented) {
            return defaultBlockState().setValue(DOCK_PART, DockPart.CENTER);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        for (BlockPos p : BlockPos.betweenClosed(pos.north().west(), pos.south().east())) {
            level.setBlock(p, state.setValue(DOCK_PART,
                    DockPart.values()[DockPart.CENTER.ordinal() + p.getX() - pos.getX() + 3 * (p.getZ() - pos.getZ())]
            ), 3);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (!level.isClientSide && state.getBlock() instanceof EnergyDockBlock && state.getValue(DOCK_PART) == DockPart.CENTER) {
            return (lvl, pos, st, be) -> {
                if (be instanceof ITickableServer entity) {
                    entity.tickServer(lvl, pos, st);
                }
            };
        } else return null;
    }
}
