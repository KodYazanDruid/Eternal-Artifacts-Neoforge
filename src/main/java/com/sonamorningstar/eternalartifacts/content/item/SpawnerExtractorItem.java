package com.sonamorningstar.eternalartifacts.content.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

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
					Direction clickedFace = ctx.getClickedFace();
					SpawnerBlock.popResourceFromFace(level, blockPos, clickedFace, spawnEgg.getDefaultInstance());
					entityTag.remove("id");
					level.sendBlockUpdated(blockPos, blockState, blockState, 3);
					return InteractionResult.sidedSuccess(level.isClientSide());
				}
			}
		}
		return super.useOn(ctx);
	}
}
