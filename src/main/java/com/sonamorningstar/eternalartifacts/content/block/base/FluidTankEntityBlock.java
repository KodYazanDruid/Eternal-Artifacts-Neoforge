package com.sonamorningstar.eternalartifacts.content.block.base;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class FluidTankEntityBlock extends BaseEntityBlock {
    protected FluidTankEntityBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ModBlockEntity mbe) {
            mbe.loadEnchants(stack.getEnchantmentTags());
            for (Map.Entry<Enchantment, Integer> entry : mbe.enchantments.object2IntEntrySet()) {
                mbe.onEnchanted(entry.getKey(), entry.getValue());
            }
        }
        
        IFluidHandlerItem fhi = FluidUtil.getFluidHandler(stack).get();
        FluidStack fluidStack = fhi.getFluidInTank(0);
        IFluidHandler fh = FluidUtil.getFluidHandler(level, pos, null).get();
        fh.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
    }

    //It may seem stupid but it works.
    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        Block block = state.getBlock();
        ItemStack stack = new ItemStack(block);
        Level actualLevel = level.getBlockEntity(pos).getLevel();
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ModBlockEntity mbe) {
            mbe.enchantments.forEach(stack::enchant);
        }
        FluidStack fs = FluidStack.EMPTY;
        if(actualLevel != null) {
            IFluidHandler fluidHandler = actualLevel.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
            if(fluidHandler != null) {
                fs = fluidHandler.getFluidInTank(0);
            }
        }
        IFluidHandlerItem fhi = FluidUtil.getFluidHandler(stack).get();
        fhi.fill(fs, IFluidHandler.FluidAction.EXECUTE);
        
        return stack;
    }
}
