package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.api.caches.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.base.DynamoMenu;
import com.sonamorningstar.eternalartifacts.content.recipe.base.DynamoRecipe;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public abstract class AbstractDynamo<MENU extends DynamoMenu> extends Machine<MENU> implements TickableClient {
	public AbstractDynamo(BlockEntityType<?> type, BlockPos pos, BlockState blockState, QuadFunction<Integer, Inventory, BlockEntity, ContainerData, MENU> quadF) {
		super(type, pos, blockState, quadF);
		setEnergy(() -> createBasicEnergy(100000, 5000, false, true));
	}
	
	private float animProgress = 0f;
	private float animSpeed = 0f;
	float lastWave = 0f;
	
	public boolean isWorking = false;
	@Getter
	protected DynamoProcessCache cache;
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		if(cache != null) cache.writeToNbt(tag);
		super.saveAdditional(tag);
	}
	
	@Override
	public void load(CompoundTag tag) {
		isWorking = tag.getBoolean("IsWorking");
		cache = DynamoProcessCache.readFromNbt(tag, energy, this).orElse(null);
		if (cache != null) setMaxProgress(cache.getMaxDuration());
		super.load(tag);
	}
	
	@Override
	protected void saveSynced(CompoundTag tag) {
		super.saveSynced(tag);
		tag.putBoolean("IsWorking", isWorking);
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
	protected void findRecipe() {
		if (cache != null) return;
		super.findRecipe();
		if (RecipeCache.getCachedRecipe(this) instanceof DynamoRecipe recipe) {
			prepareDynamo(recipe);
		}
	}
	
	protected void prepareDynamo(DynamoRecipe recipe) {
		int celerity = getEnchantmentLevel(ModEnchantments.CELERITY.get());
		setEnergyPerTick(recipe.getGeneration() * ((celerity / 3) + 1));
		int eff = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
		setMaxProgress(recipe.getDuration() * ((eff / 5) + 1));
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
	public void tickClient(Level lvl, BlockPos pos, BlockState st) {
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
	
	protected boolean canProcessRecipeless() {
		return false;
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		
		if(hasAnyEnergy(energy)) outputEnergyToDir(lvl, pos, getBlockState().getValue(BlockStateProperties.FACING), energy);
		
		Recipe<?> recipe = RecipeCache.getCachedRecipe(this);
		if(cache != null) {
			progress = cache.getDuration();
			if(!cache.isDone() && redstoneChecks(lvl)) {
				cache.process();
			} else if (!cache.isDone() && !redstoneChecks(lvl)) {
				isWorking = false;
				sendUpdate();
			} else if (cache.isDone()) {
				cache = null;
				progress = 0;
				onCacheExpire();
				findRecipe();
				if (recipe == null && !canProcessRecipeless()) {
					isWorking = false;
					sendUpdate();
				}
			}
		} else {
			if(recipe != null) {
				executeRecipe(recipe, (duration, energyCap, generation, dynamo) -> {
					this.cache = new DynamoProcessCache(duration, duration, energyCap, generation, dynamo);
					return this.cache;
				});
			} else if (canProcessRecipeless()) {
				executeRecipeless((duration, energyCap, generation, dynamo) -> {
					this.cache = new DynamoProcessCache(duration, duration, energyCap, generation, dynamo);
					return this.cache;
				});
			}
		}
	}
	
	protected void executeRecipe(Recipe<?> recipe, QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {}
	protected void executeRecipeless(QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {}
	protected void onCacheExpire() {
		setEnergyPerTick(defaultEnergyPerTick);
		setMaxProgress(defaultMaxProgress);
	}
}
