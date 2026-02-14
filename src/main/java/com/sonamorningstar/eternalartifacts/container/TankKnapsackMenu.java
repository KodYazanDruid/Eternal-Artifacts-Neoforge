package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModItemMultiFluidTank;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FluidSlot;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

@Getter
public class TankKnapsackMenu extends AbstractModContainerMenu {
    private final ItemStack knapsack;
    public int column = Config.TANK_KNAPSACK_SLOT_IN_ROW.get();
    public final Inventory playerInv;
    public TankKnapsackMenu(int id, Inventory inv, ItemStack knapsack) {
        super(ModMenuTypes.TANK_KNAPSACK.get(), id, inv);
        this.knapsack = knapsack;
        this.playerInv = inv;
        int playerInvPadding = Math.max(0, column - 9) * 9;
        IFluidHandler fh = knapsack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fh instanceof ModItemMultiFluidTank<?>) {
            addPlayerInventoryAndHotbar(inv, 8 + playerInvPadding, (Mth.ceil((float) fh.getTanks() / column) * 18) + 12);
            for (int i = 0; i < fh.getTanks(); i++) {
                int x = i % column;
                int y = i / column;
                addFluidSlot(new FluidSlot(() -> (ModItemMultiFluidTank<?>) knapsack.getCapability(Capabilities.FluidHandler.ITEM), i, 7 + x * 18, 17 + y * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = getSlot(index);
        ItemStack slotItem = slot.getItem();
        IFluidHandlerItem knapsackCap = knapsack.getCapability(Capabilities.FluidHandler.ITEM);
        IFluidHandlerItem capability = slotItem.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability != null && knapsackCap != null) {
            var result = FluidUtil.tryEmptyContainerAndStow(slotItem, knapsackCap, player.getCapability(Capabilities.ItemHandler.ENTITY), Integer.MAX_VALUE, player, true);
            if (result.isSuccess()) {
                slot.set(result.getResult());
                return ItemStack.EMPTY;
            }
        }
        return super.quickMoveStack(player, index);
    }

    @Override
    public boolean stillValid(Player player) {
        return PlayerHelper.findExactStack(player, knapsack);
    }

    public static TankKnapsackMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf buff) {
        return new TankKnapsackMenu(id, inventory, buff.readItem());
    }
}
