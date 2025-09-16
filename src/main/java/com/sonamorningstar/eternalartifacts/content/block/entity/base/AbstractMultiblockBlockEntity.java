package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.MultiblockCapabilityManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class AbstractMultiblockBlockEntity extends Machine<AbstractMachineMenu> {
	@Setter
	private boolean isMaster = false;
	@Setter
	private BlockState deformState = Blocks.AIR.defaultBlockState();
	private int masterXOff = 0;
	private int masterYOff = 0;
	private int masterZOff = 0;
	private final Multiblock multiblock;
	@Setter
	private Set<BlockPos> slaves = new HashSet<>();
	private boolean isDeforming = false;
	
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
			ListTag slavesTag = new ListTag();
			for (BlockPos slave : slaves) {
				slavesTag.add(NbtUtils.writeBlockPos(slave));
			}
			tag.put("Slaves", slavesTag);
		}
		if (!deformState.isAir()) {
			CompoundTag stateTag = NbtUtils.writeBlockState(deformState);
			tag.put("DeformState", stateTag);
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
			ListTag slavesTag = tag.getList("Slaves", 10);
			slaves = new HashSet<>();
			for (int i = 0; i < slavesTag.size(); i++) {
				slaves.add(NbtUtils.readBlockPos(slavesTag.getCompound(i)));
			}
		}
		deformState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("DeformState"));
	}
	
	public void deformMultiblock() {
		deformMultiblock(getBlockPos());
	}
	
	public void deformMultiblock(BlockPos brokenPos) {
		AbstractMultiblockBlockEntity master = getMasterBlockEntity();
		if (master != null) {
			master.deformOnMaster(brokenPos);
			level.setBlockAndUpdate(getBlockPos(), getDeformState());
		}
	}
	
	private void deformOnMaster(BlockPos brokenPos) {
		if (!isDeforming) {
			isDeforming = true;
			for (BlockPos slave : slaves) {
				BlockEntity be = level.getBlockEntity(slave);
				if (be instanceof AbstractMultiblockBlockEntity ambe) {
					level.setBlockAndUpdate(slave, ambe.getDeformState());
				}
			}
			var itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, brokenPos, null);
			if (itemHandler != null) {
				for (int i = 0; i < itemHandler.getSlots(); i++) {
					ItemStack stack = itemHandler.getStackInSlot(i);
					Block.popResource(level, brokenPos, stack);
				}
			}
			level.setBlockAndUpdate(getBlockPos(), getDeformState());
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
	
	@Nullable
	public ModEnergyStorage getEnergy(Direction ctx) {
		AbstractMultiblockBlockEntity masterBe = getMasterBlockEntity();
		if (masterBe != null) {
			if (masterBe == this) {
				if (ctx == null) return energy;
				
				BlockPos frontLeftPos = getFrontLeftPos();
				if (frontLeftPos != null) {
					boolean hasCapability = multiblock.getCapabilityManager()
						.hasCapability(frontLeftPos, getBlockPos(), ctx, MultiblockCapabilityManager.CapabilityType.ENERGY, getForwards(), getUpwards());
					
					return hasCapability ? energy : null;
				}
			} else {
				if (ctx == null) return masterBe.getEnergy(null);
				
				boolean hasCapability = multiblock.getCapabilityManager()
					.hasCapability(masterBe.getFrontLeftPos(), getBlockPos(), ctx, MultiblockCapabilityManager.CapabilityType.ENERGY, masterBe.getForwards(), masterBe.getUpwards());
				
				return hasCapability ? masterBe.getEnergy(null) : null;
			}
		}
		return null;
	}
	
	@Nullable
	public AbstractFluidTank getTank(Direction ctx) {
		AbstractMultiblockBlockEntity masterBe = getMasterBlockEntity();
		if (masterBe != null) {
			if (masterBe == this) {
				if (ctx == null) return tank;
				
				BlockPos frontLeftPos = getFrontLeftPos();
				if (frontLeftPos != null) {
					boolean hasCapability = multiblock.getCapabilityManager()
						.hasCapability(frontLeftPos, getBlockPos(), ctx, MultiblockCapabilityManager.CapabilityType.FLUID, getForwards(), getUpwards());
					
					return hasCapability ? tank : null;
				}
			} else {
				if (ctx == null) return masterBe.getTank(null);
				
				boolean hasCapability = multiblock.getCapabilityManager()
					.hasCapability(masterBe.getFrontLeftPos(), getBlockPos(), ctx, MultiblockCapabilityManager.CapabilityType.FLUID, masterBe.getForwards(), masterBe.getUpwards());
			
				return hasCapability ? masterBe.getTank(null) : null;
			}
		}
		return null;
	}
	
	@Nullable
	public ModItemStorage getInventory(Direction ctx) {
		AbstractMultiblockBlockEntity masterBe = getMasterBlockEntity();
		if (masterBe != null) {
			if (masterBe == this) {
				if (ctx == null) return inventory;
				
				BlockPos frontLeftPos = getFrontLeftPos();
				if (frontLeftPos != null) {
					boolean hasCapability = multiblock.getCapabilityManager()
						.hasCapability(frontLeftPos, getBlockPos(), ctx, MultiblockCapabilityManager.CapabilityType.ITEM, getForwards(), getUpwards());
					
					return hasCapability ? inventory : null;
				}
			} else {
				if (ctx == null) return masterBe.getInventory(null);
				
				boolean hasCapability = multiblock.getCapabilityManager()
					.hasCapability(masterBe.getFrontLeftPos(), getBlockPos(), ctx, MultiblockCapabilityManager.CapabilityType.ITEM, masterBe.getForwards(), masterBe.getUpwards());
			
				return hasCapability ? masterBe.getInventory(null) : null;
			}
		}
		return null;
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		if (isMaster) {
			super.tickServer(lvl, pos, st);
		}
	}
}
