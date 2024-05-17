package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FallingDropExperienceBlock extends FallingBlock {
    private final IntProvider xpRange;
    public FallingDropExperienceBlock(IntProvider xpRange, Properties props) {
        super(props.requiresCorrectToolForDrops());
        this.xpRange = xpRange;
    }

    @Override
    protected MapCodec<? extends FallingDropExperienceBlock> codec() {
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(IntProvider.codec(0, 10).fieldOf("experience").forGetter(block -> block.xpRange), propertiesCodec())
                        .apply(instance, FallingDropExperienceBlock::new)
        );
    }

    @Override
    public void spawnAfterBreak(BlockState pState, ServerLevel pLevel, BlockPos pPos, ItemStack pStack, boolean pDropExperience) {
        super.spawnAfterBreak(pState, pLevel, pPos, pStack, pDropExperience);

    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.util.RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return silkTouchLevel == 0 ? xpRange.sample(randomSource) : 0;
    }
}
