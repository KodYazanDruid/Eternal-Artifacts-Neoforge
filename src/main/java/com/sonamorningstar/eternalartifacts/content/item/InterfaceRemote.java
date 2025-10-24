package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.client.gui.widget.Warp;
import com.sonamorningstar.eternalartifacts.container.InterfaceRemoteMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class InterfaceRemote extends Item {
	public InterfaceRemote(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level lvl, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		Warp warp = stack.hasTag() ? Warp.readFromNBT(stack.getTag()) : null;
		if (warp != null && warp.getDimension() == lvl.dimension() && lvl.isLoaded(warp.getPosition())) {
			Direction side = Direction.from3DDataValue(stack.getTag().getInt("Side"));
			player.openMenu(new SimpleMenuProvider((id, inv, ply) -> new InterfaceRemoteMenu(id, inv, stack, warp, side), stack.getHoverName()),
				buf -> {
					buf.writeItem(stack);
					CompoundTag tag = new CompoundTag();
					warp.writeToNBT(tag);
					buf.writeNbt(tag);
					buf.writeEnum(side);
				});
			return InteractionResultHolder.sidedSuccess(stack, lvl.isClientSide());
		}
		return InteractionResultHolder.pass(stack);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		if (player != null && player.isShiftKeyDown()) {
			Level level = ctx.getLevel();
			if (!level.isClientSide()) {
				BlockPos pos = ctx.getClickedPos();
				ItemStack stack = ctx.getItemInHand();
				Warp warp = new Warp("Interface", level.dimension(), pos);
				CompoundTag stackTag = stack.getOrCreateTag();
				warp.writeToNBT(stackTag);
				Direction side = ctx.getClickedFace();
				stackTag.putInt("Side", side.get3DDataValue());
			}
			return InteractionResult.sidedSuccess(level.isClientSide());
		} else return super.useOn(ctx);
	}
}
