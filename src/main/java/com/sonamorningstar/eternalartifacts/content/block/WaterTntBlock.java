package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.entity.PrimedBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class WaterTntBlock extends TntBlock {
	public WaterTntBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
		if (!level.isClientSide) {
			PrimedBlockEntity primed = new PrimedBlockEntity(level, new Vector3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5),
				explosion.getIndirectSourceEntity(), 80, 4.0F, ModBlocks.WATER_TNT.get().defaultBlockState());
			int i = primed.getFuse();
			primed.setFuse((short)(level.random.nextInt(i / 4) + i / 8));
			level.addFreshEntity(primed);
		}
	}
	
	@Override
	public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
		if (!level.isClientSide) {
			PrimedBlockEntity primed = new PrimedBlockEntity(level, new Vector3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5),
				igniter, 80, 4.0F, ModBlocks.WATER_TNT.get().defaultBlockState());
			level.addFreshEntity(primed);
			level.playSound(null, primed.getX(), primed.getY(), primed.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
		}
	}
}
