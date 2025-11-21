package com.sonamorningstar.eternalartifacts.content.item.block;

import com.sonamorningstar.eternalartifacts.client.render.BEWLRProps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class DISUBlockItem extends BlockItem {
	public DISUBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}
	
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(BEWLRProps.INSTANCE);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltips, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltips, flag);
		IItemHandler inv = stack.getCapability(Capabilities.ItemHandler.ITEM);
		if (inv != null) {
			ItemStack storedStack = inv.getStackInSlot(0);
			if (!storedStack.isEmpty()){
				int stored = storedStack.getCount();
				int capacity = inv.getSlotLimit(0);
				Component value = stored == capacity ?
					Component.literal(String.valueOf(stored)) :
					Component.literal(String.valueOf(stored)).append(" / ").append(String.valueOf(capacity));
				tooltips.add(storedStack.getDisplayName().copy().append(": ").append(value));
			}
		}
	}
}
