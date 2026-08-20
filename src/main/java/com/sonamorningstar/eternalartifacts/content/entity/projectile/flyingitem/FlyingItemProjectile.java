package com.sonamorningstar.eternalartifacts.content.entity.projectile.flyingitem;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@Setter
public class FlyingItemProjectile extends Projectile {
	
	private static final EntityDataAccessor<ItemStack> DATA_ITEM =
		SynchedEntityData.defineId(FlyingItemProjectile.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Float> DATA_SPIN_SPEED =
		SynchedEntityData.defineId(FlyingItemProjectile.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_DRAG =
		SynchedEntityData.defineId(FlyingItemProjectile.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Byte> DATA_ORIENTATION_MODE =
		SynchedEntityData.defineId(FlyingItemProjectile.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Float> DATA_MODEL_PITCH_OFFSET =
		SynchedEntityData.defineId(FlyingItemProjectile.class, EntityDataSerializers.FLOAT);
	
	
	private final Set<Integer> hitEntityIds = new HashSet<>();
	
	private float damage = 6.0F;
	private float knockback = 0.3F;
	private int maxLife = 200;
	private boolean piercing = false;
	
	@Nullable
	private ProjectileMovement movement = null;
	
	public FlyingItemProjectile(EntityType<? extends FlyingItemProjectile> type, Level level) {
		super(type, level);
	}
	
	public FlyingItemProjectile(Level level, LivingEntity owner, ItemStack renderItem) {
		this(ModEntities.FLYING_ITEM_PROJECTILE.get(), level);
		this.setOwner(owner);
		this.setItem(renderItem);
		this.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
	}
	
	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_ITEM, ItemStack.EMPTY);
		this.entityData.define(DATA_SPIN_SPEED, 0.0F);
		this.entityData.define(DATA_DRAG, 1.0F);
		this.entityData.define(DATA_ORIENTATION_MODE, (byte) OrientationMode.SIMPLE_SPIN.ordinal());
		this.entityData.define(DATA_MODEL_PITCH_OFFSET, 0.0F);
	}
	
	public void setItem(ItemStack stack) { this.entityData.set(DATA_ITEM, stack.copy()); }
	public ItemStack getItem() { return this.entityData.get(DATA_ITEM); }
	
	public void setDragFactor(float drag) { this.entityData.set(DATA_DRAG, drag); }
	public float getDragFactor() { return this.entityData.get(DATA_DRAG); }
	
	public void setSpinSpeed(float degreesPerTick) { this.entityData.set(DATA_SPIN_SPEED, degreesPerTick); }
	public float getSpinSpeed() { return this.entityData.get(DATA_SPIN_SPEED); }
	
	public void setOrientationMode(OrientationMode mode) { this.entityData.set(DATA_ORIENTATION_MODE, (byte) mode.ordinal()); }
	public OrientationMode getOrientationMode() { return OrientationMode.values()[this.entityData.get(DATA_ORIENTATION_MODE)]; }
	
	public void setModelPitchOffset(float degrees) {this.entityData.set(DATA_MODEL_PITCH_OFFSET, degrees);}
	public float getModelPitchOffset() {return this.entityData.get(DATA_MODEL_PITCH_OFFSET);}
	
	public float getSpinAngle(float partialTicks) {
		return (this.tickCount + partialTicks) * this.getSpinSpeed();
	}
	
	protected float getGravity() {
		return 0.03F;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		final boolean clientSide = this.level().isClientSide;
		
		if (this.movement != null) {
			if (clientSide) {
				return;
			}
			this.setDeltaMovement(this.movement.updateVelocity(this, this.getDeltaMovement(), this.tickCount));
		} else {
			Vec3 newDelta = this.getDeltaMovement();
			if (!this.isNoGravity()) {
				newDelta = newDelta.add(0.0, -this.getGravity(), 0.0);
			}
			newDelta = newDelta.scale(this.getDragFactor());
			this.setDeltaMovement(newDelta);
		}
		
		if (!clientSide) {
			HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
			if (hit.getType() != HitResult.Type.MISS) {
				this.onHit(hit);
			}
		}
		
		Vec3 delta = this.getDeltaMovement();
		Vec3 pos = this.position();
		this.setPos(pos.x + delta.x, pos.y + delta.y, pos.z + delta.z);
		this.updateRotation();
		
		if (!clientSide && this.tickCount >= this.maxLife) {
			this.discard();
		}
	}
	
	/*@Override
	public void updateRotation() {
		super.updateRotation();
	}*/
	
	@Override
	protected void updateRotation() {
		Vec3 vec3 = this.getDeltaMovement();
		
		// Hız yoksa (örn. movement fonksiyonu geçici olarak 0 verdiyse) mevcut açıyı koru.
		if (vec3.lengthSqr() < 1.0E-7) {
			this.xRotO = this.getXRot();
			this.yRotO = this.getYRot();
			return;
		}
		
		double horizontalDist = vec3.horizontalDistance();
		// NOT: Bu iki satır vanilla Projectile#updateRotation() ile birebir aynı formül
		// (ok modelinin dönüşünde kanıtlanmış doğru formül). Değiştirmiyoruz.
		float targetPitch = (float) (Mth.atan2(vec3.y, horizontalDist) * (180.0 / Math.PI));
		float targetYaw = (float) (Mth.atan2(vec3.x, vec3.z) * (180.0 / Math.PI));
		
		// Vanilla'daki %20 lerpRotation() burada BİLİNÇLİ olarak kaldırıldı:
		// - xRotO/yRotO hiç güncellenmediği için o lerp sonsuza kadar %20'de kilitleniyordu.
		// - Kılıç/balta gibi "anında doğru yöne baksın" istediğimiz eşyalar için zaten
		//   ok'daki o yumuşak-eğrisel görünüm istenmiyor.
		// xRotO/yRotO yine de renderer'daki kare-arası (partial tick) interpolasyon için
		// doğru şekilde güncelleniyor.
		this.xRotO = this.getXRot();
		this.yRotO = this.getYRot();
		this.setXRot(-targetPitch);
		this.setYRot(-targetYaw);
	}
	
	@Override
	protected boolean canHitEntity(Entity target) {
		if (target == this.getOwner()) return false;
		if (this.hitEntityIds.contains(target.getId())) return false;
		return super.canHitEntity(target);
	}
	
	@Override
	protected void onHitEntity(EntityHitResult result) {
		Entity target = result.getEntity();
		
		DamageSource source = this.damageSources().thrown(this, this.getOwner());
		boolean hurt = target.hurt(source, this.damage);
		
		if (hurt) {
			if (target instanceof LivingEntity livingTarget && this.knockback > 0) {
				Vec3 dir = this.getDeltaMovement();
				livingTarget.knockback(this.knockback, -dir.x, -dir.z);
			}
			this.hitEntityIds.add(target.getId());
			
			if (!this.piercing) {
				this.discard();
			}
		}
	}
	
	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		this.discard();
	}
	
	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.put("Item", this.getItem().save(new CompoundTag()));
		tag.putFloat("Damage", this.damage);
		tag.putFloat("Knockback", this.knockback);
		tag.putInt("MaxLife", this.maxLife);
		tag.putBoolean("Piercing", this.piercing);
		tag.putFloat("Drag", this.getDragFactor());
		tag.putFloat("SpinSpeed", this.getSpinSpeed());
		tag.putByte("OrientationMode", (byte) this.getOrientationMode().ordinal());
		tag.putFloat("ModelPitchOffset", this.getModelPitchOffset());
	}
	
	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("Item", Tag.TAG_COMPOUND)) {
			this.setItem(ItemStack.of(tag.getCompound("Item")));
		}
		this.damage = tag.getFloat("Damage");
		this.knockback = tag.getFloat("Knockback");
		this.maxLife = tag.getInt("MaxLife");
		this.piercing = tag.getBoolean("Piercing");
		this.setDragFactor(tag.getFloat("Drag"));
		this.setSpinSpeed(tag.getFloat("SpinSpeed"));
		if (tag.contains("OrientationMode")) {
			this.setOrientationMode(OrientationMode.values()[tag.getByte("OrientationMode")]);
		}
		this.setModelPitchOffset(tag.getFloat("ModelPitchOffset"));
	}
	
	public enum OrientationMode {
		SIMPLE_SPIN,
		POINT_FORWARD,
		TUMBLING
	}
}