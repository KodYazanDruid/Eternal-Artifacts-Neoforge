package com.sonamorningstar.eternalartifacts.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;

import java.util.List;

public class CursedStoneBricks extends Block {
	public CursedStoneBricks(Properties properties) {
		super(properties);
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.getMaxLocalRawBrightness(pos.above()) > 0 || level.getDifficulty().equals(Difficulty.PEACEFUL)) return;
		AABB checkArea = new AABB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 2, pos.getY() + 3, pos.getZ() + 2);
		int enemyCount = level.getEntitiesOfClass(Mob.class, checkArea, e -> e instanceof Enemy).size();
		if (enemyCount < 9) {
			spawnRandomMonster(level, pos, random);
		}
	}
	
	public void spawnRandomMonster(ServerLevel level, BlockPos pos, RandomSource random) {
		Biome biome = level.getBiome(pos).value();
		List<MobSpawnSettings.SpawnerData> spawnDataList = biome.getMobSettings().getMobs(MobCategory.MONSTER).unwrap();
		if (!spawnDataList.isEmpty()) {
			EntityType<?> entityType = spawnDataList.get(random.nextInt(spawnDataList.size())).type;
			Entity entity = entityType.create(level);
			if (entity instanceof Mob mob) {
				mob.setPos(pos.getCenter().add(0, 0.5, 0));
				if (mob.checkSpawnRules(level, MobSpawnType.NATURAL) && mob.checkSpawnObstruction(level)) {
					if(level.getEntities(mob.getType(), mob.getBoundingBox(), EntitySelector.ENTITY_STILL_ALIVE).isEmpty() && level.noCollision(mob)) {
						EventHooks.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null, null);
						level.addFreshEntity(mob);
					}
				}
			}
		}
	}
}
