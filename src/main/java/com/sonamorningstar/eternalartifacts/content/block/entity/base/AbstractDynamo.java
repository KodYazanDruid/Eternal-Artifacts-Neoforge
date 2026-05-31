package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.api.caches.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.base.DynamoMenu;
import com.sonamorningstar.eternalartifacts.content.recipe.base.DynamoRecipe;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.registrar.MachineHolder;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import lombok.Getter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

@SuppressWarnings("unchecked")
public abstract class AbstractDynamo<MENU extends DynamoMenu> extends Machine<MENU> implements TickableClient {
	public AbstractDynamo(MachineHolder<?, ?, ?, ?> holder, BlockPos pos, BlockState blockState) {
		super(holder.getBlockEntity(), pos, blockState, (a, b, c, d) -> (MENU) holder.registration().getMenuFactory().apply(holder.getMenu(), a, b, c, d));
		setEnergy(() -> createBasicEnergy(100000, 5000, false, true));
	}
	
	private float animProgress = 0f;
	private float animSpeed = 0f;
	float lastWave = 0f;
	
	@Getter
	protected DynamoProcessCache cache;
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if(cache != null) cache.writeToNbt(tag);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		cache = DynamoProcessCache.readFromNbt(tag, energy, this).orElse(null);
		if (cache != null) setMaxProgress(cache.getMaxDuration());
	}
	
	@Override
	public void saveContents(CompoundTag additionalTag) {
		super.saveContents(additionalTag);
		if(cache != null) cache.writeToNbt(additionalTag);
	}
	
	@Override
	public void loadContents(CompoundTag additionalTag) {
		super.loadContents(additionalTag);
		cache = DynamoProcessCache.readFromNbt(additionalTag, energy, this).orElse(null);
		if (cache != null) setMaxProgress(cache.getMaxDuration());
	}

	@Override
	public void findRecipe() {
		if (cache != null) return;
		super.findRecipe();
		if (RecipeCache.getCachedRecipe(this) instanceof DynamoRecipe recipe) {
			prepareDynamo(recipe);
		}
	}
	
	protected void prepareDynamo(DynamoRecipe recipe) {
		int celerity = getEnchantmentLevel(ModEnchantments.CELERITY.get());
		setEnergyPerTick((int) (recipe.getGeneration() * ((celerity / (float) 3) + 1)));
		int eff = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
		setMaxProgress((int) (recipe.getDuration() * ((eff / (float) 5) + 1)));
	}
	
	public float getAnimationLerp(float partialTick) {
		float anim = animProgress + animSpeed * partialTick;
		float phase = anim * (float)(Math.PI * 2);
		float rawWave = (1 - Mth.cos(phase)) * 0.5f;
		float smoothFactor = isWorking ? 1f : 0.15f;
		lastWave += (rawWave - lastWave) * smoothFactor;
		return Mth.lerp(lastWave, 6f, 9f);
	}
	
	@Override
	public void tickClient(ClientLevel lvl, BlockPos pos, BlockState st) {
		float targetSpeed = isWorking ? 0.1f : 0f;
		float accel = 0.04f;
		animSpeed += (targetSpeed - animSpeed) * accel;
		if (Math.abs(animSpeed) < 0.0001f) animSpeed = 0f;
		animProgress = (animProgress + animSpeed) % 1f;
	}
	
	@Override
	protected void applyEfficiency(int level) {}
	
	@Override
	public boolean isGenerator() {
		return true;
	}
	
	@Override
	public boolean shouldSyncWorkingState() {
		return true;
	}
	
	protected boolean canProcessRecipeless() {
		return false;
	}
	
	public void invalidateDynamoCache() {
		this.cache = null;
		this.progress = 0;
		setWorking(false);
		onCacheExpire();
		findRecipe();
	}
	
	@Override
	public void tickServer(ServerLevel lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		
		if(hasAnyEnergy(energy)) outputEnergyToDir(lvl, pos, getBlockState().getValue(BlockStateProperties.FACING), energy);
		
		Recipe<?> recipe = RecipeCache.getCachedRecipe(this);
		if(cache != null) {
			progress = cache.getDuration();
			if(!cache.isDone()) {
				if (redstoneChecks(lvl)) cache.process();
				else setWorking(false);
			} else if (cache.isDone()) {
				invalidateDynamoCache();
			}
		} else if (redstoneChecks(lvl)) {
			if(recipe != null) {
				executeRecipe(recipe, (duration, energyCap, generation, dynamo) -> {
					this.cache = createCache(duration, energyCap, generation, dynamo);
					return this.cache;
				});
			} else if (canProcessRecipeless()) {
				executeRecipeless((duration, energyCap, generation, dynamo) -> {
					this.cache = createCache(duration, energyCap, generation, dynamo);
					return this.cache;
				});
			}
		}
	}
	
	protected DynamoProcessCache createCache(int duration, ModEnergyStorage energyCap, int generation, AbstractDynamo<?> dynamo) {
		return new DynamoProcessCache(duration, duration, energyCap, generation, dynamo);
	}
	
	protected void executeRecipe(Recipe<?> recipe, QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {}
	protected void executeRecipeless(QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {}
	protected void onCacheExpire() {
		setEnergyPerTick(defaultEnergyPerTick);
		setMaxProgress(defaultMaxProgress);
	}
}
