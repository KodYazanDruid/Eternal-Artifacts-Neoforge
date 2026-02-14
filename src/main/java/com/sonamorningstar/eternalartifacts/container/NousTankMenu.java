package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.NousTank;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.util.ExperienceHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class NousTankMenu extends AbstractMachineMenu {
    NousTank nousTank;
    public NousTankMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }
    public NousTankMenu( int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.NOUS_TANK.get(), pContainerId, inv, entity, data);
        nousTank = (NousTank) entity;
    }
    
    /*public boolean depositLevels(Player player, int levels) {
        int wantedXp = levels == Integer.MAX_VALUE ? Integer.MAX_VALUE : ExperienceHelper.totalXpForLevel(levels);
        int totalPlayerXp = ExperienceHelper.getTotalPlayerXp(player);
        int toDeposit = Math.min(wantedXp, totalPlayerXp) * 20;
        AbstractFluidTank tank = nousTank.tank;
        int emptySpace = tank.getEmptySpace(0);
        int toFill = Math.min(toDeposit, emptySpace);
        if (toFill > 0) {
            FluidStack fluidStack = tank.getFluidInTank(0);
            Fluid fluid = fluidStack.isEmpty() ? ModFluids.NOUS.getFluid() : fluidStack.getFluid();
            FluidStack toInsert = new FluidStack(fluid, toFill);
            int xpToTake = toFill / 20;
            tank.fillForced(toInsert, IFluidHandler.FluidAction.EXECUTE);
            ExperienceHelper.givePlayerXpSilent(player, -xpToTake);
            return true;
        }
        return false;
    }
    
    public boolean takeLevels(Player player, int levels) {
        int wantedXp = levels == Integer.MAX_VALUE ? Integer.MAX_VALUE : ExperienceHelper.totalXpForLevel(levels);
        AbstractFluidTank tank = nousTank.tank;
        int availableXp = tank.getFluidInTank(0).getAmount() / 20;
        int toTake = Math.min(wantedXp, availableXp);
        if (toTake > 0) {
            tank.drainForced(toTake * 20, IFluidHandler.FluidAction.EXECUTE);
            ExperienceHelper.givePlayerXpSilent(player, toTake);
            return true;
        }
        return false;
    }*/
    
    /**
     * Deposits XP from player to tank based on player's current level.
     * E.g., if player is at level 40 and deposits 10 levels,
     * the XP difference between level 40 and level 30 will be transferred.
     */
    public boolean depositLevels(Player player, int levels) {
        int currentTotalXp = ExperienceHelper.getTotalPlayerXp(player);
        if (currentTotalXp <= 0) return false;
        
        int xpToDeposit;
        if (levels == Integer.MAX_VALUE) {
            xpToDeposit = currentTotalXp;
        } else {
            int currentLevel = player.experienceLevel;
            if (currentLevel <= 0) return false;
            
            int targetLevel = Math.max(0, currentLevel - levels);
            int targetTotalXp = ExperienceHelper.totalXpForLevel(targetLevel);
            xpToDeposit = currentTotalXp - targetTotalXp;
            
            if (xpToDeposit <= 0) return false;
        }
        
        AbstractFluidTank tank = nousTank.tank;
        int emptySpace = tank.getEmptySpace(0);
        int maxXpBySpace = emptySpace / 20;
        int actualXpToDeposit = Math.min(xpToDeposit, maxXpBySpace);
        
        if (actualXpToDeposit > 0) {
            FluidStack fluidStack = tank.getFluidInTank(0);
            Fluid fluid = fluidStack.isEmpty() ? ModFluids.NOUS.getFluid() : fluidStack.getFluid();
            FluidStack toInsert = new FluidStack(fluid, actualXpToDeposit * 20);
            tank.fillForced(toInsert, IFluidHandler.FluidAction.EXECUTE);
            ExperienceHelper.givePlayerXpSilent(player, -actualXpToDeposit);
            return true;
        }
        return false;
    }
    
    /**
     * Takes XP from tank and gives to player based on player's current level.
     * E.g., if player is at level 20 and takes 10 levels,
     * the XP difference between level 30 and level 20 will be transferred.
     */
    public boolean takeLevels(Player player, int levels) {
        AbstractFluidTank tank = nousTank.tank;
        int availableXp = tank.getFluidInTank(0).getAmount() / 20;
        if (availableXp <= 0) return false;
        
        int actualXpToTake;
        if (levels == Integer.MAX_VALUE) {
            actualXpToTake = availableXp;
        } else {
            int currentLevel = player.experienceLevel;
            int targetLevel = currentLevel + levels;
            
            int currentTotalXp = ExperienceHelper.getTotalPlayerXp(player);
            int targetTotalXp = ExperienceHelper.totalXpForLevel(targetLevel);
            int xpNeeded = targetTotalXp - currentTotalXp;
            
            if (xpNeeded <= 0) return false;
            
            actualXpToTake = Math.min(xpNeeded, availableXp);
        }
		
		tank.drainForced(actualXpToTake * 20, IFluidHandler.FluidAction.EXECUTE);
		ExperienceHelper.givePlayerXpSilent(player, actualXpToTake);
		return true;
	}
    
    @Override
    public boolean clickMenuButton(Player player, int id) {
        return switch (id) {
            case 0 -> depositLevels(player, 1);
            case 1 -> depositLevels(player, 10);
            case 2 -> depositLevels(player, Integer.MAX_VALUE);
            case 3 -> takeLevels(player, Integer.MAX_VALUE);
            case 4 -> takeLevels(player, 10);
            case 5 -> takeLevels(player, 1);
            default -> false;
        };
    }
}
