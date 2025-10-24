package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.container.PipeFilterItemMenu;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class PipeAttachmentItem extends Item {
	private final int type;
	public PipeAttachmentItem(int type, Properties props) {
		super(props);
		this.type = type;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		CompoundTag tag = stack.hasTag() ? stack.getTag() : new CompoundTag();
		CompoundTag filterData = tag.getCompound("FilterData");
		boolean isWhitelist = filterData.getBoolean("whitelist");
		boolean ignoresNbt = filterData.getBoolean("ignore_nbt");
		tooltip.add(ModConstants.GUI.withSuffixTranslatable(isWhitelist ? "whitelist" : "blacklist").withStyle(ChatFormatting.YELLOW));
		tooltip.add(ModConstants.GUI.withSuffixTranslatable(ignoresNbt ? "pipe_filter_ignore_nbt" : "pipe_filter_nbt_tolerant").withStyle(ChatFormatting.YELLOW));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(level.isClientSide()) return InteractionResultHolder.success(stack);
		else {
			openMenu(player, stack);
			return InteractionResultHolder.consume(stack);
		}
	}
	
	private void openMenu(Player player, ItemStack stack) {
		player.openMenu(new SimpleMenuProvider((id, inv, p) -> new PipeFilterItemMenu(id, type, inv, (short) ItemHelper.getSlot(inv, stack)), stack.getHoverName()),
			buff -> {
				buff.writeByte(type);
				buff.writeShort(ItemHelper.getSlot(player.getInventory(), stack));
		});
	}
}
