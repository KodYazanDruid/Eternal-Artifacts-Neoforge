package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.capabilities.helper.FluidTankUtils;
import com.sonamorningstar.eternalartifacts.container.TankKnapsackMenu;
import com.sonamorningstar.eternalartifacts.container.TankKnapsacktemMenu;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class TankKnapsackItem extends Item {
    public TankKnapsackItem(Properties props) {
        super(props);
    }
    
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if(action == ClickAction.SECONDARY && slot.allowModification(player) && other.isEmpty()) {
            if(!player.level().isClientSide()) {
                openMenu(player, stack);
            }
            return true;
        } else if (action == ClickAction.SECONDARY && !other.isEmpty()) {
            if(!player.level().isClientSide()) {
                IFluidHandlerItem capability = other.getCapability(Capabilities.FluidHandler.ITEM);
                if (capability != null && !FluidTankUtils.isFluidHandlerEmpty(capability)) {
                    var result = FluidUtil.tryFillContainerAndStow(stack, capability, player.getCapability(Capabilities.ItemHandler.ENTITY), Integer.MAX_VALUE, player, true);
                    if (result.isSuccess()) {
                        slot.set(result.getResult());
                        access.set(capability.getContainer());
                    }
				}
			}
            return true;
        } else if (action == ClickAction.PRIMARY && slot.allowModification(player) && !other.isEmpty()) {
            if(!player.level().isClientSide()) {
                IFluidHandlerItem capability = other.getCapability(Capabilities.FluidHandler.ITEM);
                if (capability != null && !FluidTankUtils.isFluidHandlerFull(capability)) {
                    var result = FluidUtil.tryEmptyContainerAndStow(stack, capability, player.getCapability(Capabilities.ItemHandler.ENTITY), Integer.MAX_VALUE, player, true);
                    if (result.isSuccess()) {
                        slot.set(result.getResult());
                        access.set(capability.getContainer());
                    }
                }
            }
            return true;
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
	}
    
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
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
        player.openMenu(new SimpleMenuProvider((id, inv, p) ->
            new TankKnapsackMenu(id, inv, stack),
            stack.getHoverName()), buff -> buff.writeItem(stack));
    }
    
    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 18;
    }
    
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return getMaxStackSize(stack) == 1;
    }
}
