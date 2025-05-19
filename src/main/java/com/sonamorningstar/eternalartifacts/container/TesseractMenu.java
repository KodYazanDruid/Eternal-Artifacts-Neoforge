package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.Network;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TesseractMenu extends AbstractModContainerMenu {
	public final Level level;
	public final Tesseract tesseract;
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
		this.tesseract = ((Tesseract) blockEntity);
		gatheredNetworks = networks;
	}
	
	@Override
	public boolean stillValid(Player player) {
		return stillValid(ContainerLevelAccess.create(level, tesseract.getBlockPos()), player, tesseract.getBlockState().getBlock());
	}
	
	public void addNetwork(String name, Player owner, int secIdx, int capIdx) {
		Network<?> network = new Network<>(name, UUID.randomUUID(), owner.getGameProfile(), Network.CAPABILITY_NAMES.keySet().stream().toList().get(capIdx));
		network.setAccess(Network.Access.values()[secIdx]);
		TesseractNetworks.get(level).addNetwork(network);
		gatheredNetworks = TesseractNetworks.get(level).getNetworksForPlayer(owner);
		rebuildPanel((ServerPlayer) owner, false);
	}
	
	@Override
	public boolean clickMenuButton(Player player, int id) {
		Level level = player.level();
		TesseractNetworks networks = TesseractNetworks.get(level);
		if (networks == null || !(player instanceof ServerPlayer sp)) { return false; }
		if (id >= 1000 && id < 2000) {
			if (!gatheredNetworks.isEmpty()) {
				int networkIdx = id - 1000;
				Network<?> network = gatheredNetworks.get(networkIdx);
				if (network != null) {
					networks.removeNetwork(network);
					gatheredNetworks = networks.getNetworksForPlayer(player);
					rebuildPanel(sp, false);
				}
			}
			return true;
		} else if (id == 2000) {
			tesseract.setNetworkId(null);
			rebuildPanel(sp, true);
			return true;
		} else if (id == 3000) {
			tesseract.cycleTransfer();
			return true;
		} else if (id >= 5000) {
			int index = id - 5000;
			if (index < gatheredNetworks.size()) {
				Network<?> network = gatheredNetworks.get(index);
				tesseract.setNetworkId(network.getUuid());
				rebuildPanel(sp, false);
			}
			return true;
		}
		return super.clickMenuButton(player, id);
	}
	
	private void rebuildPanel(ServerPlayer player, boolean clearSelected) {
		Channel.sendToPlayer(new RebuildTesseractPanelToClient(clearSelected), player);
	}
}