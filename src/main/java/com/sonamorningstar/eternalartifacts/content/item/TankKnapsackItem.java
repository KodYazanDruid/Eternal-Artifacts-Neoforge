package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.container.TankKnapsackMenu;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.SyncFluidSlotsToClient;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class TankKnapsackItem extends Item {
    public TankKnapsackItem(Properties props) {
        super(props);
    }
    
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (!slot.allowModification(player)) return false;
        if(action == ClickAction.SECONDARY && slot.allowModification(player) && other.isEmpty()) {
            if(!player.level().isClientSide()) {
                openMenu(player, stack);
            }
            return true;
        }
        
        IFluidHandlerItem knapsackCap = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (knapsackCap == null || other.isEmpty()) return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        
        if (action == ClickAction.PRIMARY) {
            if(!player.level().isClientSide()) {
                IFluidHandlerItem capability = other.getCapability(Capabilities.FluidHandler.ITEM);
                if (capability != null) {
                    var result = FluidUtil.tryFillContainerAndStow(other, knapsackCap, player.getCapability(Capabilities.ItemHandler.ENTITY), Integer.MAX_VALUE, player, true);
                    if (result.isSuccess()) {
                        access.set(result.getResult());
                    }
                    updateContainer((ServerPlayer) player, stack);
                }
            }
            return true;
        }
        
        if (action == ClickAction.SECONDARY) {
            if(!player.level().isClientSide()) {
                IFluidHandlerItem capability = other.getCapability(Capabilities.FluidHandler.ITEM);
                if (capability != null) {
                    var result = FluidUtil.tryEmptyContainerAndStow(other, knapsackCap, player.getCapability(Capabilities.ItemHandler.ENTITY), Integer.MAX_VALUE, player, true);
                    if (result.isSuccess()) {
                        access.set(result.getResult());
                    }
                    updateContainer((ServerPlayer) player, stack);
                }
            }
            return true;
        }
        
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
	}
    
    private void updateContainer(ServerPlayer player, ItemStack stack) {
        if(player.containerMenu instanceof TankKnapsackMenu menu) {
            IFluidHandlerItem capability = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (capability != null) {
                NonNullList<FluidStack> fluids = NonNullList.withSize(capability.getTanks(), FluidStack.EMPTY);
                for (int i = 0; i < capability.getTanks(); i++) {
                    FluidStack fluid = capability.getFluidInTank(i);
                    menu.getFluidSlot(i).setFluid(fluid.copy());
                    fluids.set(i, fluid.copy());
                }
                Channel.sendToPlayer(new SyncFluidSlotsToClient(fluids), player);
            }
        }
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
