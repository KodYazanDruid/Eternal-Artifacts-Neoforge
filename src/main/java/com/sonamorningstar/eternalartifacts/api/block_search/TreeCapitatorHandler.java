package com.sonamorningstar.eternalartifacts.api.block_search;

import com.sonamorningstar.eternalartifacts.api.ModFakePlayer;
import com.sonamorningstar.eternalartifacts.content.item.AxeOfRegrowthItem;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class TreeCapitatorHandler {
	private static final Set<ServerPlayer> MINERS = new HashSet<>();
	
	@SubscribeEvent
	public static void onBreakEvent(BlockEvent.BreakEvent event) {
		if (!(event.getLevel() instanceof ServerLevel level)) return;
		if (!(event.getPlayer() instanceof ServerPlayer player)) return;
		
		if (MINERS.contains(player)) return;
		
		if (player.isShiftKeyDown()) return;
		if (!event.getState().is(BlockTags.LOGS)) return;
		
		ItemStack tool = player.getMainHandItem();
		if (!(tool.getItem() instanceof AxeOfRegrowthItem)) return;
		
		event.setCanceled(true);
		
		MINERS.add(player);
		try {
			TreeFinder.TreeBlocks tree = TreeFinder.find(level, event.getPos());
			if (!tree.isEmpty()) {
				breakTree(level, player, tool, event.getPos(), tree, true);
			}
		} finally {
			MINERS.remove(player);
		}
	}

	private static void breakTree(ServerLevel level,
								  ServerPlayer player,
								  ItemStack tool,
								  BlockPos origin,
								  TreeFinder.TreeBlocks tree,
								  boolean placeSapling) {
		List<ItemStack> allDrops = new ArrayList<>();
		AtomicInteger totalExp = new AtomicInteger();
		Set<BlockPos> allBlocks = new HashSet<>(tree.logs());
		allBlocks.addAll(tree.leaves());
		for (BlockPos pos : allBlocks) {
			BlockState state = level.getBlockState(pos);
			if (state.isAir() || !state.canHarvestBlock(level, pos, player)) continue;
			
			BlockHelper.destroyBlock(level, player, pos, allDrops::addAll, totalExp::addAndGet);
			if (tool.isEmpty()) break;
		}
		
		if (totalExp.get() > 0) {
			ExperienceOrb.award(level, origin.getCenter(), totalExp.get());
		}
		
		if (placeSapling) plantSapling(level, player, tree.root(), allDrops);
		
		if (player instanceof ModFakePlayer modFakePlayer) {
			for (ItemStack drop : allDrops) {
				if (!modFakePlayer.getInventory().add(drop)) {
					Block.popResource(level, origin, drop);
				}
			}
		} else {
			for (ItemStack drop : allDrops) {
				if (!drop.isEmpty()) {
					Block.popResource(level, origin, drop);
				}
			}
		}
	}
	
	private static void plantSapling(ServerLevel level, ServerPlayer player,
									 TreeFinder.RootInfo root, List<ItemStack> drops) {
		if (root == null) return;
		
		if (root.is2x2()) {
			plant2x2(level, player, root.plantAnchor(), drops);
		} else {
			plantSingle(level, player, root.plantAnchor(), drops);
		}
	}
	
	private static void plantSingle(ServerLevel level, ServerPlayer player,
									BlockPos pos, List<ItemStack> drops) {
		if (!level.getBlockState(pos).isAir()) return;
		
		for (int i = 0; i < drops.size(); i++) {
			ItemStack stack = drops.get(i);
			if (stack.isEmpty()) continue;
			if (!(stack.getItem() instanceof BlockItem bi)) continue;
			if (!(bi.getBlock() instanceof SaplingBlock sapling)) continue;
			
			BlockState saplingState = sapling.defaultBlockState();
			if (!saplingState.canSurvive(level, pos)) continue;
			
			if (tryPlace(level, player, pos, saplingState)) {
				stack.shrink(1);
				if (stack.isEmpty()) drops.remove(i);
			}
			return;
		}
	}
	
	private static BlockPos get2x2Pos(int index, BlockPos anchor) {
		return switch (index) {
			case 0 -> anchor;
			case 1 -> anchor.east();
			case 2 -> anchor.south();
			case 3 -> anchor.east().south();
			default -> throw new IllegalArgumentException("Invalid index for 2x2 sapling: " + index);
		};
	}
	
	private static void plant2x2(ServerLevel level, ServerPlayer player,
								 BlockPos anchor, List<ItemStack> drops) {
		BlockPos[] positions = {
			anchor,
			anchor.east(),
			anchor.south(),
			anchor.east().south()
		};
		
		SaplingBlock saplingBlock = null;
		for (ItemStack stack : drops) {
			if (stack.isEmpty()) continue;
			if (!(stack.getItem() instanceof BlockItem bi)) continue;
			if (!(bi.getBlock() instanceof SaplingBlock sb)) continue;
			if (sb.defaultBlockState().canSurvive(level, anchor)) {
				saplingBlock = sb;
				break;
			}
		}
		if (saplingBlock == null) return;
		
		BlockState saplingState = saplingBlock.defaultBlockState();
		
		if (!allPositionsValid(level, positions, saplingState)) {
			plantSingle(level, player, anchor, drops);
			return;
		}
		
		int plantable = 0;
		for (ItemStack drop : drops) {
			if (drop.isEmpty()) continue;
			if (!(drop.getItem() instanceof BlockItem bi)) continue;
			if (!(bi.getBlock().equals(saplingBlock))) continue;
			
			plantable += Math.min(drop.getCount(), 4);
			if (plantable >= 4) break;
		}
		
		consumeAndPlant(drops, saplingBlock, anchor, level, player, plantable);
	}
	
	private static void consumeAndPlant(List<ItemStack> drops, SaplingBlock type, BlockPos pos, ServerLevel level, ServerPlayer player, int count) {
		int remaining = Math.min(count, 4);
		int index = 0;
		for (int i = drops.size() - 1; i >= 0 && remaining > 0; i--) {
			ItemStack stack = drops.get(i);
			if (stack.isEmpty()) continue;
			if (!(stack.getItem() instanceof BlockItem bi)) continue;
			if (!(bi.getBlock().equals(type))) continue;
			
			boolean placed = tryPlace(level, player, get2x2Pos(index++, pos), type.defaultBlockState());
			if (placed) {
				int take = Math.min(remaining, stack.getCount());
				stack.shrink(take);
				remaining -= take;
				if (stack.isEmpty()) drops.remove(i);
			}
		}
	}
	
	private static boolean allPositionsValid(ServerLevel level,
											 BlockPos[] positions,
											 BlockState saplingState) {
		for (BlockPos pos : positions) {
			if (!level.getBlockState(pos).isAir()) return false;
			if (!saplingState.canSurvive(level, pos)) return false;
		}
		return true;
	}
	
	private static boolean tryPlace(ServerLevel level, ServerPlayer player,
									BlockPos pos, BlockState saplingState) {
		BlockSnapshot snapshot      = BlockSnapshot.create(level.dimension(), level, pos);
		boolean cancelled = EventHooks.onBlockPlace(player, snapshot, Direction.UP);
		if (cancelled) {
			level.captureBlockSnapshots = true;
			snapshot.restore(true, false);
			level.captureBlockSnapshots = false;
			return false;
		}
		level.setBlock(pos, saplingState, Block.UPDATE_ALL);
		level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, saplingState));
		return true;
	}
}