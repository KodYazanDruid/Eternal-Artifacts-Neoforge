package com.sonamorningstar.eternalartifacts.api.farm;

import com.sonamorningstar.eternalartifacts.api.farm.behaviors.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;

public class FarmBehaviorRegistry {
	private static final NavigableSet<FarmBehavior> BEHAVIORS = new TreeSet<>(
		Comparator.comparingInt(FarmBehavior::getPriority)
			.reversed()
			.thenComparing(b -> b.getClass().getName())
			.thenComparingInt(System::identityHashCode)
	);
	
	public static void register(FarmBehavior behavior) {
		BEHAVIORS.add(behavior);
	}
	
	@Nullable
	public static FarmBehavior get(Level lvl, BlockPos pos) {
		for (var behavior : BEHAVIORS) {
			if (behavior.matches(lvl, pos)) {
				return behavior;
			}
		}
		return null;
	}
	
	public static BlockState getPlantingState(Level level, BlockPos pos, ItemStack seed) {
		for (var behavior : BEHAVIORS) {
			if (behavior.isCorrectSeed(seed)) {
				return behavior.getPlantingState(level, pos, seed);
			}
		}
		return Blocks.AIR.defaultBlockState();
	}
	
	public static boolean isValidSeed(ItemStack seed) {
		for (var behavior : BEHAVIORS) {
			if (behavior.isCorrectSeed(seed)) return true;
		}
		return false;
	}
	
	public static void bootstrap() {
		register(new NetherWartBehavior());
		register(new CocoaBeansBehavior());
		register(new CropBehavior());
		register(new BambooBehavior());
		register(new KelpBehavior());
		register(new SweetBerryBehavior());
		register(new TorchFlowerBehavior());
		register(new PitcherPlantBehavior());
		register(new GlowBerryBehavior());
		register(new ChorusFlowerBehavior());
		register(new OreBerryBehavior());
		register(new StemBehavior(Items.MELON_SEEDS, Blocks.MELON_STEM, Blocks.MELON, Blocks.ATTACHED_MELON_STEM));
		register(new StemBehavior(Items.PUMPKIN_SEEDS, Blocks.PUMPKIN_STEM, Blocks.PUMPKIN, Blocks.ATTACHED_PUMPKIN_STEM));
		register(new ReedBehavior(() -> Blocks.SUGAR_CANE));
		register(new ReedBehavior(() -> Blocks.CACTUS));
	}
}
