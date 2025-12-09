package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import com.sonamorningstar.eternalartifacts.api.farm.FarmBehaviorRegistry;
import com.sonamorningstar.eternalartifacts.api.machine.config.ToggleConfig;
import com.sonamorningstar.eternalartifacts.container.HarvesterMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.WorkingAreaProvider;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import com.sonamorningstar.eternalartifacts.util.LootTableHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ToolActions;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Harvester extends SidedTransferMachine<HarvesterMenu> implements WorkingAreaProvider {
	public static Item[] hoe_tillables;

	private final List<Integer> inputSlots;
	private int workingIndex = 0;
	private List<BlockPos> workingPoses;
	
	public Harvester(BlockPos pos, BlockState blockState) {
		super(ModMachines.HARVESTER.getBlockEntity(), pos, blockState, (a, b, c, d) -> new HarvesterMenu(ModMachines.HARVESTER.getMenu(), a, b, c, d));
		setEnergy(this::createDefaultEnergy);
		setTank(this::createDefaultTank);
		this.inputSlots = List.of(0, 1, 2, 3);
		for (int i = 4; i < 13; i++) {
			outputSlots.add(i);
		}
		hoe_tillables = BuiltInRegistries.ITEM.holders().map(Holder.Reference::value).filter(item -> isHoe(item.getDefaultInstance())).toArray(Item[]::new);
		// slot 13 is hoe slot
		setInventory(() -> createBasicInventory(14, (slot, stack) -> {
			if (slot == 13) return isHoe(stack);
			else if (outputSlots.contains(slot)) return false;
			else return FarmBehaviorRegistry.isValidSeed(stack);
		}));
	}
	
	@Override
	public void registerConfigs() {
		super.registerConfigs();
		getConfiguration().add(new ToggleConfig("harvester_output_mode"));
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("WorkingIndex", workingIndex);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.workingIndex = tag.getInt("WorkingIndex");
	}
	
	@Override
	public AABB getWorkingArea(BlockPos anchor) {
		Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		BlockPos farmCenter = anchor.relative(facing.getOpposite(), 5);
		return new AABB(farmCenter).inflate(4, 0, 4);
	}
	
	private boolean tryInsertToInput() {
		ToggleConfig harvesterOutputMode = getConfiguration().get(ToggleConfig.class, "harvester_output_mode");
		return harvesterOutputMode != null && !harvesterOutputMode.isEnabled();
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoInputItems(lvl, pos);
		performAutoInputFluids(lvl, pos);
		performAutoOutputItems(lvl, pos);
		if (workingPoses == null) {
			workingPoses = BlockPos.MutableBlockPos.betweenClosedStream(getWorkingArea(pos)
				.contract(1, 1, 1)).map(BlockPos::immutable).toList();
		}
		if (!redstoneChecks(lvl) || !canWork(energy)) return;
		
		ItemStack hoeStack = inventory.getStackInSlot(13);
		for (int i = 0; i < progressStep; i++) {
			BlockPos targetPos = workingPoses.get(workingIndex);
			BlockState targetState = lvl.getBlockState(targetPos);
			
			boolean worked = false;
			FarmBehavior behavior = FarmBehaviorRegistry.get(lvl, targetPos);
			if (behavior != null && behavior.canHarvest(lvl, targetPos)) {
				if (!canInsertPossibleItems(lvl, targetState, behavior)) return;
				List<ItemStack> drops = behavior.harvest(lvl, targetPos, hoeStack);
				
				boolean planted = false;
				for (ItemStack drop : drops) {
					if (!drop.isEmpty()) {
						if (!planted && behavior.isCorrectSeed(drop) && behavior.supportsReplanting()) {
							BlockState plantState = behavior.getReplantingState(lvl, targetPos, drop);
							if (!plantState.isAir()) {
								lvl.playSound(null, targetPos, behavior.getReplantSound(targetState), SoundSource.BLOCKS);
								lvl.setBlockAndUpdate(targetPos, plantState);
								lvl.gameEvent(null, GameEvent.BLOCK_CHANGE, targetPos);
								drop.shrink(1);
								planted = true;
							}
						}
						
						if (behavior.isCorrectSeed(drop) && tryInsertToInput()) {
							var remainderPair = ItemHelper.insertItemStackedForced(inventory, drop, false, inputSlots);
							ItemStack remainder = remainderPair.getFirst();
							if (!remainder.isEmpty()) {
								ItemHelper.insertItemStackedForced(inventory, remainder, false, outputSlots);
							}
						} else {
							ItemHelper.insertItemStackedForced(inventory, drop, false, outputSlots);
						}
						
					}
				}
				
				spendEnergy(energy);
				worked = true;
			}
			
			if (!worked) worked = tillSoil(hoeStack, lvl, targetPos, targetState);
			if (!worked) plant(lvl, targetPos);
			workingIndex++;
			if (workingIndex >= workingPoses.size()) {
				workingIndex = 0;
			}
		}
		sendUpdate();
	}
	
	public static boolean isHoe(ItemStack stack) {
		return stack.canPerformAction(ToolActions.HOE_TILL);
	}
	
	public static final Map<Block, List<ItemStack>> cachedLootTables = new HashMap<>();
	protected boolean canInsertPossibleItems(Level lvl, BlockState targetState, FarmBehavior behavior) {
		List<ItemStack> table = cachedLootTables.computeIfAbsent(targetState.getBlock(), block ->
			LootTableHelper.getItemsWithCounts((ServerLevel) lvl, targetState.getBlock().getLootTable())
				.entrySet().stream()
				.map(entry -> new ItemStack(entry.getKey(), entry.getValue().getSecond().intValue()))
				.toList()
		);
		for (ItemStack stack : table) {
			if (!stack.isEmpty()) {
				if (behavior.isCorrectSeed(stack) && tryInsertToInput()) {
					var remainderPair = ItemHelper.insertItemStackedForced(inventory, stack, true, inputSlots);
					ItemStack remainder = remainderPair.getFirst();
					if (!remainder.isEmpty()) {
						var lastPair = ItemHelper.insertItemStackedForced(inventory, remainder, true, outputSlots);
						if (!lastPair.getFirst().isEmpty()) {
							return false;
						}
					}
				} else {
					var lastPair = ItemHelper.insertItemStackedForced(inventory, stack, true, outputSlots);
					if (!lastPair.getFirst().isEmpty()) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	protected boolean tillSoil(ItemStack hoeStack, Level lvl, BlockPos targetPos, BlockState targetState) {
		if (canWork(energy) && isHoe(hoeStack) && targetState.isAir()) {
			BlockPos soilPos = targetPos.below();
			BlockState soilState = lvl.getBlockState(soilPos);
			FakePlayer fakePlayer = getFakePlayer();
			Vec3 center = targetPos.getCenter();
			UseOnContext ctx = new UseOnContext(lvl, fakePlayer, InteractionHand.MAIN_HAND, hoeStack,
				lvl.clip(new ClipContext(center.add(0, 1, 0), center, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, fakePlayer)));
			BlockState hoedState = soilState.getToolModifiedState(ctx, ToolActions.HOE_TILL, false);
			if (hoedState != null) {
				lvl.playSound(null, soilPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
				lvl.setBlockAndUpdate(soilPos, hoedState);
				lvl.gameEvent(null, GameEvent.BLOCK_CHANGE, soilPos);
				hoeStack.hurtAndBreak(1, fakePlayer, p -> {});
				spendEnergy(energy);
				return true;
			}
		}
		return false;
	}
	
	protected void plant(Level lvl, BlockPos targetPos) {
		if (!canWork(energy)) return;
		if (!lvl.getBlockState(targetPos).isAir()) return;
		for (int i = 0; i < 4; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			BlockState plantState = FarmBehaviorRegistry.getPlantingState(lvl, targetPos, stack);
			if (!plantState.isAir() && plantState.canSurvive(lvl, targetPos)) {
				lvl.playSound(null, targetPos, plantState.getSoundType().getPlaceSound(), SoundSource.BLOCKS);
				lvl.setBlockAndUpdate(targetPos, plantState);
				lvl.gameEvent(null, GameEvent.BLOCK_PLACE, targetPos);
				FluidState fluidState = lvl.getFluidState(targetPos);
				lvl.scheduleTick(targetPos, fluidState.getType(), fluidState.getType().getTickDelay(lvl));
				inventory.extractItem(i, 1, false);
				spendEnergy(energy);
				return;
			}
		}
	}
}
