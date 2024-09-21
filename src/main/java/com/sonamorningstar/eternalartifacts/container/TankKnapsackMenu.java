package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.capabilities.ModItemMultiFluidTank;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.content.recipe.inventory.FluidSlot;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;

@Getter
public class TankKnapsackMenu extends AbstractModContainerMenu {
    private final ItemStack knapsack;
    public int column = Config.TANK_KNAPSACK_SLOT_IN_ROW.get();
    public final Inventory playerInv;
    public TankKnapsackMenu(int id, Inventory inv, ItemStack knapsack) {
        super(ModMenuTypes.TANK_KNAPSACK.get(), id);
        this.knapsack = knapsack;
        this.playerInv = inv;
        int playerInvPadding = Math.max(0, column - 9) * 9;
        IFluidHandler fh = knapsack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fh instanceof ModItemMultiFluidTank<?> multiTank) {
            addPlayerInventoryAndHotbar(inv, 8 + playerInvPadding, (Mth.ceil((float) fh.getTanks() / column) * 18) + 12);
            for (int i = 0; i < fh.getTanks(); i++) {
                int x = i % column;
                int y = i / column;
                addFluidSlot(new FluidSlot(multiTank.getTank(i), i, 7 + x * 18, 17 + y * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {return ItemStack.EMPTY;}

    @Override
    public boolean stillValid(Player player) {
        return PlayerHelper.findStack(player, knapsack);
    }

    public static TankKnapsackMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf buff) {
        return new TankKnapsackMenu(id, inventory, buff.readItem());
    }

    public void handleTransfers(int tankNo, int button) {
        Player player = playerInv.player;
        FluidSlot slot = fluidSlots.get(tankNo);
        IFluidHandlerItem containerHandler = getCarried().getCapability(Capabilities.FluidHandler.ITEM);
        IItemHandler playerInventory = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if (containerHandler != null && playerInventory != null) {
            switch (button) {
                case 0 -> setCarried(drainSlotAndStow(slot, getCarried(), player));
                case 1 -> setCarried(fillSlotAndStow(slot, getCarried(), player));
            }
        }
    }
}
