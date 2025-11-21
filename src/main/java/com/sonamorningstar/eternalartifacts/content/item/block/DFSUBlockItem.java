package com.sonamorningstar.eternalartifacts.content.item.block;

import com.sonamorningstar.eternalartifacts.client.render.BEWLRProps;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class DFSUBlockItem extends BasicFluidTankItem {
	public DFSUBlockItem(Block block, Properties props) {
		super(block, props);
	}
	
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(BEWLRProps.INSTANCE);
	}
}
