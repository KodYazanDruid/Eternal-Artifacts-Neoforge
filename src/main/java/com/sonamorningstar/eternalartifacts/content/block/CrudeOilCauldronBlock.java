package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.api.cauldron.ModCauldronInteraction;
import com.sonamorningstar.eternalartifacts.content.block.entity.CrudeOilCauldron;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class CrudeOilCauldronBlock extends AbstractCauldronBlock implements EntityBlock {
	public CrudeOilCauldronBlock(Properties pProperties) {
		super(pProperties, ModCauldronInteraction.CRUDE_OIL);
	}
	
	@Override
	protected MapCodec<? extends AbstractCauldronBlock> codec() {
		return simpleCodec(CrudeOilCauldronBlock::new);
	}
	
	@Override
	public boolean isFull(BlockState pState) { return true; }
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		return Items.CAULDRON.getDefaultInstance();
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new CrudeOilCauldron(pPos, pState);
	}
	
	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof CrudeOilCauldron cauldron && cauldron.hasHeatSource(level, pos)) {
			int cooldown = cauldron.cooldown;
			double padding = (random.nextDouble()*10/16D) + 3/16D;
			double d1 = (double)pos.getY() + 1;
			double d2 = (double)pos.getZ() + padding;
			double d0 = (double)pos.getX() + padding;
			if (cooldown > 0) {
				if (random.nextDouble() < 0.1) level.playLocalSound(d0, d1, d2, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
				level.addParticle(
					new DustParticleOptions(new Vector3f(218/255f, 165/255f, 32/255f), 1.0F), // Naphtha amber/goldenrod rengi
					d0, d1, d2, 0, 0, 0
				);
			} else {
				level.addParticle(
					new DustParticleOptions(new Vector3f(0/255f, 255/255f, 127/255f), 1.0F),
					d0, d1, d2, 0, 0, 0
				);
			}
		}
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> bet) {
		if (level.isClientSide) {
			return null;
		}
		return (lvl, pos, st, be) -> {
			if (be instanceof CrudeOilCauldron crudeOilCauldron) {
				crudeOilCauldron.tickServer(lvl, pos, st);
			}
		};
	}
}
