package com.sonamorningstar.eternalartifacts.content.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import javax.annotation.Nullable;
import java.util.Optional;

public class SpawnerExtractorItem extends Item {
	public SpawnerExtractorItem(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		BlockPos blockPos = ctx.getClickedPos();
		Level level = ctx.getLevel();
		BlockState blockState = level.getBlockState(blockPos);
		BlockEntity blockEntity = level.getBlockEntity(blockPos);
		if (blockState.getBlock() instanceof SpawnerBlock && blockEntity instanceof SpawnerBlockEntity spawner) {
			SpawnData spawnData = spawner.getSpawner().getOrCreateNextSpawnData(level, level.getRandom(), blockPos);
			CompoundTag entityTag = spawnData.getEntityToSpawn();
			Optional<EntityType<?>> entityType = EntityType.by(entityTag);
			if (entityType.isPresent()) {
				SpawnEggItem spawnEgg = DeferredSpawnEggItem.byId(entityType.get());
				if (spawnEgg != null) {
					if (ctx.getPlayer() != null )
						ctx.getItemInHand().hurtAndBreak(1, ctx.getPlayer(), p -> p.broadcastBreakEvent(ctx.getHand()));
					else
						ctx.getItemInHand().hurt(1, level.random, null);
					Direction clickedFace = ctx.getClickedFace();
					SpawnerBlock.popResourceFromFace(level, blockPos, clickedFace, spawnEgg.getDefaultInstance());
					removeEntityId(spawner.getSpawner(), level, level.getRandom(), blockPos);
					spawner.setChanged();
					level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL);
					return InteractionResult.sidedSuccess(level.isClientSide());
				}
			}
		}
		return super.useOn(ctx);
	}
	
	public void removeEntityId(BaseSpawner spawner, @Nullable Level level, RandomSource random, BlockPos pos) {
		spawner.getOrCreateNextSpawnData(level, random, pos)
			.getEntityToSpawn()
			.remove("id");
	}
}
