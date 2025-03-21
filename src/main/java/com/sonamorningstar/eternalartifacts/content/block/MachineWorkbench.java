package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.MachineWorkbenchBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModProperties;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MachineWorkbench extends MachineFourWayBlock<MachineWorkbenchBlockEntity> {
	public MachineWorkbench() {
		super(ModProperties.Blocks.MACHINE, MachineWorkbenchBlockEntity::new);
	}
	private static final VoxelShape TOP = BlockHelper.generateByArea(16, 2, 16, 0, 14, 0);
	private static final VoxelShape LEG1 = BlockHelper.generateByArea(2, 14, 2, 1, 0, 1);
	private static final VoxelShape LEG2 = BlockHelper.generateByArea(2, 14, 2, 1, 0, 13);
	private static final VoxelShape LEG3 = BlockHelper.generateByArea(2, 14, 2, 13, 0, 1);
	private static final VoxelShape LEG4 = BlockHelper.generateByArea(2, 14, 2, 13, 0, 13);
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return Shapes.or(TOP, LEG1, LEG2, LEG3, LEG4);
	}
}
