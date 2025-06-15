package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.container.PipeFilterItemMenu;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PipeExtractor extends Item {
	public PipeExtractor(Properties props) {
		super(props);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(level.isClientSide()) {
			return InteractionResultHolder.success(stack);
		}else{
			openMenu(player, stack);
			return InteractionResultHolder.consume(stack);
		}
	}
	
	private void openMenu(Player player, ItemStack stack) {
		player.openMenu(new SimpleMenuProvider((id, inv, p) -> new PipeFilterItemMenu(id, 0, inv, stack), stack.getHoverName()),
			buff -> {
			buff.writeByte(0);
			buff.writeItem(stack);
		});
	}
}
