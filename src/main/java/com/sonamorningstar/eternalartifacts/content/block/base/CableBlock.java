package com.sonamorningstar.eternalartifacts.content.block.base;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.CableBlockEntity;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CableBlock extends BaseEntityBlock {
    public CableBlock(Properties props) {
        super(props);
    }

    private static final VoxelShape SHAPE = BlockHelper.generateByArea(6, 6, 6, 5, 5, 5);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {return SHAPE;}

    @Override
    public RenderShape getRenderShape(BlockState pState) {return RenderShape.MODEL;}

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {return simpleCodec(CableBlock::new);}

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return new CableBlockEntity(pos, state);}

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> beType) {
        return level.isClientSide() ? null : (lvl, pos, st, be) -> {
            if (be instanceof CableBlockEntity cable) cable.tickServer(lvl, pos, st);
        };
    }
}
