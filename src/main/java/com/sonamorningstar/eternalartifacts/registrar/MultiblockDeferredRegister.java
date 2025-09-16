package com.sonamorningstar.eternalartifacts.registrar;

import com.sonamorningstar.eternalartifacts.content.block.base.MultiblockBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.MultiblockCapabilityManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MultiblockDeferredRegister {
	private final DeferredRegister<Multiblock> multiblockRegister;
	private final DeferredRegister<BlockEntityType<?>> blockEntityRegister;
	private final DeferredRegister.Blocks blockRegister;
	
	private final List<MultiblockDeferredHolder<?, ?, ?>> multiblocks = new ArrayList<>();
	
	private static final BlockBehaviour.Properties defaultProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).mapColor(MapColor.COLOR_GRAY).noLootTable();
	
	public MultiblockDeferredRegister(String modid) {
		this.multiblockRegister = DeferredRegister.create(ModRegistries.Keys.MULTIBLOCK, modid);
		this.blockEntityRegister = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modid);
		this.blockRegister = DeferredRegister.createBlocks(modid);
	}
	
	public void register(IEventBus bus) {
		this.multiblockRegister.register(bus);
		this.blockEntityRegister.register(bus);
		this.blockRegister.register(bus);
	}
	
	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		for (MultiblockDeferredHolder<?, ?, ?> holder : this.multiblocks) {
			holder.registerCapabilities(event);
		}
	}
	
	public List<MultiblockDeferredHolder<?, ?, ?>> getMultiblocks() {
		return Collections.unmodifiableList(this.multiblocks);
	}
	
	public List<DeferredHolder<Block, ? extends Block>> getBlockHolders() {
		return this.multiblocks.stream().map(MultiblockDeferredHolder::getBlockHolder)
			.collect(Collectors.toUnmodifiableList());
	}
	
	public <MB extends Multiblock, MBB extends AbstractMultiblockBlockEntity> MultiblockDeferredHolder<MB, MultiblockBlock, MBB> register(
			String name, BlockEntityType.BlockEntitySupplier<MBB> blockEntity,
			MultiblockSupplier<MB> multiblockSupplier,
			BlockPattern pattern, int masterPalmOffset, int masterThumbOffset, int masterFingerOffset, MultiblockCapabilityManager capabilityManager
		) {
		var multiblockHolder = this.multiblockRegister.register(name, () -> multiblockSupplier.create(
			pattern, masterPalmOffset, masterThumbOffset, masterFingerOffset, capabilityManager
		));
		var blockHolder = this.blockRegister.register(name, () -> new MultiblockBlock(defaultProperties, blockEntity));
		var beHolder = this.blockEntityRegister.register(name, () -> BlockEntityType.Builder.of(blockEntity, blockHolder.get()).build(null));
		
		var holder = new MultiblockDeferredHolder<>(multiblockHolder, beHolder, blockHolder);
		this.multiblocks.add(holder);
		
		return holder;
	}
	
	public interface MultiblockSupplier<MB extends Multiblock> {
		MB create(BlockPattern pattern, int masterPalmOffset, int masterThumbOffset, int masterFingerOffset, MultiblockCapabilityManager capabilityManager);
	}
}
