package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrashCanBlock extends Block {
	private static final VoxelShape BODY = BlockHelper.generateByArea(10, 14, 10, 3, 0, 3);
	private static final VoxelShape LID_1 = BlockHelper.generateByArea(12, 1, 12, 2, 14, 2);
	private static final VoxelShape LID_2 = BlockHelper.generateByArea(8, 1, 8, 4, 15, 4);
	
	public TrashCanBlock(Properties props) {
		super(props);
	}
	
	@Override
	public void onPlace(BlockState pState, Level level, BlockPos pos, BlockState pOldState, boolean pMovedByPiston) {
		super.onPlace(pState, level, pos, pOldState, pMovedByPiston);
		level.invalidateCapabilities(pos);
	}
	
	@Override
	public void onRemove(BlockState pState, Level level, BlockPos pos, BlockState pNewState, boolean pMovedByPiston) {
		super.onRemove(pState, level, pos, pNewState, pMovedByPiston);
		level.invalidateCapabilities(pos);
	}
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		VoxelShape lid = Shapes.join(LID_1, LID_2, BooleanOp.OR);
		return Shapes.join(BODY, lid, BooleanOp.OR);
	}
}
