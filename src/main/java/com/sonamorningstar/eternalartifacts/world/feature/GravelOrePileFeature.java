package com.sonamorningstar.eternalartifacts.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class GravelOrePileFeature extends Feature<GravelOrePileConfiguration> {
    public GravelOrePileFeature(Codec<GravelOrePileConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<GravelOrePileConfiguration> context) {
        BlockPos blockPos = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        GravelOrePileConfiguration config = context.config();
        
        int radius = config.radius().sample(random);
        if (radius <= 0) return false;
        
        int yRadius = Math.min(radius, 2);
        
        if (blockPos.getY() < level.getMinBuildHeight() + yRadius) {
            return false;
        }
        
        int placed = 0;
        
        for (int i = blockPos.getX() - radius; i <= blockPos.getX() + radius; i++) {
            for (int j = blockPos.getY() - yRadius; j <= blockPos.getY() + yRadius; j++) {
                for (int k = blockPos.getZ() - radius; k <= blockPos.getZ() + radius; k++) {
                    int dx = i - blockPos.getX();
                    int dy = j - blockPos.getY();
                    int dz = k - blockPos.getZ();
                    
                    float normalizedX = (float) dx / radius;
                    float normalizedY = (float) dy / yRadius;
                    float normalizedZ = (float) dz / radius;
                    
                    if (normalizedX * normalizedX + normalizedY * normalizedY + normalizedZ * normalizedZ <= 1.0f) {
                        BlockPos currentPos = new BlockPos(i, j, k);
                        BlockState stateToPlace = config.stateProvider().getState(random, currentPos);
                        
                        if ((float)(dx * dx + dz * dz) <= random.nextFloat() * 10.0F - random.nextFloat() * 6.0F) {
                            if (tryPlaceBlock(level, currentPos, stateToPlace, random)) {
                                placed++;
                            }
                        } else if ((double)random.nextFloat() < 0.031) {
                            if (tryPlaceBlock(level, currentPos, stateToPlace, random)) {
                                placed++;
                            }
                        }
                    }
                }
            }
        }
        
        return placed > 0;
    }
    
    private static boolean tryPlaceBlock(LevelAccessor level, BlockPos pos, BlockState state, RandomSource random) {
        BlockState currentState = level.getBlockState(pos);
        
        if (!currentState.isAir()) {
            return false;
        }
        
        BlockState belowState = level.getBlockState(pos.below());
        
        if (!isSolid(belowState, level, pos.below())) {
            return false;
        }
        
        level.setBlock(pos, state, 4);
        return true;
    }
    
    private static boolean isSolid(BlockState state, LevelAccessor level, BlockPos pos) {
        return state.isFaceSturdy(level, pos, Direction.UP);
    }
}
