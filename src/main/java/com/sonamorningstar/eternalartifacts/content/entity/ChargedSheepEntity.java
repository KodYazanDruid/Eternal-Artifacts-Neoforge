package com.sonamorningstar.eternalartifacts.content.entity;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ChargedSheepEnergy;
import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.core.ModLootTables;
import com.sonamorningstar.eternalartifacts.util.EnergyUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class ChargedSheepEntity extends Sheep implements PowerableMob {
    public static final EntityDataAccessor<Integer> ENERGY = SynchedEntityData.defineId(ChargedSheepEntity.class, EntityDataSerializers.INT);

    public ChargedSheepEntity(EntityType<? extends Sheep> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ChargedSheepEnergy energy = new ChargedSheepEnergy(this, 5000, 5000);

    @Override
    public boolean isPowered() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ENERGY, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Energy", energy.serializeNBT());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        energy.deserializeNBT(tag.get("Energy"));
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        if (isSheared()) {
            return this.getType().getDefaultLootTable();
        } else {
            return switch(getColor()) {
                case WHITE -> ModLootTables.CHARGED_SHEEP_WHITE;
                case ORANGE -> ModLootTables.CHARGED_SHEEP_ORANGE;
                case MAGENTA -> ModLootTables.CHARGED_SHEEP_MAGENTA;
                case LIGHT_BLUE -> ModLootTables.CHARGED_SHEEP_LIGHT_BLUE;
                case YELLOW -> ModLootTables.CHARGED_SHEEP_YELLOW;
                case LIME -> ModLootTables.CHARGED_SHEEP_LIME;
                case PINK -> ModLootTables.CHARGED_SHEEP_PINK;
                case GRAY -> ModLootTables.CHARGED_SHEEP_GRAY;
                case LIGHT_GRAY -> ModLootTables.CHARGED_SHEEP_LIGHT_GRAY;
                case CYAN -> ModLootTables.CHARGED_SHEEP_CYAN;
                case PURPLE -> ModLootTables.CHARGED_SHEEP_PURPLE;
                case BLUE -> ModLootTables.CHARGED_SHEEP_BLUE;
                case BROWN -> ModLootTables.CHARGED_SHEEP_BROWN;
                case GREEN -> ModLootTables.CHARGED_SHEEP_GREEN;
                case RED -> ModLootTables.CHARGED_SHEEP_RED;
                case BLACK -> ModLootTables.CHARGED_SHEEP_BLACK;
            };
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energy != null) {
            if (!level().isClientSide){
                if (EnergyUtils.transferEnergy(this.energy, energy, Integer.MAX_VALUE) > 0)
                    return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }else return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public Sheep getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        ChargedSheepEntity child = ModEntities.CHARGED_SHEEP.get().create(level);
        if (child != null) {
            ChargedSheepEntity mother = (ChargedSheepEntity) otherParent;
            ChargedSheepEnergy motherEnergy = mother.energy;
            ChargedSheepEnergy childEnergy = child.energy;
            if (energy != null && motherEnergy != null && childEnergy != null) {
                int motherExtracted = motherEnergy.extractEnergyForced(motherEnergy.getEnergyStored() / 3, false);
                int fatherExtracted = energy.extractEnergyForced(energy.getEnergyStored() / 3, false);
                childEnergy.setEnergy((int) ((motherExtracted + fatherExtracted) * 1.2));
            }
            child.setColor(getOffspringColor(this, mother));
        }
        return child;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance pDifficulty, MobSpawnType reason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if (reason == MobSpawnType.CONVERSION) {
            RandomSource random = level.getRandom();
            energy.setEnergy(random.nextIntBetweenInclusive(3000, 4000));
        }
        return super.finalizeSpawn(level, pDifficulty, reason, pSpawnData, pDataTag);
    }
}
