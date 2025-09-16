package com.sonamorningstar.eternalartifacts.api.machine.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

import javax.annotation.Nullable;

public class MultiblockPatternHelper {
	
	/*@Nullable
	public static BlockPattern.BlockPatternMatch findMultiblockPattern(LevelReader levelReader, BlockPos pos, BlockPattern pattern) {
		int range = Math.max(Math.max(pattern.getWidth(), pattern.getHeight()), pattern.getDepth()) - 1;
		for (int dx = -range; dx <= range; dx++) {
			for (int dy = -range; dy <= range; dy++) {
				for (int dz = -range; dz <= range; dz++) {
					BlockPos checkPos = pos.offset(dx, dy, dz);
					
					for (Direction finger : Direction.values()) {
						for (Direction thumb : Direction.values()) {
							if (finger != thumb && finger != thumb.getOpposite()) {
								BlockPattern.BlockPatternMatch match =
									pattern.matches(levelReader, checkPos, finger, thumb);
								if (match != null) {
									return match;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}*/
	
	@Nullable
	public static BlockPattern.BlockPatternMatch findMultiblockPattern(LevelReader levelReader, BlockPos pos, BlockPattern pattern) {
		int range = Math.max(Math.max(pattern.getWidth(), pattern.getHeight()), pattern.getDepth()) - 1;
		for (int dx = -range; dx <= range; dx++) {
			for (int dy = -range; dy <= range; dy++) {
				for (int dz = -range; dz <= range; dz++) {
					BlockPos checkPos = pos.offset(dx, dy, dz);
					
					for (Direction finger : Direction.values()) {
						for (Direction thumb : Direction.values()) {
							
							if (finger == thumb || finger == thumb.getOpposite()) continue;
							
							BlockPattern.BlockPatternMatch match =
								pattern.matches(levelReader, checkPos, finger, thumb);
							
							if (match != null) return match;
						}
					}
				}
			}
		}
		return null;
	}
	
	public static void clearPatternBlocks3D(Level pLevel, BlockPattern.BlockPatternMatch pPatternMatch) {
		for (int d = 0; d < pPatternMatch.getDepth(); ++d) {
			for (int i = 0; i < pPatternMatch.getWidth(); ++i) {
				for (int j = 0; j < pPatternMatch.getHeight(); ++j) {
					BlockInWorld blockinworld = pPatternMatch.getBlock(i, j, d);
					pLevel.setBlock(blockinworld.getPos(), Blocks.AIR.defaultBlockState(), 2);
					pLevel.levelEvent(2001, blockinworld.getPos(), Block.getId(blockinworld.getState()));
				}
			}
		}
	}
	
	public static void updatePatternBlocks3D(Level pLevel, BlockPattern.BlockPatternMatch pPatternMatch) {
		for (int d = 0; d < pPatternMatch.getDepth(); ++d) {
			for (int i = 0; i < pPatternMatch.getWidth(); ++i) {
				for (int j = 0; j < pPatternMatch.getHeight(); ++j) {
					BlockInWorld blockinworld = pPatternMatch.getBlock(i, j, d);
					pLevel.blockUpdated(blockinworld.getPos(), Blocks.AIR);
				}
			}
		}
	}
}
