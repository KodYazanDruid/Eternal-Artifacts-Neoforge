package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.MultiblockCapabilityManager;
import com.sonamorningstar.eternalartifacts.util.RelativeBlockPos;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
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
import java.util.Arrays;
import java.util.Map;

@Setter
@Getter
public abstract class AbstractMultiblockBlockEntity extends Machine<AbstractMachineMenu> {
	private boolean isMaster = false;
	private LongSet disciples = new LongArraySet();
	private int partIndex = 0;
	private BlockState deformState = Blocks.AIR.defaultBlockState();
	private int mbWidth;
	private int mbHeight;
	private int mbDepth;
	private int masterXOff = 0;
	private int masterYOff = 0;
	private int masterZOff = 0;
	private boolean isDeforming = false;
	private Direction forwards = Direction.NORTH;
	private Direction upwards = Direction.UP;

	private final Multiblock multiblock;
	
	/**
	 *  The capabilities on the disciples are not used and not being saved. Get capabilities from the master instead.
	 */
	public AbstractMultiblockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Multiblock multiblock) {
		super(type, pos, state, null);
		this.multiblock = multiblock;
	}

	/**
	 * Defines the specific slot indices that this hatch should exclusively load.
	 * If null is returned, all slots may be displayed.
	 */
	@Nullable
	public int[] getHatchSlots() {
		return null;
	}

	/**
	 * Returns an array of strings representing a visual slot layout for the hatch UI.
	 */
	@Nullable
	public String[] getHatchSlotPattern() {
		return null;
	}

	/**
	 * Maps characters in the hatch slot pattern to the actual IItemHandler slot indices.
	 */
	@Nullable
	public Map<Character, Integer> getHatchSlotConfig() {
		return null;
	}

	/**
	 * Starting X pixel position on the UI screen for the slot pattern.
	 */
	public int getHatchSlotStartX() {
		return 8;
	}

	/**
	 * Starting Y pixel position on the UI screen for the slot pattern.
	 */
	public int getHatchSlotStartY() {
		return 18;
	}

	/**
	 * Returns the specific fluid tank indices this fluid hatch is allowed to show/interact.
	 * If null, all available tanks are shown.
	 */
	@Nullable
	public int[] getHatchFluidTanks() {
		return null;
	}

	/**
	 * Starting X pixel position on the UI screen for fluid bars.
	 */
	public int getHatchFluidStartX() {
		return 8;
	}

	/**
	 * Starting Y pixel position on the UI screen for fluid bars.
	 */
	public int getHatchFluidStartY() {
		return 18;
	}

	/**
	 * Spacing between generated fluid tanks.
	 */
	public int getHatchFluidSpacing() {
		return 24;
	}

	/**
	 * Whether this hatch displays energy.
	 */
	public boolean hasHatchEnergy() {
		return true;
	}

	/**
	 * X pixel position for energy bar on the UI.
	 */
	public int getHatchEnergyX() {
		return 8;
	}

	/**
	 * Y pixel position for energy bar on the UI.
	 */
	public int getHatchEnergyY() {
		return 20;
	}
	
	public int[] getAvaiableInventorySlots() {
		return null;
	}
	
	public boolean canAccessSlot(int slot) {
		int[] availableSlots = getAvaiableInventorySlots();
		return availableSlots == null || Arrays.stream(availableSlots).anyMatch(s -> s == slot);
	}
	
	public int[] getAvaiableFluidTanks() {
		return null;
	}
	
	public boolean canAccessTank(int tank) {
		int[] availableTanks = getAvaiableFluidTanks();
		return availableTanks == null || Arrays.stream(availableTanks).anyMatch(t -> t == tank);
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
		tag.putInt("PartIndex", partIndex);
		if (isMaster) {
			super.saveAdditional(tag);
			tag.putString("Forwards", forwards.getName());
			tag.putString("Upwards", upwards.getName());
			ListTag slavesTag = new ListTag();
			for (long disciple : disciples) {
				CompoundTag slaveTag = new CompoundTag();
				slaveTag.putLong("Pos", disciple);
				slavesTag.add(slaveTag);
			}
			tag.put("Disciples", slavesTag);
			tag.putInt("MBWidth", mbWidth);
			tag.putInt("MBHeight", mbHeight);
			tag.putInt("MBDepth", mbDepth);
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
		partIndex = tag.getInt("PartIndex");
		if (isMaster) {
			super.load(tag);
			forwards = Direction.byName(tag.getString("Forwards"));
			upwards = Direction.byName(tag.getString("Upwards"));
			ListTag slavesTag = tag.getList("Disciples", 10);
			disciples = new LongArraySet();
			for (int i = 0; i < slavesTag.size(); i++) {
				disciples.add(slavesTag.getCompound(i).getLong("Pos"));
			}
			mbWidth = tag.getInt("MBWidth");
			mbHeight = tag.getInt("MBHeight");
			mbDepth = tag.getInt("MBDepth");
		}
		deformState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("DeformState"));
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		invalidateCapabilities();
	}
	
	public void deformMultiblock() {
		deformMultiblock(getBlockPos());
	}
	
	protected boolean deformMultiblock(BlockPos brokenPos) {
		AbstractMultiblockBlockEntity master = getMasterBlockEntity();
		if (master != null) {
			return master.deformOnMaster(brokenPos);
		}
		return false;
	}
	
	private boolean deformOnMaster(BlockPos brokenPos) {
		if (!isDeforming && isMaster) {
			isDeforming = true;
			boolean canDeform = onDeform(getLevel(), getBlockPos(),
				new RelativeBlockPos(brokenPos.getX() - getBlockPos().getX(), brokenPos.getY() - getBlockPos().getY(), brokenPos.getZ() - getBlockPos().getZ())
			);
			if (canDeform) {
				var itemHandler = getLevel().getCapability(Capabilities.ItemHandler.BLOCK, brokenPos, null);
				if (itemHandler != null) {
					for (int i = 0; i < itemHandler.getSlots(); i++) {
						ItemStack stack = itemHandler.getStackInSlot(i);
						Block.popResource(getLevel(), brokenPos, stack);
					}
				}
				for (long slave : disciples) {
					BlockPos slavePos = BlockPos.of(slave);
					BlockEntity be = getLevel().getBlockEntity(slavePos);
					if (be instanceof AbstractMultiblockBlockEntity ambe) {
						getLevel().setBlockAndUpdate(slavePos, ambe.getDeformState());
					}
				}
				getLevel().setBlockAndUpdate(getBlockPos(), getDeformState());
				return true;
			} else isDeforming = false;
		}
		return false;
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
	public BlockPos getFrontLeftTopPos() {
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
	
	public RelativeBlockPos getMultiblockRelativePos() {
		BlockPos frontLeftTopPos = getFrontLeftTopPos();
		if (frontLeftTopPos != null) {
			return new RelativeBlockPos(
				getBlockPos().getX() - frontLeftTopPos.getX(),
				getBlockPos().getY() - frontLeftTopPos.getY(),
				getBlockPos().getZ() - frontLeftTopPos.getZ()
			);
		}
		return null;
	}
	
	//region Capability Getters
	// Giving null on recursive calls is fine because I already check if that direction should have capability right above.
	@Nullable
	public ModEnergyStorage getEnergy(Direction ctx) {
		AbstractMultiblockBlockEntity masterBe = getMasterBlockEntity();
		if (masterBe != null) {
			if (masterBe == this) {
				if (ctx == null) return energy;
				
				BlockPos frontLeftPos = getFrontLeftTopPos();
				if (frontLeftPos != null) {
					boolean hasCapability = multiblock.getCapabilityManager()
						.hasCapability(frontLeftPos, getBlockPos(), ctx, MultiblockCapabilityManager.CapabilityType.ENERGY, getForwards(), getUpwards());
					
					return hasCapability ? energy : null;
				}
			} else {
				if (ctx == null) return masterBe.getEnergy(null);
				
				boolean hasCapability = multiblock.getCapabilityManager()
					.hasCapability(masterBe.getFrontLeftTopPos(), getBlockPos(), ctx, MultiblockCapabilityManager.CapabilityType.ENERGY, masterBe.getForwards(), masterBe.getUpwards());
				
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
				
				BlockPos frontLeftPos = getFrontLeftTopPos();
				if (frontLeftPos != null) {
					boolean hasCapability = multiblock.getCapabilityManager()
						.hasCapability(frontLeftPos, getBlockPos(), ctx, MultiblockCapabilityManager.CapabilityType.FLUID, getForwards(), getUpwards());
					
					return hasCapability ? tank : null;
				}
			} else {
				if (ctx == null) return masterBe.getTank(null);
				
				boolean hasCapability = multiblock.getCapabilityManager()
					.hasCapability(masterBe.getFrontLeftTopPos(), getBlockPos(), ctx, MultiblockCapabilityManager.CapabilityType.FLUID, masterBe.getForwards(), masterBe.getUpwards());
			
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
				
				BlockPos frontLeftPos = getFrontLeftTopPos();
				if (frontLeftPos != null) {
					boolean hasCapability = multiblock.getCapabilityManager()
						.hasCapability(frontLeftPos, getBlockPos(), ctx, MultiblockCapabilityManager.CapabilityType.ITEM, getForwards(), getUpwards());
					
					return hasCapability ? inventory : null;
				}
			} else {
				if (ctx == null) return masterBe.getInventory(null);
				
				boolean hasCapability = multiblock.getCapabilityManager()
					.hasCapability(masterBe.getFrontLeftTopPos(), getBlockPos(), ctx, MultiblockCapabilityManager.CapabilityType.ITEM, masterBe.getForwards(), masterBe.getUpwards());
			
				return hasCapability ? masterBe.getInventory(null) : null;
			}
		}
		return null;
	}
	//endregion
	
	/**
	 *  Fired just after the multiblock is formed and all data is set.
	 */
	public void onFormed(Level level, BlockPos masterPos) {
	
	}
	
	/**
	 *  Fired just before the multiblock deforms.
	 *  @return whether the multiblock should actually deform. Returning false will cancel the deformation and reform the multiblock.
	 */
	public boolean onDeform(Level level, BlockPos masterPos, RelativeBlockPos brokenOffset) {
		return true;
	}
	
	/**
	 * Main logic handled in the master but disciples can also tick if they needed.
	 */
	@Override
	public void tickServer(ServerLevel lvl, BlockPos pos, BlockState st) {
		if (isMaster) {
			super.tickServer(lvl, pos, st);
			tickMaster(lvl, pos, st);
		} else {
			if (isDirty && !isRemoved() && lvl.hasChunkAt(pos)) {
				setChanged();
				lvl.sendBlockUpdated(pos, st, st, 3);
				isDirty = false;
			}
			tickDisciple(lvl, pos, st);
		}
	}
	
	public void tickMaster(ServerLevel lvl, BlockPos pos, BlockState st) {
	
	}
	
	public void tickDisciple(ServerLevel lvl, BlockPos pos, BlockState st) {
	
	}
}
