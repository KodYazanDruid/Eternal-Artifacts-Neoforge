package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.container.TesseractMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

@Getter
public class TesseractBlockEntity extends ModBlockEntity implements MenuProvider {
	private static final String SELECTED_NETWORK = "SelectedNetwork";
	@Nullable
	private UUID networkId = null;
	
	public TesseractBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.TESSERACT.get(), pos, state);
	}
	
	@Override
	protected boolean shouldSyncOnUpdate() {return true;}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (networkId != null) {
			tag.putUUID(SELECTED_NETWORK, networkId);
		}
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains(SELECTED_NETWORK)) {
			networkId = tag.getUUID(SELECTED_NETWORK);
		}
	}
	
	public void setNetworkId(@Nullable UUID networkId) {
		this.networkId = networkId;
		sendUpdate();
	}
	
	@Override
	public Component getDisplayName() {
		return getBlockState().getBlock().getName();
	}
	
	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new TesseractMenu(id, inv, this,
			TesseractNetworks.get(player.level()).getNetworksForPlayer(player));
	}
	
}
