package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.BaseMachineBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.SolarPanel;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SolarPanelBlock extends SlabBlock implements EntityBlock {
	public SolarPanelBlock() {
		super(ModProperties.Blocks.MACHINE);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@Override
	public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof MachineBlockEntity<?> machine) {
			int resistance = machine.getEnchantmentLevel(Enchantments.BLAST_PROTECTION) + 1;
			return super.getExplosionResistance(state, level, pos, explosion) * resistance;
		}
		return super.getExplosionResistance(state, level, pos, explosion);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		super.use(state, level, pos, player, hand, hit);
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof MachineBlockEntity<?> mbe && mbe.canConstructMenu()) {
			AbstractMachineMenu.openContainer(player, pos);
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return InteractionResult.PASS;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SolarPanel(pos, state);
	}
	
	@Nullable
	@Override
	public <B extends BlockEntity> BlockEntityTicker<B> getTicker(Level level, BlockState pState, BlockEntityType<B> pBlockEntityType) {
		return new BaseMachineBlock.SimpleTicker<>(level.isClientSide());
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof MachineBlockEntity<?> mbe && stack.hasTag()) {
			mbe.loadEnchants(stack.getEnchantmentTags());
			for (Map.Entry<Enchantment, Integer> entry : mbe.enchantments.object2IntEntrySet()) {
				mbe.onEnchanted(entry.getKey(), entry.getValue());
			}
		}
		IEnergyStorage energyStack = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if (energyStack != null) {
			IEnergyStorage energy = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null);
			if (energy != null) {
				if (energy instanceof ModEnergyStorage mes) mes.setEnergy(energyStack.getEnergyStored());
				else energy.receiveEnergy(energyStack.getEnergyStored(), false);
			}
		}
		if (be instanceof MachineBlockEntity<?> mbe && stack.hasTag()) {
			CompoundTag nbt = stack.getTag();
			if (nbt.contains("MachineData")) {
				mbe.loadContents(nbt.getCompound("MachineData"));
			}
		}
	}
}
