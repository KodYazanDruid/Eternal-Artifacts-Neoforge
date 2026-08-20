package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.base.InheritorRetexturedBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.PedestalBlockEntity;
import com.sonamorningstar.eternalartifacts.util.BlockEntityHelper;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PedestalBlock extends InheritorRetexturedBlock implements SimpleWaterloggedBlock {
	private static final VoxelShape TOP = BlockHelper.generateByArea(12, 2, 12, 2, 13, 2);
	private static final VoxelShape MIDDLE = BlockHelper.generateByArea(8, 11, 8, 4, 2, 4);
	private static final VoxelShape BOTTOM = BlockHelper.generateByArea(14, 2, 14, 1, 0, 1);
	
	public PedestalBlock() {
		super(Properties.of().destroyTime(1.2F).noOcclusion().mapColor(MapColor.COLOR_GRAY));
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
	}
	
	@Override
	protected MapCodec<? extends Block> codec() {return simpleCodec(p -> new PedestalBlock());}
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return Shapes.join(Shapes.join(TOP, BOTTOM, BooleanOp.OR), MIDDLE, BooleanOp.OR);
	}
	@Nullable
	@Override
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {return false;}
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return new PedestalBlockEntity(pos, state);}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack heldStack = player.getItemInHand(hand);
		Optional<PedestalBlockEntity> pedestalBlockEntity = BlockEntityHelper.get(PedestalBlockEntity.class, level, pos);
		if (pedestalBlockEntity.isPresent()) {
			PedestalBlockEntity pedestal = pedestalBlockEntity.get();
			ModItemStorage pedestalInv = pedestal.inventory;
			//Both items are different but at least one of them are not empty, so we swap them.
			if (!(heldStack.isEmpty() && pedestalInv.getStackInSlot(0).isEmpty()) &&
					!ItemStack.isSameItemSameTags(heldStack, pedestalInv.getStackInSlot(0))) {
				ItemStack copyHeld = heldStack.copy();
				ItemStack copyInv = pedestalInv.getStackInSlot(0).copy();
				player.setItemInHand(hand, copyInv);
				pedestalInv.setStackInSlot(0, copyHeld);
				SoundEvent sound = copyHeld.isEmpty() ? SoundEvents.ITEM_FRAME_ADD_ITEM :
					copyInv.isEmpty() ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ROTATE_ITEM;
				level.playSound(null, pos, sound, player.getSoundSource(), 1.0F, 1.0F);
				return InteractionResult.sidedSuccess(level.isClientSide());
			}
			//Both items are the same.
			ItemStack pedestalStack = pedestalInv.getStackInSlot(0);
			if (!heldStack.isEmpty() && !pedestalStack.isEmpty()) {
				// Hand -> Pedestal
				ItemStack copyHeld = heldStack.copy();
				ItemStack remainder = pedestalInv.insertItem(0, copyHeld, true);
				if (!ItemStack.matches(copyHeld, remainder)) {
					remainder = pedestalInv.insertItem(0, copyHeld, false);
					player.setItemInHand(hand, remainder);
					level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, player.getSoundSource(), 1.0F, 1.0F);
					return InteractionResult.sidedSuccess(level.isClientSide());
				}
				//If there were no actions do this instead.
				//Pedestal -> Hand
				heldStack = player.getItemInHand(hand);
				if (ItemStack.matches(heldStack, remainder)) {
					int emptySpace = Math.max(0, heldStack.getMaxStackSize() - heldStack.getCount());
					ItemStack extracted = pedestalInv.extractItem(0, emptySpace, false);
					player.setItemInHand(hand, ItemHandlerHelper.copyStackWithSize(heldStack, heldStack.getCount() + extracted.getCount()));
				}
				level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, player.getSoundSource(), 1.0F, 1.0F);
				return InteractionResult.sidedSuccess(level.isClientSide());
			}
		}
		return super.use(state, level, pos, player, hand, hit);
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		FluidState fluidstate = level.getFluidState(pos);
		return defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.WATERLOGGED);
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
		Optional<PedestalBlockEntity> optPedestal = BlockEntityHelper.get(PedestalBlockEntity.class, level, pos);
		if (optPedestal.isPresent()) {
			ModItemStorage inventory = optPedestal.get().inventory;
			ItemStack stored = inventory.getStackInSlot(0);
			return Mth.lerpDiscrete((float)stored.getCount() / (float)Math.min(inventory.getSlotLimit(0), stored.getMaxStackSize()), 0, 15);
		}
		return 0;
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		Optional<PedestalBlockEntity> optPedestal = BlockEntityHelper.get(PedestalBlockEntity.class, level, pos);
		if (optPedestal.isPresent()) {
			ModItemStorage inventory = optPedestal.get().inventory;
			Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(0));
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}
	
	
}
