package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
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
public class Tesseract extends ModBlockEntity implements MenuProvider {
	private static final String SELECTED_NETWORK = "SelectedNetwork";
	@Nullable
	private UUID networkId = null;
	@Nullable
	private TesseractNetwork<?> cachedTesseractNetwork = null;
	private TransferMode transferMode = TransferMode.BOTH;
	
	public Tesseract(BlockPos pos, BlockState state) {
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
		tag.putString("TransferMode", transferMode.name());
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains(SELECTED_NETWORK)) {
			networkId = tag.getUUID(SELECTED_NETWORK);
		}
		try {
			transferMode = TransferMode.valueOf(tag.getString("TransferMode"));
		} catch (IllegalArgumentException e) {
			EternalArtifacts.LOGGER.error("Invalid transfer mode in Tesseract NBT: \"{}\"", tag.getString("TransferMode"));
			transferMode = TransferMode.BOTH;
		}
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		setNetworkId(networkId);
	}
	
	@Override
	public void setRemoved() {
		super.setRemoved();
		if (!level.isClientSide()) TesseractNetworks.get(level).removeTesseractFromNetwork(this);
	}
	
	public void cycleTransfer() {
		transferMode = TransferMode.values()[(transferMode.ordinal() + 1) % TransferMode.values().length];
		invalidateCapabilities();
		sendUpdate();
	}
	
	public void setNetworkId(@Nullable UUID newId) {
		//TesseractNetwork id is only useful on screen and menu layout in client.
		//TesseractNetwork logic is handled on server side.
		if (level.isClientSide()) {
			if (newId == null) {
				this.networkId = null;
				this.cachedTesseractNetwork = null;
			} else {
				this.networkId = newId;
			}
		} else {
			if (newId != null) {
				TesseractNetworks.get(level).changeNetwork(this, networkId, newId);
				this.cachedTesseractNetwork = TesseractNetworks.getNetwork(newId, level);
			} else {
				TesseractNetworks.get(level).removeTesseractFromNetwork(this);
				this.cachedTesseractNetwork = null;
			}
			this.networkId = newId;
			invalidateCapabilities();
			sendUpdate();
		}
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
	
	public enum TransferMode {
		BOTH,
		EXTRACT_ONLY,
		INSERT_ONLY,
		NONE
	}
	
}
