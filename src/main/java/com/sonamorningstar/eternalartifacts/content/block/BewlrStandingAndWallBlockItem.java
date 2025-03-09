package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.client.renderer.BEWLRProps;
import net.minecraft.core.Direction;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class BewlrStandingAndWallBlockItem extends StandingAndWallBlockItem {
	public BewlrStandingAndWallBlockItem(Block pBlock, Block pWallBlock, Properties pProperties, Direction pAttachmentDirection) {
		super(pBlock, pWallBlock, pProperties, pAttachmentDirection);
	}
	
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(BEWLRProps.INSTANCE);
	}
}
