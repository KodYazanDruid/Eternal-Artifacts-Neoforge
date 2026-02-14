package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.filter.EntityPredicateEntry;
import com.sonamorningstar.eternalartifacts.content.block.base.EntityFilterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.WorkingAreaProvider;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Getter
@Setter
public class EntityInteractor extends GenericMachine implements WorkingAreaProvider, EntityFilterable {
	private EntityPredicateEntry entityFilter = new EntityPredicateEntry();
	Predicate<EntityPredicateEntry.EntityPredicate> filterValidator = e -> !Objects.equals(e, EntityPredicateEntry.EntityPredicate.PLAYER);
	private int workingIndex = 0;
	
	public EntityInteractor(BlockPos pos, BlockState blockState) {
		super(ModMachines.ENTITY_INTERACTOR, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		setTank(() -> createBasicTank(16000, true, false));
		for (int i = 1; i < 13; i++) {
			outputSlots.add(i);
		}
		setInventory(() -> createBasicInventory(13, outputSlots, (slot, stack) -> slot == 0));
		this.isChargeProgress = true;
		screenInfo.setSlotPosition(45, 37, 0);
		screenInfo.setArrowPos(67, 34);
		for (int i = 0; i < 12; i++) {
			int x = i % 4;
			int y = i / 4;
			screenInfo.setSlotPosition(97 + x * 18, 19 + y * 18, i + 1);
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("EntityFilter", entityFilter.serializeNBT());
		tag.putInt("WorkingIndex", workingIndex);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("EntityFilter")) {
			entityFilter.deserializeNBT(tag.getCompound("EntityFilter"));
		}
		workingIndex = tag.getInt("WorkingIndex");
	}
	
	@Override
	public void saveContents(CompoundTag additionalTag) {
		super.saveContents(additionalTag);
		additionalTag.put("EntityFilter", entityFilter.serializeNBT());
		additionalTag.putInt("WorkingIndex", workingIndex);
	}
	
	@Override
	public void loadContents(CompoundTag additionalTag) {
		super.loadContents(additionalTag);
		if (additionalTag.contains("EntityFilter")) {
			entityFilter.deserializeNBT(additionalTag.getCompound("EntityFilter"));
		}
		workingIndex = additionalTag.getInt("WorkingIndex");
	}
	
	@Override
	public AABB getWorkingArea(BlockPos anchor) {
		Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		BlockPos center = anchor.relative(facing.getOpposite(), 5);
		return new AABB(center).inflate(4, 0.5, 4).move(0, 0.5, 0);
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoOutputFluids(lvl, pos);
		
		ItemStack interactStack = inventory.getStackInSlot(0);
		getFakePlayer();
		setupFakePlayer(st);
		if (fakePlayer != null) fakePlayer.getInventory().selected = 0;
		
		transferFluidToTank(interactStack);
		
		List<LivingEntity> entities = lvl.getEntitiesOfClass(LivingEntity.class, getWorkingArea(pos),
			e -> !(e instanceof Player) && entityFilter.matches(e));
		
		if (entities.isEmpty()) {
			progress = 0;
			workingIndex = 0;
			return;
		}
		
		if (workingIndex >= entities.size()) {
			workingIndex = 0;
		}

		LivingEntity entity = entities.get(workingIndex);
		progressCharge(entities::isEmpty, () -> {
			boolean isShears = interactStack.is(Tags.Items.SHEARS);
			var result = isShears ? InteractionResult.PASS : interactStack.interactLivingEntity(fakePlayer, entity, InteractionHand.MAIN_HAND);
			boolean success = false;
			if (!result.consumesAction()) {
				if (isShears && entity instanceof IShearable shearable && shearable.isShearable(interactStack, lvl, entity.blockPosition())) {
					var items = shearable.onSheared(fakePlayer, interactStack, lvl, entity.blockPosition(), interactStack.getEnchantmentLevel(Enchantments.MOB_LOOTING));
					interactStack.hurtAndBreak(1, fakePlayer, e -> {});
					for (ItemStack drop : items) {
						ItemStack remaining = ItemHelper.insertItemStackedForced(inventory, drop, false, outputSlots).getFirst();
						if (!remaining.isEmpty()) {
							shearable.spawnShearedDrop(lvl, entity.blockPosition(), drop);
						}
					}
					success = true;
				}
				if (entity instanceof Animal animal) {
					InteractionResult animalResult = animal.mobInteract(fakePlayer, InteractionHand.MAIN_HAND);
					if (animalResult.consumesAction()) {
						success = true;
					}
				}
			} else {
				success = true;
			}
			workingIndex++;
			return success;
		}, energy);
	}
	
	/**
	 * Transfers fluid from the modified item to the tank and restores the original empty container.
	 * For example: empty bucket -> milk bucket, extracts milk to tank and keeps empty bucket.
	 */
	private void transferFluidToTank(ItemStack after) {
		if (after.isEmpty() || tank == null) return;
		
		IFluidHandlerItem fluidHandler = after.getCapability(Capabilities.FluidHandler.ITEM);
		if (fluidHandler == null) return;
		
		FluidStack fluidInItem = fluidHandler.getFluidInTank(0);
		if (fluidInItem.isEmpty()) return;
		
		int filled = tank.fillForced(fluidInItem.copy(), IFluidHandler.FluidAction.SIMULATE);
		if (filled > 0) {
			tank.fillForced(fluidInItem.copyWithAmount(filled), IFluidHandler.FluidAction.EXECUTE);
			fluidHandler.drain(filled, IFluidHandler.FluidAction.EXECUTE);
			
			if (fluidHandler.getFluidInTank(0).isEmpty()) {
				ItemStack container = fluidHandler.getContainer();
				if (!container.isEmpty()) {
					inventory.setStackInSlot(0, container);
				}
			}
		}
	}
}
