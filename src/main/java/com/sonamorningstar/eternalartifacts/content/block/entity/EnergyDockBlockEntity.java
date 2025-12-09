package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.content.block.EnergyDockBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.WorkingAreaProvider;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.TickableServer;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.properties.DockPart;
import com.sonamorningstar.eternalartifacts.content.entity.ChargedSheepEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.util.EnergyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public class EnergyDockBlockEntity extends ModBlockEntity implements TickableServer, WorkingAreaProvider {
    @Nullable
    private final ModEnergyStorage energy;
    private DockPart part;
    public EnergyDockBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_DOCK.get(), pos, state);
        this.part = state.getValue(EnergyDockBlock.DOCK_PART);
        energy = part == DockPart.CENTER ? createBasicEnergy(50000, 1000, false, true) : null;
    }

    public DockPart getPart() {
        return getBlockState().getValue(EnergyDockBlock.DOCK_PART);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (energy != null) tag.put("Energy", energy.serializeNBT());
        tag.putString("Part", getPart().toString());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (energy != null) energy.deserializeNBT(tag.get("Energy"));
        part = DockPart.valueOf(tag.getString("Part"));
    }

    @Nullable
    public static ModEnergyStorage getEnergy(EnergyDockBlockEntity dock, @Nullable Direction dir) {
        DockPart part = dock.getPart();
        if (part == DockPart.CENTER) return dock.energy;
        else return dock.level != null ? (ModEnergyStorage) dock.level.getCapability(Capabilities.EnergyStorage.BLOCK, dock.getCenterPos(), dir) : null;
    }

    private ModEnergyStorage getEnergy() {
        return getEnergy(this, null);
    }

    private BlockPos getCenterPos() {
        BlockPos pos = getBlockPos();
        return switch (part) {
            case NORTH_WEST -> pos.south().east();
            case NORTH -> pos.south();
            case NORTH_EAST -> pos.south().west();
            case WEST -> pos.east();
            case CENTER -> pos;
            case EAST -> pos.west();
            case SOUTH_WEST -> pos.north().east();
            case SOUTH -> pos.north();
            case SOUTH_EAST -> pos.north().west();
        };
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        List<ChargedSheepEntity> entities = lvl.getEntitiesOfClass(ChargedSheepEntity.class, getWorkingArea(pos));
        for (ChargedSheepEntity sheep : entities) {
            ModEnergyStorage energy = getEnergy();
            IEnergyStorage sheepEnergy = sheep.getCapability(Capabilities.EnergyStorage.ENTITY, null);
            if (energy != null && sheepEnergy != null && energy.getEnergyStored() < energy.getMaxEnergyStored()) {
                EnergyUtils.transferEnergyForced(sheepEnergy, energy, 1000);
            }
        }
    }

    @Override
    public AABB getWorkingArea(BlockPos anchor) {
        if (part == DockPart.CENTER) return new AABB(anchor).inflate(1, 0.5, 1).move(0, 0.5, 0);
        return null;
    }
}
