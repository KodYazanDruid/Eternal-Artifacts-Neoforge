package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.Network;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.TesseractBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.RebuildTesseractPanelToClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TesseractMenu extends AbstractModContainerMenu {
	public final Level level;
	public final TesseractBlockEntity tesseract;
	public volatile static List<Network<?>> gatheredNetworks;
	
	public TesseractMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		this(id, inv,
			inv.player.level().getBlockEntity(extraData.readBlockPos()),
			extraData.readCollection(ArrayList::new, rd -> Network.fromNBT(rd.readNbt()))
		);
	}
	
	public TesseractMenu(int id, Inventory inv, BlockEntity blockEntity, List<Network<?>> networks) {
		super(ModMenuTypes.TESSERACT.get(), id);
		this.level = inv.player.level();
		this.tesseract = ((TesseractBlockEntity) blockEntity);
		gatheredNetworks = networks;
	}
	
	@Override
	public boolean stillValid(Player player) {
		return stillValid(ContainerLevelAccess.create(level, tesseract.getBlockPos()), player, tesseract.getBlockState().getBlock());
	}
	
	@Override
	public boolean clickMenuButton(Player player, int id) {
		Level level = player.level();
		TesseractNetworks networks = TesseractNetworks.get(level);
		if (networks == null || !(player instanceof ServerPlayer sp)) { return false; }
		if (id == 0) {
			Network<IEnergyStorage> network = new Network<>(gatheredNetworks.size() + 1 + ". Network",
				UUID.randomUUID(), player.getUUID(), IEnergyStorage.class);
			networks.addNetwork(network);
			gatheredNetworks = networks.getNetworksForPlayer(player);
			rebuildPanel(sp);
			return true;
		}
		if (id == 1) {
			if (!gatheredNetworks.isEmpty()) {
				Network<?> network = gatheredNetworks.get(gatheredNetworks.size() - 1);
				networks.removeNetwork(network);
				gatheredNetworks = networks.getNetworksForPlayer(player);
				rebuildPanel(sp);
			}
			return true;
		}
		if (id == 2) {
			tesseract.setNetworkId(null);
			rebuildPanel(sp);
			return true;
		}
		if (id >= 50) {
			int index = id - 50;
			if (index < gatheredNetworks.size()) {
				Network<?> network = gatheredNetworks.get(index);
				tesseract.setNetworkId(network.getUuid());
				rebuildPanel(sp);
			}
			return true;
		}
		return super.clickMenuButton(player, id);
	}
	
	private void rebuildPanel(ServerPlayer player) {
		Channel.sendToPlayer(new RebuildTesseractPanelToClient(), player);
	}
}
