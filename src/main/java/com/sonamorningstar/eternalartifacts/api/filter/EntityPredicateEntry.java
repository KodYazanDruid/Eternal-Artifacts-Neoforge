package com.sonamorningstar.eternalartifacts.api.filter;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.*;
import java.util.function.Predicate;

@Getter
@Setter
public class EntityPredicateEntry implements EntityFilterEntry {
	private boolean isWhitelist = true;
	private Set<EntityPredicate> activePredicates = EnumSet.noneOf(EntityPredicate.class);
	private PredicateMode mode = PredicateMode.ANY;
	
	public EntityPredicateEntry() {}
	
	public EntityPredicateEntry(EntityPredicate... predicates) {
		this.activePredicates.addAll(Arrays.asList(predicates));
	}
	
	public EntityPredicateEntry(PredicateMode mode, EntityPredicate... predicates) {
		this.mode = mode;
		this.activePredicates.addAll(Arrays.asList(predicates));
	}
	
	@Override
	public boolean matches(Entity entity) {
		if (entity == null) return !isWhitelist;
		if (activePredicates.isEmpty()) return isWhitelist;
		
		boolean result;
		if (mode == PredicateMode.ALL) {
			result = activePredicates.stream().allMatch(p -> p.test(entity));
		} else {
			result = activePredicates.stream().anyMatch(p -> p.test(entity));
		}
		
		return isWhitelist == result;
	}
	
	@Override
	public boolean isEmpty() {
		return activePredicates.isEmpty();
	}
	
	@Override
	public Component getDisplayName() {
		if (activePredicates.isEmpty()) {
			return Component.translatable("filter.eternalartifacts.entity_any");
		}
		if (activePredicates.size() == 1) {
			return activePredicates.iterator().next().getDisplayName();
		}
		return Component.translatable("filter.eternalartifacts.entity_multi", activePredicates.size());
	}
	
	@Override
	public boolean isIgnoreNBT() {
		return true;
	}
	
	@Override
	public void setIgnoreNBT(boolean ignoreNBT) {
		// Entity filtreleme için NBT kontrolü yok
	}
	
	public void addPredicate(EntityPredicate predicate) {
		activePredicates.add(predicate);
	}
	
	public void removePredicate(EntityPredicate predicate) {
		activePredicates.remove(predicate);
	}
	
	public void togglePredicate(EntityPredicate predicate) {
		if (activePredicates.contains(predicate)) {
			activePredicates.remove(predicate);
		} else {
			activePredicates.add(predicate);
		}
	}
	
	public boolean hasPredicate(EntityPredicate predicate) {
		return activePredicates.contains(predicate);
	}
	
	public void clearPredicates() {
		activePredicates.clear();
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putString("Type", "Predicate");
		tag.putBoolean("IsWhitelist", isWhitelist);
		tag.putByte("Mode", (byte) mode.ordinal());
		
		int[] ordinals = activePredicates.stream().mapToInt(EntityPredicate::ordinal).toArray();
		tag.putIntArray("Predicates", ordinals);
		
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag tag) {
		this.isWhitelist = tag.getBoolean("IsWhitelist");
		int modeOrdinal = tag.getByte("Mode");
		this.mode = modeOrdinal >= 0 && modeOrdinal < PredicateMode.values().length
			? PredicateMode.values()[modeOrdinal]
			: PredicateMode.ANY;
		
		this.activePredicates.clear();
		int[] ordinals = tag.getIntArray("Predicates");
		EntityPredicate[] values = EntityPredicate.values();
		for (int ordinal : ordinals) {
			if (ordinal >= 0 && ordinal < values.length) {
				activePredicates.add(values[ordinal]);
			}
		}
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buff) {
		buff.writeUtf("Predicate");
		buff.writeBoolean(isWhitelist);
		buff.writeByte(mode.ordinal());
		
		buff.writeVarInt(activePredicates.size());
		for (EntityPredicate predicate : activePredicates) {
			buff.writeVarInt(predicate.ordinal());
		}
	}
	
