package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.api.forceload.ForceLoadManager;
import com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.ForcedChunksToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Set;

/**
 * An interface for objects that can force-load chunks in the world.
 * This is not limited to {@link net.minecraft.world.level.block.entity.BlockEntity}s; any object can implement this interface.
 * <p>
 * Implementers are responsible for:
 * <ul>
 *     <li>Managing their own {@link Set} of forced chunks to be returned by {@link #getForcedChunks()}.</li>
 *     <li>Calling {@link #addToManager()} and {@link #removeFromManager()} at appropriate lifecycle points (e.g., on load and removal).</li>
 *     <li>Calling {@link #updateForcedChunks()} whenever the loader's state changes and a chunk update is required.</li>
 * </ul>
 *
 * @see Tesseract for an example implementation in a BlockEntity.
 */
public interface ChunkLoader {
	
	Set<ForceLoadManager.ForcedChunkPos> getForcedChunks();
	Level getLevel();
	BlockPos getBlockPos();
	boolean canLoadChunks();
	boolean needsUpdate();
	int getLoadingRange();
	int getChunkUnloadCooldown();
	void setChunkUnloadCooldown(int cooldown);
	
	default void claimChunks(Set<ForceLoadManager.ForcedChunkPos> forcedChunks) {
		getForcedChunks().addAll(forcedChunks);
		/*System.out.println("*-*-*-*-*-*");
		System.out.println("Claimed " + forcedChunks.size() + " chunks at " + getBlockPos());
		System.out.println("BlockEntity: " + this);*/
		if (getLevel() instanceof ServerLevel sLevel) {
			Channel.sendToChunk(new ForcedChunksToClient(getForcedChunks(), getBlockPos()), sLevel.getChunkAt(getBlockPos()));
		}
	}
	
	default void addToManager() {
		ForceLoadManager.ALL_LOADERS.add(this);
	}
	default void removeFromManager() {
		ForceLoadManager.ALL_LOADERS.remove(this);
	}
	
	/**
	 * Handles the logic for updating the force-loaded chunks based on the loader's current state.
	 * If {@link #canLoadChunks()} is true, it will update the loaded chunks around its position.
	 * If false, it will begin a cooldown and then unforce its chunks.
	 */
	default void updateForcedChunks() {
		boolean resetCooldown = true;
		BlockPos pos = getBlockPos();
		Level level = getLevel();
		if (!(level instanceof ServerLevel serverLevel)) return;
		if (canLoadChunks()) {
			ForceLoadManager.updateForcedChunks(serverLevel.getServer(),
				new ForceLoadManager.ForcedChunkPos(serverLevel, pos), pos,
				getLoadingRange(), getForcedChunks());
		} else if (getChunkUnloadCooldown() == 0) {
			ForceLoadManager.unforceAllChunks(level.getServer(), pos, getForcedChunks());
		} else {
			setChunkUnloadCooldown(getChunkUnloadCooldown() - 1);
			resetCooldown = false;
		}
		if (resetCooldown) {
			setChunkUnloadCooldown(0);
		}
	}
}
