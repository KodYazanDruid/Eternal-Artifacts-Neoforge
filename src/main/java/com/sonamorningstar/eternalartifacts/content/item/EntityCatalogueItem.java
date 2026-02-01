package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.endernotebook.OpenItemStackScreenToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityCatalogueItem extends Item {
	private static final int maxEntitySize = 4;
	private static final String NBT_KEY = "EntityData";
	public static final String IDX_NBT_KEY = "Index";
	
	public EntityCatalogueItem(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == ModEnchantments.VOLUME.get() || super.canApplyAtEnchantingTable(stack, enchantment);
	}
	
	public static int getMaxEntitySize(ItemStack stack) {
		int enchLvl = stack.getEnchantmentLevel(ModEnchantments.VOLUME.get());
		return maxEntitySize + enchLvl * 2;
	}
	
	public static int getIndex(ItemStack stack, Level level) {
		if (stack.hasTag() && stack.getTag().contains(IDX_NBT_KEY)) {
			int index = stack.getTag().getInt(IDX_NBT_KEY);
			var list = getSavedEntities(stack, level);
			if (list == null || list.isEmpty()) return 0;
			return Mth.clamp(index, 0, getSavedEntities(stack, level).size() - 1);
		}
		return 0;
	}
	public static void setIndex(ItemStack stack, int index) {
		stack.getOrCreateTag().putInt(IDX_NBT_KEY, index);
	}
	
	public static List<LivingEntity> getSavedEntities(ItemStack stack, Level level) {
		List<LivingEntity> entities = new ArrayList<>();
		if (stack.hasTag() && stack.getTag().contains(NBT_KEY)) {
			ListTag entityListTag = stack.getTag().getList(NBT_KEY, 10);
			for (Tag tag : entityListTag) {
				Entity entity = EntityType.loadEntityRecursive((CompoundTag) tag, level, (e) -> e);
				if (entity instanceof LivingEntity living) {
					entities.add(living);
				}
			}
			return entities;
		}
		return null;
	}
	
	public static boolean addEntity(ItemStack stack, LivingEntity entity) {
		CompoundTag entityTag = new CompoundTag();
		entity.stopRiding();
		entity.ejectPassengers();
		boolean saved = entity.save(entityTag);
		if (saved) {
			ListTag entityListTag;
			if (stack.hasTag() && stack.getTag().contains(NBT_KEY)) entityListTag = stack.getTag().getList(NBT_KEY, 10);
			else entityListTag = new ListTag();
			entityListTag.add(entityTag);
			if (entity instanceof Villager villager) {
				villager.releaseAllPois();
			}
			stack.getOrCreateTag().put(NBT_KEY, entityListTag);
			entity.discard();
			return true;
		}
		return false;
	}
	
	@Nullable
	public LivingEntity getAndRemoveEntity(ItemStack stack, Level level, int index) {
		if (stack.hasTag() && stack.getTag().contains(NBT_KEY)) {
			ListTag entityListTag = stack.getTag().getList(NBT_KEY, 10);
			if (index >= 0 && index < entityListTag.size()) {
				CompoundTag entityTag = entityListTag.getCompound(index);
				Entity entity = EntityType.loadEntityRecursive(entityTag, level, (e) -> e);
				if (entity instanceof LivingEntity living) {
					entityListTag.remove(index);
					if (entityListTag.isEmpty()) {
						stack.removeTagKey(NBT_KEY);
					}
					return living;
				}
			}
		}
		return null;
	}
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		if (target.getType().is(ModTags.Entities.CATALOGUE_BLACKLISTED)) return InteractionResult.PASS;
		ItemStack heldStack = player.getItemInHand(hand);
		var entities = getSavedEntities(heldStack, player.level());
		if (entities != null && entities.size() >= getMaxEntitySize(heldStack)) {
			return InteractionResult.PASS;
		}
		if (addEntity(heldStack, target)) {
			player.awardStat(Stats.ITEM_USED.get(this));
			return InteractionResult.sidedSuccess(player.level().isClientSide);
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!player.isShiftKeyDown()) {
			return InteractionResultHolder.pass(stack);
		}
		if (player instanceof ServerPlayer serverPlayer) {
			Channel.sendToPlayer(new OpenItemStackScreenToClient(stack), serverPlayer);
		}
		player.awardStat(Stats.ITEM_USED.get(this));
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		Level level = ctx.getLevel();
		ItemStack stack = ctx.getItemInHand();
		
		if (player.isShiftKeyDown()) {
			Direction dir = ctx.getClickedFace();
			BlockPos releasePos = ctx.getClickedPos().relative(dir);
			if (level.getBlockState(releasePos).getCollisionShape(level, releasePos).isEmpty()) {
				LivingEntity entity = getAndRemoveEntity(stack, level, getIndex(stack, level));
				if (entity != null) {
					entity.moveTo(releasePos.getX() + 0.5, releasePos.getY(), releasePos.getZ() + 0.5, dir.toYRot(), 0);
					if (entity instanceof Villager villager && level instanceof ServerLevel sLevel) {
						villager.refreshBrain(sLevel);
					}
					level.addFreshEntity(entity);
					
					var entities = getSavedEntities(stack, ctx.getLevel());
					if (entities == null || entities.isEmpty()) {
						stack.removeTagKey(IDX_NBT_KEY);
						setIndex(stack, 0);
					}else setIndex(stack, Mth.clamp(getIndex(stack, ctx.getLevel()), 0, getSavedEntities(stack, ctx.getLevel()).size() - 1));
					return InteractionResult.sidedSuccess(level.isClientSide());
				}
			}
		}
		
		return super.useOn(ctx);
	}
}
