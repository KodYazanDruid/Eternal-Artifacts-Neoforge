package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.MultiFluidTank;
import com.sonamorningstar.eternalartifacts.container.AlchemicalBrewerMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AlchemicalBrewer extends SidedTransferMachine<AlchemicalBrewerMenu> {
	
	public AlchemicalBrewer(BlockPos pos, BlockState blockState) {
		super(ModMachines.ALCHEMICAL_BREWER.getBlockEntity(), pos, blockState,
			(a, b, c, d) -> new AlchemicalBrewerMenu(ModMachines.ALCHEMICAL_BREWER.getMenu(), a, b, c, d));
		setEnergy(this::createDefaultEnergy);
		setTank(() -> new MultiFluidTank<>(
			createBasicTank(16000, false, true),
			createBasicTank(16000, true, false)
		));
		setInventory(() -> createBasicInventory(1, (slot, stack) ->
			PotionBrewing.isPotionIngredient(stack)
		));
	}
	
	@Override
	protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
		condition.createCustomCondition(() -> !isBrewable());
		super.setProcessCondition(condition, recipe);
	}
	
	@Override
	protected InteractionResult useAfter(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack held = player.getItemInHand(hand);
		if (held.is(Items.GLASS_BOTTLE)) {
			int tankNo = 1;
			FluidStack fluid = tank.getFluidInTank(tankNo);
			if (fluid.isEmpty()) tankNo = 0;
			fluid = tank.getFluidInTank(tankNo);
			if (!fluid.isEmpty() && fluid.getAmount() >= 250) {
				ItemStack bottle = new ItemStack(Items.POTION);
				CompoundTag tag = bottle.getOrCreateTag();
				tag.putString("Potion", fluid.getTag().getString("Potion"));
				if (!player.isCreative()) {
					held.shrink(1);
				}
				if (held.isEmpty()) {
					player.setItemInHand(hand, bottle);
				} else if (!player.getInventory().add(bottle)) {
					player.drop(bottle, false);
				}
				tank.get(tankNo).drainForced(250, IFluidHandler.FluidAction.EXECUTE);
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		} else if (held.is(Items.POTION)) {
			FluidStack fluid = ModFluids.POTION.getFluidStack(250);
			Potion potion = PotionUtils.getPotion(held);
			CompoundTag tag = held.getOrCreateTag();
			fluid.setTag(tag);
			if (potion == Potions.WATER) {
				fluid = new FluidStack(Fluids.WATER, 250);
			}
			int filled = tank.get(0).fillForced(fluid, IFluidHandler.FluidAction.SIMULATE);
			if (filled == 250) {
				if (!player.isCreative()) {
					held.shrink(1);
					if (held.isEmpty()) {
						player.setItemInHand(hand, new ItemStack(Items.GLASS_BOTTLE));
					} else if (!player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE))) {
						player.drop(new ItemStack(Items.GLASS_BOTTLE), false);
					}
				}
				tank.get(0).fillForced(fluid, IFluidHandler.FluidAction.EXECUTE);
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		} else {
			FluidStack fluid = tank.getFluidInTank(0);
			if (fluid.isEmpty()) return InteractionResult.PASS;
			
			ItemStack tankCopy = held.copyWithCount(1);
			IFluidHandlerItem jarHandler = tankCopy.getCapability(Capabilities.FluidHandler.ITEM);
			
			if (jarHandler != null) {
				int filled = jarHandler.fill(fluid.copy(), IFluidHandler.FluidAction.SIMULATE);
				if (filled > 0) {
					jarHandler.fill(fluid.copy(), IFluidHandler.FluidAction.EXECUTE);
					tank.get(0).drainForced(filled, IFluidHandler.FluidAction.EXECUTE);
					Optional<SoundEvent> pickupSound = fluid.getFluid().getPickupSound();
					pickupSound.ifPresent(soundEvent -> level.playSound(null, pos, soundEvent, player.getSoundSource(), 1.0F, 1.0F));
					ItemStack filledTank = jarHandler.getContainer();
					if (!player.isCreative()) {
						held.shrink(1);
					}
					if (held.isEmpty()) {
						player.setItemInHand(hand, filledTank);
					} else if (!player.getInventory().add(filledTank)) {
						player.drop(filledTank, false);
					}
					return InteractionResult.sidedSuccess(level.isClientSide);
				}
			}
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoInputItems(lvl, pos);
		performAutoInputFluids(lvl, pos);
		performAutoOutputFluids(lvl, pos);
		
		progress(() -> {
			FluidStack result = getPotionResult(inventory.getStackInSlot(0), tank.getFluidInTank(0));
			tank.get(1).fillForced(result, IFluidHandler.FluidAction.EXECUTE);
			tank.get(0).drainForced(result.getAmount(), IFluidHandler.FluidAction.EXECUTE);
			inventory.extractItem(0, 1, false);
		});
	}
	
	private boolean isBrewable() {
		ItemStack ingredient = inventory.getStackInSlot(0);
		if (ingredient.isEmpty()) {
			return false;
		}
		
		FluidStack inputFluid = tank.getFluidInTank(0);
		if (inputFluid.isEmpty() || inputFluid.getAmount() < Config.BREW_AMOUNT.get()) {
			return false;
		}
		
		FluidStack outputFluid = getPotionResult(ingredient, inputFluid);
		if (outputFluid.isEmpty()) {
			return false;
		}
		
		int filled = tank.get(1).fillForced(outputFluid, IFluidHandler.FluidAction.SIMULATE);
		
		return filled == outputFluid.getAmount();
	}
	
	protected FluidStack getPotionResult(ItemStack ingredient, FluidStack inputFluid) {
		if (PotionBrewing.isPotionIngredient(ingredient)) {
			Potion output = mix(ingredient, inputFluid);
			if (output != Potions.EMPTY) {
				return createPotionFluid(output, Config.BREW_AMOUNT.get());
			}
		}
		return FluidStack.EMPTY;
	}
	
	/*for(PotionBrewing.Mix<Item> mix : PotionBrewing.CONTAINER_MIXES) {
		if (mix.from == item && mix.ingredient.test(ingredient)) {
			return mix.to;
		}
	}*/
	
	protected Potion mix(ItemStack ingredient, FluidStack inputFluid) {
		Potion result = Potions.EMPTY;
		if (!inputFluid.isEmpty()) {
			result = inputFluid.is(Fluids.WATER) ? Potions.WATER : PotionUtils.getPotion(inputFluid.getTag());
			for(PotionBrewing.Mix<Potion> mix1 : PotionBrewing.POTION_MIXES) {
				if (mix1.from == result && mix1.ingredient.test(ingredient)) {
					result = mix1.to;
				}
			}
		}
		return result;
	}
	
	protected FluidStack createPotionFluid(Potion potion, int amount) {
		ResourceLocation potionRl = BuiltInRegistries.POTION.getKey(potion);
		FluidStack fluid = ModFluids.POTION.getFluidStack(amount);
		fluid.getOrCreateTag().putString("Potion", potionRl.toString());
		return fluid;
	}
}
