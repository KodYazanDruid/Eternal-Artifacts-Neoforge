package com.sonamorningstar.eternalartifacts.api.machine.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.common.Tags;

import java.util.HashMap;
import java.util.Map;

public class OilDepositData extends SavedData {
	public final Map<ChunkPos, Long> deposits = new HashMap<>();
	private static final String ID = "eternalartifacts_oil_deposits";
	private static final float DEPOSIT_CHANCE = 0.04F;
	
	public static OilDepositData get(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(
			new SavedData.Factory<>(OilDepositData::new, OilDepositData::load), ID
		);
	}
	
	@Override
	public CompoundTag save(CompoundTag compoundTag) {
		ListTag depositList = new ListTag();
		deposits.forEach((pos, amount) -> {
			CompoundTag chunkTag = new CompoundTag();
			chunkTag.putLong("Amount", amount);
			chunkTag.putInt("ChunkX", pos.x);
			chunkTag.putInt("ChunkZ", pos.z);
			depositList.add(chunkTag);
		});
		compoundTag.put("OilDeposits", depositList);
		return compoundTag;
	}
	
	private static OilDepositData load(CompoundTag compoundTag) {
		OilDepositData data = new OilDepositData();
		ListTag depositList = compoundTag.getList("OilDeposits", 10);
		for (int i = 0; i < depositList.size(); i++) {
			CompoundTag chunkTag = depositList.getCompound(i);
			int chunkX = chunkTag.getInt("ChunkX");
			int chunkZ = chunkTag.getInt("ChunkZ");
			long amount = chunkTag.getLong("Amount");
			data.deposits.put(new ChunkPos(chunkX, chunkZ), amount);
		}
		return data;
	}
	
	private long getOrGenerateDeposit(ChunkPos pos, ServerLevel level) {
		return deposits.computeIfAbsent(pos, p -> {
			long amount = generateDepositAmount(p, level);
			setDirty();
			return amount;
		});
	}
	
	private long generateDepositAmount(ChunkPos pos, ServerLevel level) {
		long rawSeed = level.getSeed() ^ (((long) pos.x << 32) | (pos.z & 0xFFFFFFFFL));
		long mixedSeed = mixSeed(rawSeed);
		RandomSource rand = RandomSource.create(mixedSeed);
		
		Holder<Biome> biome = level.getBiome(new BlockPos(pos.getMinBlockX(), 64, pos.getMinBlockZ()));
		boolean eligible = biome.is(Tags.Biomes.IS_DESERT) || biome.is(BiomeTags.IS_DEEP_OCEAN);
		
		if (!eligible) return 0L;
		
		float randFloat = rand.nextFloat();
		//System.out.println("Random float for chunk " + pos + ": " + randFloat);
		if (randFloat >= DEPOSIT_CHANCE) return 0L;
		
		return 200_000L + rand.nextInt(600_000);
	}
	
	private long mixSeed(long seed) {
		seed = (seed ^ (seed >>> 33)) * 0xff51afd7ed558ccdL;
		seed = (seed ^ (seed >>> 33)) * 0xc4ceb9fe1a85ec53L;
		return seed ^ (seed >>> 33);
	}
	
	private long getRemaining(ChunkPos pos) {
		return deposits.getOrDefault(pos, -1L);
	}
	
	public long getOilAmount(ChunkPos pos, ServerLevel level) {
		long remaining = getRemaining(pos);
		if (remaining < 0) {
			return getOrGenerateDeposit(pos, level);
		}
		return remaining;
	}
	
	public long extract(ChunkPos pos, long amount) {
		Long current = deposits.get(pos);
		if (current == null || current <= 0) return 0;
		long extracted = Math.min(current, amount);
		deposits.put(pos, current - extracted);
		setDirty();
		return extracted;
	}
}
