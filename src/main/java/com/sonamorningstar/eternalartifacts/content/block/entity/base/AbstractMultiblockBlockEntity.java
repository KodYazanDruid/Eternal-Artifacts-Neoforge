package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

import javax.annotation.Nullable;

@Getter
public abstract class AbstractMultiblockBlockEntity extends Machine<AbstractMachineMenu> {
	@Setter
	private boolean isMaster = false;
	private int masterXOff = 0;
	private int masterYOff = 0;
	private int masterZOff = 0;
	private final Multiblock multiblock;
	
	private Direction forwards = Direction.NORTH;
	private Direction upwards = Direction.UP;
	
	public AbstractMultiblockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Multiblock multiblock) {
		super(type, pos, state, null);
		this.multiblock = multiblock;
		this.maxProgress = defaultMaxProgress;
		this.energyPerTick = defaultEnergyPerTick;
	}
	
	public void setMasterOffsets(int masterXOff, int masterYOff, int masterZOff) {
		this.masterXOff = masterXOff;
		this.masterYOff = masterYOff;
		this.masterZOff = masterZOff;
	}
	
	public void setOrientation(Direction forwards, Direction upwards) {
		if (!isMaster) return;
		this.forwards = forwards;
		this.upwards = upwards;
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		tag.putBoolean("IsMaster", isMaster);
		tag.putInt("MasterXOffset", masterXOff);
		tag.putInt("MasterYOffset", masterYOff);
		tag.putInt("MasterZOffset", masterZOff);
		if (isMaster) {
			super.saveAdditional(tag);
			tag.putString("Forwards", forwards.getName());
			tag.putString("Upwards", upwards.getName());
		}
	}
	
	@Override
	public void load(CompoundTag tag) {
		isMaster = tag.getBoolean("IsMaster");
		masterXOff = tag.getInt("MasterXOffset");
		masterYOff = tag.getInt("MasterYOffset");
		masterZOff = tag.getInt("MasterZOffset");
		if (isMaster) {
			super.load(tag);
			forwards = Direction.byName(tag.getString("Forwards"));
			upwards = Direction.byName(tag.getString("Upwards"));
		}
	}
	
	@Nullable
	public AbstractMultiblockBlockEntity getMasterBlockEntity() {
		Level level = getLevel();
		if (level == null) return null;
		if (isMaster) return this;
		return level.getBlockEntity(getBlockPos().offset(masterXOff, masterYOff, masterZOff)) instanceof AbstractMultiblockBlockEntity masterBe
			? masterBe : null;
	}
	
	@Nullable
	public BlockPos getFrontLeftPos() {
		AbstractMultiblockBlockEntity masterBe = getMasterBlockEntity();
		if (masterBe != null) {
			Multiblock multiblock = masterBe.getMultiblock();
			return BlockPattern.translateAndRotate(
				masterBe.getBlockPos(),
				masterBe.getForwards(),
				masterBe.getUpwards(),
				-multiblock.getMasterPalmOffset(),
				-multiblock.getMasterThumbOffset(),
				-multiblock.getMasterFingerOffset()
			);
		}
		return null;
	}
	
	//Wrap capabilities if ctx is not null.
	@Nullable
	public ModEnergyStorage getEnergy(Direction ctx) {
		AbstractMultiblockBlockEntity masterBe = getMasterBlockEntity();
		if (masterBe != null) {
			if (masterBe == this) {
				return energy;
			}
			return masterBe.getEnergy(ctx);
		}
		return null;
	}
	
	@Nullable
	public AbstractFluidTank getTank(Direction ctx) {
		AbstractMultiblockBlockEntity masterBe = getMasterBlockEntity();
		if (masterBe != null) {
			if (masterBe == this) return tank;
			return masterBe.getTank(ctx);
		}
		return null;
	}
	
	@Nullable
	public ModItemStorage getInventory(Direction ctx) {
		AbstractMultiblockBlockEntity masterBe = getMasterBlockEntity();
		if (masterBe != null) {
			if (masterBe == this) return inventory;
			return masterBe.getInventory(ctx);
		}
		return null;
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState state) {
		if (isMaster) {
		
		}
	}
	
}
