package com.sonamorningstar.eternalartifacts.content.entity;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class MimicEntity extends Monster {
    private static final EntityDataAccessor<Boolean> IS_OPEN = SynchedEntityData.defineId(MimicEntity.class, EntityDataSerializers.BOOLEAN);
    
    private final ChestLidController chestLidController = new ChestLidController();
    
    public MimicEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MimicAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new MimicLookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new MimicRandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 40)
            .add(Attributes.MOVEMENT_SPEED, 0.5)
            .add(Attributes.ATTACK_DAMAGE, 6)
            .add(Attributes.ARMOR, 4)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }
    
    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_OPEN, false);
	}
	
	public boolean isOpen() {
        return this.entityData.get(IS_OPEN);
    }

    public void setOpen(boolean open) {
        this.entityData.set(IS_OPEN, open);
    }
    
    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            this.entityData.set(IS_OPEN, !this.isOpen());
            this.chestLidController.shouldBeOpen(this.isOpen());
            if (this.isOpen()) this.playSound(SoundEvents.CHEST_OPEN, 1.0F, 1.0F);
            else this.playSound(SoundEvents.CHEST_CLOSE, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(player.level().isClientSide());
        }
        return super.interactAt(player, vec, hand);
    }
    
    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        List<ResourceLocation> tables = BuiltInLootTables.all().stream().toList();
        lootTable = tables.get(random.nextInt(tables.size()));
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.chestLidController.tickLid();
    }
    
    public float getOpenness(float partialTick) {
        return this.chestLidController.getOpenness(partialTick);
    }
    
    public boolean isFullyClosed() {
        return this.getOpenness(1.0f) == 0.0f;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IsOpen", this.isOpen());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setOpen(pCompound.getBoolean("IsOpen"));
    }

    @Override
    public float getWalkTargetValue(BlockPos pPos) {
        return 0.5F;
    }
    
    public class MimicAttackGoal extends MeleeAttackGoal {
        public MimicAttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(mob, speedModifier, followingTargetEvenIfNotSeen);
        }
        
        @Override
        public boolean canUse() {
            return MimicEntity.this.isFullyClosed() && super.canUse();
        }
        
        @Override
        public boolean canContinueToUse() {
            return MimicEntity.this.isFullyClosed() && super.canContinueToUse();
        }
    }
    
    public class MimicLookAtPlayerGoal extends LookAtPlayerGoal {
        public MimicLookAtPlayerGoal(Mob mob, Class<? extends LivingEntity> lookAtType, float lookDistance) {
            super(mob, lookAtType, lookDistance);
        }
        
        @Override
        public boolean canUse() {
            return MimicEntity.this.isFullyClosed() && super.canUse();
        }
        
        @Override
        public boolean canContinueToUse() {
            return MimicEntity.this.isFullyClosed() && super.canContinueToUse();
        }
    }
    
    public class MimicRandomLookAroundGoal extends RandomLookAroundGoal {
        public MimicRandomLookAroundGoal(Mob mob) {
            super(mob);
        }
        
        @Override
        public boolean canUse() {
            return MimicEntity.this.isFullyClosed() && super.canUse();
        }
        
        @Override
        public boolean canContinueToUse() {
            return MimicEntity.this.isFullyClosed() && super.canContinueToUse();
        }
    }
}
