package com.sonamorningstar.eternalartifacts.registrar;

import com.sonamorningstar.eternalartifacts.content.block.base.MultiblockBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@RequiredArgsConstructor
public class MultiblockDeferredHolder<MB extends Multiblock, B extends MultiblockBlock, BE extends AbstractMultiblockBlockEntity> {
	private final DeferredHolder<Multiblock, MB> multiblock;
	private final DeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> blockEntity;
	private final DeferredHolder<Block, B> block;
	
	public DeferredHolder<Multiblock, MB> getMultiblockHolder() {return this.multiblock; }
	public MB getMultiblock() {return getMultiblockHolder().get(); }
	public DeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> getBlockEntityHolder() {return this.blockEntity; }
	public BlockEntityType<BE> getBlockEntity() {return getBlockEntityHolder().get(); }
	public DeferredHolder<Block, B> getBlockHolder() {return this.block; }
	public B getBlock() {return getBlockHolder().get(); }
	
	public String getTranslationKey() {return getBlock().getDescriptionId();}
	
	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, getBlockEntity(), AbstractMultiblockBlockEntity::getEnergy);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, getBlockEntity(), AbstractMultiblockBlockEntity::getTank);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, getBlockEntity(), AbstractMultiblockBlockEntity::getInventory);
	}
}
