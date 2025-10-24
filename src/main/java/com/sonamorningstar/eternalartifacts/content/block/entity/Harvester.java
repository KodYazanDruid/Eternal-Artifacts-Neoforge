package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.container.HarvesterMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AreaRenderer;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.ToolActions;
import net.neoforged.neoforge.common.util.FakePlayer;

public class Harvester extends SidedTransferMachine<HarvesterMenu> implements AreaRenderer {
	private AABB workingArea = null;
	public Harvester(BlockPos pos, BlockState blockState) {
		super(ModMachines.HARVESTER.getBlockEntity(), pos, blockState, (a, b, c, d) -> new HarvesterMenu(ModMachines.HARVESTER.getMenu(), a, b, c, d));
		setEnergy(this::createDefaultEnergy);
		setTank(this::createDefaultTank);
		for (int i = 9; i < 18; i++) {
			outputSlots.add(i);
		}
		// slot 18 is hoe slot
		setInventory(() -> createBasicInventory(19, (slot, stack) -> {
			if (slot == 18) return isHoe(stack);
			else if (outputSlots.contains(slot)) return false;
			else return canPlant(stack);
		}));
	}
	
	private static boolean isHoe(ItemStack stack) {
		return stack.canPerformAction(ToolActions.HOE_TILL);
	}
	
	private static boolean canPlant(ItemStack stack) {
		return stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof IPlantable;
	}
	
	@Override
	public boolean shouldRender() {
		return false;
	}
	
	@Override
	public AABB getWorkingArea() {
		BlockPos pos = getBlockPos();
		if (workingArea == null) {
			workingArea = AABB.encapsulatingFullBlocks(pos.relative(Direction.NORTH, 4).relative(Direction.WEST, 4),
				pos.relative(Direction.SOUTH, 4).relative(Direction.EAST, 4).above());
		}
		return workingArea;
	}
	
	public record PlantingOption(ItemStack seedStack, Direction direction, int slotIndex) {}
	
	public PlantingOption findSeedFor(Level lvl, BlockPos soilPos) {
		for (int i = 0; i < 9; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (canPlant(stack)) {
				BlockItem blockItem = (BlockItem) stack.getItem();
				IPlantable plantable = (IPlantable) blockItem.getBlock();
				BlockState soilState = lvl.getBlockState(soilPos);
				for (Direction value : Direction.values()) {
					BlockPos targetPos = soilPos.relative(value);
					if (soilState.canSustainPlant(lvl, targetPos, value.getOpposite(), plantable)) {
						return new PlantingOption(stack, value, i);
					}
				}
			}
		}
		return new PlantingOption(ItemStack.EMPTY, Direction.UP, -1);
		
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoInputFluids(lvl, pos);
		ItemStack hoeStack = inventory.getStackInSlot(18);
		BlockPos.betweenClosedStream(getWorkingArea()).forEach(targetPos -> {
			/*Pair<ItemStack, Direction> option = findSeedFor(lvl, targetPos.below());
			ItemStack seedStack = option.getFirst();
			Direction plantDir = option.getSecond();*/
			if (targetPos.getY() == pos.getY() && isHoe(hoeStack)) {
				BlockPos soilPos = targetPos.below();
				BlockState soilState = lvl.getBlockState(soilPos);
				FakePlayer fakePlayer = FakePlayerHelper.getFakePlayer(this, lvl);
				Vec3 center = targetPos.getCenter();
				UseOnContext ctx = new UseOnContext(lvl, fakePlayer, InteractionHand.MAIN_HAND, hoeStack,
					lvl.clip(new ClipContext(center.add(0, 1, 0), center, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, fakePlayer)));
				BlockState hoedState = soilState.getToolModifiedState(ctx, ToolActions.HOE_TILL, false);
				if (hoedState != null) {
					lvl.setBlockAndUpdate(soilPos, hoedState);
					hoeStack.hurt(1, lvl.getRandom(), fakePlayer);
				}
			}
			
			PlantingOption option = findSeedFor(lvl, targetPos.below());
			ItemStack seed = option.seedStack;
			Direction plantDir = option.direction;
			if (!seed.isEmpty()) {
				BlockState targetState = lvl.getBlockState(targetPos);
				if (targetState.isAir()) {
					BlockState plantState = ((IPlantable) ((BlockItem) seed.getItem()).getBlock()).getPlant(lvl, targetPos);
					if (plantState.canSurvive(lvl, targetPos.below().relative(plantDir.getOpposite()))) {
						lvl.setBlockAndUpdate(targetPos, plantState);
						inventory.extractItem(option.slotIndex, 1, false);
					}
				}
			}
			
		});
		
	}
}
