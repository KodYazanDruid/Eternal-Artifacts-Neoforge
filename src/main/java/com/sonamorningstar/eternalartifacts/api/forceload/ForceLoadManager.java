package com.sonamorningstar.eternalartifacts.api.forceload;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ChunkLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;

import javax.annotation.Nullable;
import java.util.*;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ForceLoadManager {
	public static final TicketController TICKET_CONTROLLER = new TicketController(
		new ResourceLocation(MODID, "force_load"),
		ForceLoadManager::validateTickets
	);
	
	private static final List<Pair<UUID, Set<ForcedChunkPos>>> UNFORCED_CHUNKS_QUEUE = new LinkedList<>();
	public static final Map<UUID, Set<ForcedChunkPos>> ENTITY_LOADED_CHUNKS = new HashMap<>();
	private static int CHUNK_DISCARD_COUNTDOWN = 200;
	
	public static final List<ChunkLoader> ALL_LOADERS = new ArrayList<>();
	
	public static void onServerWorldTick(MinecraftServer server) {
		if (CHUNK_DISCARD_COUNTDOWN == 0) {
			for (Map.Entry<UUID, Set<ForcedChunkPos>> entry : ENTITY_LOADED_CHUNKS.entrySet()) {
				unforceAllChunks(server, entry.getKey(), entry.getValue());
			}
			ENTITY_LOADED_CHUNKS.clear();
		} else if (CHUNK_DISCARD_COUNTDOWN > 0)
			CHUNK_DISCARD_COUNTDOWN--;
		
		if (!UNFORCED_CHUNKS_QUEUE.isEmpty()) {
			for (Pair<UUID, Set<ForcedChunkPos>> pair : UNFORCED_CHUNKS_QUEUE) {
				unforceAllChunks(server, pair.getFirst(), pair.getSecond());
			}
			UNFORCED_CHUNKS_QUEUE.clear();
		}
	}
	
	public static void onServerStopping(MinecraftServer server) {
		for (Map.Entry<UUID, Set<ForcedChunkPos>> entry : ENTITY_LOADED_CHUNKS.entrySet()) {
			unforceAllChunks(server, entry.getKey(), entry.getValue());
		}
		ALL_LOADERS.forEach(loader -> unforceAllChunks(server, loader));
		ENTITY_LOADED_CHUNKS.clear();
		UNFORCED_CHUNKS_QUEUE.clear();
		ALL_LOADERS.clear();
		
		CHUNK_DISCARD_COUNTDOWN = -1;
	}
	
	public static <T extends Comparable<? super T>> void updateForcedChunks(
		MinecraftServer server, ForcedChunkPos center, T owner,
		int loadingRange, Set<ForcedChunkPos> forcedChunks, @Nullable ChunkLoader loader) {
		
		Set<ForcedChunkPos> targetChunks = getChunksAroundCenter(center, loadingRange);
		updateForcedChunks(server, targetChunks, owner, forcedChunks, loader);
	}
	
	public static <T extends Comparable<? super T>> void updateForcedChunks(
		MinecraftServer server, Collection<ForcedChunkPos> centers, T owner,
		int loadingRange, Set<ForcedChunkPos> forcedChunks, @Nullable ChunkLoader loader) {
		
		Set<ForcedChunkPos> targetChunks = new HashSet<>();
		for (ForcedChunkPos center : centers) {
			targetChunks.addAll(getChunksAroundCenter(center, loadingRange));
		}
		updateForcedChunks(server, targetChunks, owner, forcedChunks, loader);
	}
	
	public static <T extends Comparable<? super T>> void updateForcedChunks(
		MinecraftServer server, Collection<ForcedChunkPos> newChunks, T owner,
		Set<ForcedChunkPos> forcedChunks, @Nullable ChunkLoader loader) {
		
		Set<ForcedChunkPos> unforcedChunks = new HashSet<>();
		for (ForcedChunkPos chunk : forcedChunks) {
			if (newChunks.contains(chunk)) {
				newChunks.remove(chunk);
			} else {
				forceChunk(server, owner, chunk.dimension(), chunk.getX(), chunk.getZ(), false);
				unforcedChunks.add(chunk);
			}
		}
		forcedChunks.removeAll(unforcedChunks);
		for (ForcedChunkPos chunk : newChunks) {
			forceChunk(server, owner, chunk.dimension(), chunk.getX(), chunk.getZ(), true);
			forcedChunks.add(chunk);
		}
		HashSet<ForcedChunkPos> toClaim = new HashSet<>(forcedChunks);
		/*Set<ForceLoadManager.ForcedChunkPos> allChunks = ForceLoadManager.ALL_LOADERS.stream()
			.flatMap(l -> l.getForcedChunks().stream()).collect(Collectors.toSet());
		toClaim.removeIf(allChunks::contains);*/
		if (loader != null) loader.claimChunks(toClaim);
	}
	
	public static void enqueueUnforceAll(UUID owner, Set<ForcedChunkPos> forcedChunks) {
		UNFORCED_CHUNKS_QUEUE.add(Pair.of(owner, forcedChunks));
	}
	
	public static <T extends Comparable<? super T>> void unforceAllChunks(MinecraftServer server, T owner, Set<ForcedChunkPos> forcedChunks) {
		for (ForcedChunkPos chunk : forcedChunks) {
			forceChunk(server, owner, chunk.dimension(), chunk.getX(), chunk.getZ(), false);
		}
		forcedChunks.clear();
	}
	
	public static void unforceAllChunks(MinecraftServer server, ChunkLoader owner) {
		BlockPos pos = owner.getBlockPos();
		Set<ForcedChunkPos> forcedChunks = owner.getForcedChunks();
		for (ForcedChunkPos chunk : forcedChunks) {
			forceChunk(server, pos, chunk.dimension(), chunk.getX(), chunk.getZ(), false);
		}
		forcedChunks.clear();
	}
	
	private static Set<ForcedChunkPos> getChunksAroundCenter(ForcedChunkPos center, int radius) {
		Set<ForcedChunkPos> ret = new HashSet<>();
		for (int i = center.getX() - radius + 1; i <= center.getX() + radius - 1; i++) {
			for (int j = center.getZ() - radius + 1; j <= center.getZ() + radius - 1; j++) {
				ret.add(new ForcedChunkPos(center.dimension(), i, j));
			}
		}
		return ret;
	}
	
	private static <T extends Comparable<? super T>> void forceChunk(MinecraftServer server, T owner, ResourceLocation dimension, int chunkX, int chunkZ, boolean add) {
		ServerLevel targetLevel = server.getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
		if (targetLevel != null) {
			if (owner instanceof BlockPos pos) {
				TICKET_CONTROLLER.forceChunk(targetLevel, pos, chunkX, chunkZ, add, true);
			} else {
				TICKET_CONTROLLER.forceChunk(targetLevel, (UUID) owner, chunkX, chunkZ, add, true);
			}
		}
	}
	
	private static void validateTickets(ServerLevel serverLevel, TicketHelper ticketHelper) {
		ticketHelper.getBlockTickets().forEach((pos, tickets) -> {
			BlockEntity be = serverLevel.getBlockEntity(pos);
			if (!(be instanceof ChunkLoader chunkLoader)) {
				ticketHelper.removeAllTickets(pos);
				return;
			}
			
			for (Long chunk : tickets.nonTicking()) {
				ticketHelper.removeTicket(pos, chunk, false);
			}
			
			Set<ForcedChunkPos> forcedChunks = new HashSet<>();
			for (Long chunk : tickets.ticking()) {
				ChunkPos chunkPos = new ChunkPos(chunk);
				ForcedChunkPos forced = new ForcedChunkPos(serverLevel, chunkPos);
				forcedChunks.add(forced);
			}
			chunkLoader.claimChunks(forcedChunks);
		});
		
		ticketHelper.getEntityTickets().forEach((id, tickets) -> {
			Set<ForcedChunkPos> forcedChunks = new HashSet<>();
			if (ENTITY_LOADED_CHUNKS.containsKey(id)) {
				forcedChunks = ENTITY_LOADED_CHUNKS.get(id);
			}
			for (Long chunk : tickets.nonTicking()) {
				forcedChunks.add(new ForcedChunkPos(serverLevel, new ChunkPos(chunk)));
			}
			for (Long chunk : tickets.ticking()) {
				forcedChunks.add(new ForcedChunkPos(serverLevel, new ChunkPos(chunk)));
			}
			ENTITY_LOADED_CHUNKS.put(id, forcedChunks);
		});
		CHUNK_DISCARD_COUNTDOWN = 200;
	}
	
	public record ForcedChunkPos(ResourceLocation dimension, ChunkPos chunkPos) {
		public ForcedChunkPos(ServerLevel level, ChunkPos chunkPos) {
			this(level.dimension().location(), chunkPos);
		}
		
		public ForcedChunkPos(ServerLevel level, BlockPos pos) {
			this(level.dimension().location(), pos);
		}
		
		public ForcedChunkPos(ResourceLocation dimension, int x, int z) {
			this(dimension, new ChunkPos(x, z));
		}
		
		public ForcedChunkPos(ResourceLocation dimension, BlockPos pos) {
			this(dimension, new ChunkPos(pos));
		}
		
		public int getX() {
			return chunkPos.x;
		}
		
		public int getZ() {
			return chunkPos.z;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof ForcedChunkPos that)) return false;
			return Objects.equals(chunkPos, that.chunkPos) && Objects.equals(dimension, that.dimension);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(dimension, chunkPos);
		}
		
		@Override
		public String toString() {
			return "ForcedChunkPos{" +
				"dimension=" + dimension +
				", chunkPos=" + chunkPos +
				", blockPos=" + chunkPos.getWorldPosition() +
				'}';
		}
		
		public static ForcedChunkPos read(FriendlyByteBuf buf) {
			return new ForcedChunkPos(
				buf.readResourceLocation(),
				new ChunkPos(buf.readInt(), buf.readInt())
			);
		}
		
		public static void write(FriendlyByteBuf buf, ForcedChunkPos forcedChunkPos) {
			buf.writeResourceLocation(forcedChunkPos.dimension);
			buf.writeInt(forcedChunkPos.getX());
			buf.writeInt(forcedChunkPos.getZ());
		}
	}
}