	@Override
	public void fromNetwork(FriendlyByteBuf buff) {
		this.isWhitelist = buff.readBoolean();
		int modeOrdinal = buff.readByte();
		this.mode = modeOrdinal >= 0 && modeOrdinal < PredicateMode.values().length
			? PredicateMode.values()[modeOrdinal]
			: PredicateMode.ANY;
		
		this.activePredicates.clear();
		int count = buff.readVarInt();
		EntityPredicate[] values = EntityPredicate.values();
		for (int i = 0; i < count; i++) {
			int ordinal = buff.readVarInt();
			if (ordinal >= 0 && ordinal < values.length) {
				activePredicates.add(values[ordinal]);
			}
		}
	}
	
	@Override
	public String toString() {
		return "EntityPredicateEntry{" +
			"activePredicates=" + activePredicates +
			", mode=" + mode +
			", isWhitelist=" + isWhitelist +
			'}';
	}
	
	public enum PredicateMode {
		ANY,
		ALL
	}
	
	public enum EntityPredicate implements Predicate<Entity> {
		// === Yaşam Durumu ===
		ALIVE(Entity::isAlive, "filter.eternalartifacts.entity.alive"),
		DEAD(e -> !e.isAlive(), "filter.eternalartifacts.entity.dead"),
		
		// === Entity Tipi ===
		LIVING(e -> e instanceof LivingEntity, "filter.eternalartifacts.entity.living"),
		MOB(e -> e instanceof Mob, "filter.eternalartifacts.entity.mob"),
		PLAYER(e -> e instanceof Player, "filter.eternalartifacts.entity.player"),
		MONSTER(e -> e instanceof Monster, "filter.eternalartifacts.entity.monster"),
		ANIMAL(e -> e instanceof Animal, "filter.eternalartifacts.entity.animal"),
		WATER_ANIMAL(e -> e instanceof WaterAnimal, "filter.eternalartifacts.entity.water_animal"),
		VILLAGER(e -> e instanceof Villager, "filter.eternalartifacts.entity.villager"),
		NPC(e -> e instanceof Npc, "filter.eternalartifacts.entity.npc"),
		AMBIENT(e -> e instanceof AmbientCreature, "filter.eternalartifacts.entity.ambient"),
		
		// === Düşmanlık ===
		HOSTILE(e -> e instanceof Enemy, "filter.eternalartifacts.entity.hostile"),
		NEUTRAL(e -> e instanceof NeutralMob, "filter.eternalartifacts.entity.neutral"),
		PASSIVE(e -> e instanceof Animal && !(e instanceof NeutralMob), "filter.eternalartifacts.entity.passive"),
		TAMEABLE(e -> e instanceof TamableAnimal, "filter.eternalartifacts.entity.tameable"),
		TAMED(e -> e instanceof TamableAnimal ta && ta.isTame(), "filter.eternalartifacts.entity.tamed"),
		
		// === Yaş Durumu ===
		BABY(e -> e instanceof LivingEntity le && le.isBaby(), "filter.eternalartifacts.entity.baby"),
		ADULT(e -> e instanceof LivingEntity le && !le.isBaby(), "filter.eternalartifacts.entity.adult"),
		AGEABLE(e -> e instanceof AgeableMob, "filter.eternalartifacts.entity.ageable"),
		BREEDABLE(e -> e instanceof Animal a && a.canFallInLove(), "filter.eternalartifacts.entity.breedable"),
		
		// === Boss ===
		BOSS(e -> !e.canChangeDimensions(), "filter.eternalartifacts.entity.boss"),
		WITHER(e -> e instanceof WitherBoss, "filter.eternalartifacts.entity.wither"),
		ENDER_DRAGON(e -> e instanceof EnderDragon, "filter.eternalartifacts.entity.ender_dragon"),
		
