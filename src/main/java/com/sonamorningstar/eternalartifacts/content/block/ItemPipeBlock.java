package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.AttachmentablePipeBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.ItemPipe;
import com.sonamorningstar.eternalartifacts.content.block.properties.PipeConnectionProperty;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.IExtensibleEnum;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

@Getter
public class ItemPipeBlock extends AttachmentablePipeBlock<IItemHandler> {
	
	private final PipeTier tier;
	private static final VoxelShape SHAPE = BlockHelper.generateByArea(8, 8, 8, 4, 4, 4);
	private static final VoxelShape SHAPE_NORTH = BlockHelper.generateByArea(8, 8, 4, 4, 4, 0);
	private static final VoxelShape SHAPE_SOUTH = BlockHelper.generateByArea(8, 8, 4, 4, 4, 12);
	private static final VoxelShape SHAPE_EAST = BlockHelper.generateByArea(4, 8, 8, 12, 4, 4);
	private static final VoxelShape SHAPE_WEST = BlockHelper.generateByArea(4, 8, 8, 0, 4, 4);
	private static final VoxelShape SHAPE_UP = BlockHelper.generateByArea(8, 4, 8, 4, 12, 4);
	private static final VoxelShape SHAPE_DOWN = BlockHelper.generateByArea(8, 4, 8, 4, 0, 4);
	
	public ItemPipeBlock(PipeTier tier, Properties props) {
		super(IItemHandler.class, props);
		this.tier = tier;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		VoxelShape joinedShape = SHAPE;
		PipeConnectionProperty.PipeConnection isNorth = state.getValue(NORTH_CONNECTION);
		PipeConnectionProperty.PipeConnection isEast = state.getValue(EAST_CONNECTION);
		PipeConnectionProperty.PipeConnection isSouth = state.getValue(SOUTH_CONNECTION);
		PipeConnectionProperty.PipeConnection isWest = state.getValue(WEST_CONNECTION);
		PipeConnectionProperty.PipeConnection isUp = state.getValue(UP_CONNECTION);
		PipeConnectionProperty.PipeConnection isDown = state.getValue(DOWN_CONNECTION);
		
		if (isNorth != PipeConnectionProperty.PipeConnection.NONE) joinedShape = Shapes.or(joinedShape, SHAPE_NORTH);
		if (isEast != PipeConnectionProperty.PipeConnection.NONE) joinedShape = Shapes.or(joinedShape, SHAPE_EAST);
		if (isSouth != PipeConnectionProperty.PipeConnection.NONE) joinedShape = Shapes.or(joinedShape, SHAPE_SOUTH);
		if (isWest != PipeConnectionProperty.PipeConnection.NONE) joinedShape = Shapes.or(joinedShape, SHAPE_WEST);
		if (isUp != PipeConnectionProperty.PipeConnection.NONE) joinedShape = Shapes.or(joinedShape, SHAPE_UP);
		if (isDown != PipeConnectionProperty.PipeConnection.NONE) joinedShape = Shapes.or(joinedShape, SHAPE_DOWN);
		
		return joinedShape;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) { return new ItemPipe(pPos, pState); }
	
	@Override
	protected boolean checkPipe(BlockState relativeState) {
		return relativeState.getBlock() instanceof ItemPipeBlock o && o.getTier() == tier;
	}
	
	@Getter
	public enum PipeTier implements IExtensibleEnum {
		COPPER(16, 1),
		GOLD(32, 4);
		
		private final int maxConnections;
		private final int transferRate;
		
		PipeTier(int maxConnections, int transferRate) {
			this.maxConnections = maxConnections;
			this.transferRate = transferRate;
		}
		
		public static PipeTier create(String name, int maxConnections, int transferRate) {
			throw new IllegalStateException("Enum not extended");
		}
	}
}
