package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.content.block.FluidHopperBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.TickableServer;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;

@Setter
@Getter
public class FluidHopper extends ModBlockEntity implements Nameable, TickableServer {
	public ModFluidStorage tank;
	private Component name;
	private int cooldown = 0;
	private final int MAX_COOLDOWN = 8;
	public FluidHopper(BlockPos pos, BlockState state) {
		super(ModBlockEntities.FLUID_HOPPER.get(), pos, state);
		tank = createBasicTank(8000);
	}
	
	@Override
	public Component getName() {
		Component customName = getCustomName();
		return customName == null ? Component.translatable(this.getBlockState().getBlock().getDescriptionId()) : customName;
	}
	
	@Nullable
	@Override
	public Component getCustomName() {
		return name;
	}
	
	@Override
	public Component getDisplayName() {
		return getName();
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("CustomName", 8)) name = Component.Serializer.fromJson(tag.getString("CustomName"));
		tank.deserializeNBT(tag.getCompound("Tank"));
		cooldown = tag.getInt("Cooldown");
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (hasCustomName()) tag.putString("CustomName", Component.Serializer.toJson(name));
		tag.put("Tank", tank.serializeNBT());
		tag.putInt("Cooldown", cooldown);
	}
	
	@Override
	public void tickServer(ServerLevel lvl, BlockPos pos, BlockState st) {
		cooldown = Math.min(MAX_COOLDOWN, cooldown + 1);
		if (cooldown == MAX_COOLDOWN) {
			IFluidHandler upHandler = lvl.getCapability(Capabilities.FluidHandler.BLOCK, pos.above(), Direction.DOWN);
			if (upHandler != null) {
				FluidStack toExtract = upHandler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
				int accepted = tank.fillForced(toExtract, IFluidHandler.FluidAction.SIMULATE);
				if (accepted > 0) {
					tank.fillForced(upHandler.drain(accepted, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
					cooldown = 0;
				}
			}
			FluidState fluidState = lvl.getFluidState(pos.above());
			BlockState aboveState = lvl.getBlockState(pos.above());
			if (!fluidState.isEmpty() && fluidState.isSource() && aboveState.getBlock() instanceof BucketPickup bp &&
				(tank.getFluid(0).isEmpty() || (tank.getFluid(0).is(fluidState.getType()) && tank.getEmptySpace(0) >= FluidType.BUCKET_VOLUME))) {
				ItemStack bucket = bp.pickupBlock(null, lvl, pos.above(), aboveState);
				if (!bucket.isEmpty()) {
					IFluidHandlerItem bucketHandler = bucket.getCapability(Capabilities.FluidHandler.ITEM);
					if (bucketHandler != null) {
						FluidStack stack = bucketHandler.getFluidInTank(0).copyWithAmount(FluidType.BUCKET_VOLUME);
						tank.fillForced(stack, IFluidHandler.FluidAction.EXECUTE);
						cooldown = 0;
					}
				}
			}
			Direction facing = st.getValue(FluidHopperBlock.FACING);
			IFluidHandler dest = lvl.getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(facing), facing.getOpposite());
			if (dest != null) {
				FluidStack toExtract = tank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
				int accepted = dest.fill(toExtract, IFluidHandler.FluidAction.SIMULATE);
				if (accepted > 0) {
					dest.fill(tank.drain(accepted, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
					cooldown = 0;
				}
			}
		}
	}
}