		// === Hareket ===
		ON_GROUND(Entity::onGround, "filter.eternalartifacts.entity.on_ground"),
		IN_WATER(Entity::isInWater, "filter.eternalartifacts.entity.in_water"),
		IN_LAVA(Entity::isInLava, "filter.eternalartifacts.entity.in_lava"),
		UNDERWATER(Entity::isUnderWater, "filter.eternalartifacts.entity.underwater"),
		SWIMMING(Entity::isSwimming, "filter.eternalartifacts.entity.swimming"),
		SPRINTING(Entity::isSprinting, "filter.eternalartifacts.entity.sprinting"),
		CROUCHING(Entity::isCrouching, "filter.eternalartifacts.entity.crouching"),
		FLYING(e -> e instanceof LivingEntity le && le.isFallFlying(), "filter.eternalartifacts.entity.flying"),
		SLEEPING(e -> e instanceof LivingEntity le && le.isSleeping(), "filter.eternalartifacts.entity.sleeping"),
		
		// === Durum ===
		ON_FIRE(Entity::isOnFire, "filter.eternalartifacts.entity.on_fire"),
		FREEZING(Entity::isFullyFrozen, "filter.eternalartifacts.entity.freezing"),
		INVISIBLE(Entity::isInvisible, "filter.eternalartifacts.entity.invisible"),
		GLOWING(Entity::isCurrentlyGlowing, "filter.eternalartifacts.entity.glowing"),
		SILENT(Entity::isSilent, "filter.eternalartifacts.entity.silent"),
		INVULNERABLE(Entity::isInvulnerable, "filter.eternalartifacts.entity.invulnerable"),
		NO_GRAVITY(Entity::isNoGravity, "filter.eternalartifacts.entity.no_gravity"),
		
		// === Sağlık ===
		FULL_HEALTH(e -> e instanceof LivingEntity le && le.getHealth() >= le.getMaxHealth(), "filter.eternalartifacts.entity.full_health"),
		LOW_HEALTH(e -> e instanceof LivingEntity le && le.getHealth() <= le.getMaxHealth() * 0.25f, "filter.eternalartifacts.entity.low_health"),
		HALF_HEALTH(e -> e instanceof LivingEntity le && le.getHealth() <= le.getMaxHealth() * 0.5f, "filter.eternalartifacts.entity.half_health"),
		
		// === Araçlar ===
		VEHICLE(Entity::isVehicle, "filter.eternalartifacts.entity.vehicle"),
		PASSENGER(Entity::isPassenger, "filter.eternalartifacts.entity.passenger"),
		BOAT(e -> e instanceof Boat, "filter.eternalartifacts.entity.boat"),
		MINECART(e -> e instanceof AbstractMinecart, "filter.eternalartifacts.entity.minecart"),
		
		// === Projectile ===
		PROJECTILE(e -> e instanceof Projectile, "filter.eternalartifacts.entity.projectile"),
		
		// === Item & Experience ===
		ITEM_ENTITY(e -> e instanceof ItemEntity, "filter.eternalartifacts.entity.item_entity"),
		EXPERIENCE_ORB(e -> e instanceof ExperienceOrb, "filter.eternalartifacts.entity.experience_orb"),
		
		// === Leash ===
		LEASHABLE(e -> e instanceof Mob, "filter.eternalartifacts.entity.leashable"),
		LEASHED(e -> e instanceof Mob m && m.getLeashHolder() != null, "filter.eternalartifacts.entity.leashed"),
		
		// === Spawn ===
		PERSISTENT(e -> e instanceof Mob m && m.isPersistenceRequired(), "filter.eternalartifacts.entity.persistent"),
		CAN_DESPAWN(e -> e instanceof Mob m && !m.isPersistenceRequired(), "filter.eternalartifacts.entity.can_despawn");
		
		private final Predicate<Entity> predicate;
		private final String translationKey;
		
		EntityPredicate(Predicate<Entity> predicate, String translationKey) {
			this.predicate = predicate;
			this.translationKey = translationKey;
		}
		
		@Override
		public boolean test(Entity entity) {
			return predicate.test(entity);
		}
		
		public Component getDisplayName() {
			return Component.translatable(translationKey);
		}
		
		public String getTranslationKey() {
			return translationKey;
		}
	}
}
