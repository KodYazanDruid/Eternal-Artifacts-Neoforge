package com.sonamorningstar.eternalartifacts.content.item.block;

import com.sonamorningstar.eternalartifacts.client.render.BEWLRProps;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class TesseractItem extends BlockItem {
	public TesseractItem(Properties properties) {
		super(ModBlocks.TESSERACT.get(), properties);
	}
	
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(BEWLRProps.INSTANCE);
	}
	
}
