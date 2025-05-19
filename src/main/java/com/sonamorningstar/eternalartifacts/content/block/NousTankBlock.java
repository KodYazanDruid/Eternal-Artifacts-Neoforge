package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.NousTank;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class NousTankBlock extends MachineFourWayBlock<NousTank> {
    public NousTankBlock(Properties pProperties) {
        super(pProperties, NousTank::new);
    }

    private static final VoxelShape SHAPE = BlockHelper.generateByArea(14, 14, 14, 1, 0, 1);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_309084_, BlockGetter p_309133_, BlockPos p_309097_) {
        return true;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        Block block = state.getBlock();
        Level actualLevel = level.getBlockEntity(pos).getLevel();
        FluidStack fs = FluidStack.EMPTY;
        if(actualLevel != null) {
            IFluidHandler fluidHandler = actualLevel.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
            if(fluidHandler != null) {
                fs = fluidHandler.getFluidInTank(0);
            }
        }
        ItemStack stack = new ItemStack(block);
        IFluidHandlerItem fhi = FluidUtil.getFluidHandler(stack).get();
        fhi.fill(fs, IFluidHandler.FluidAction.EXECUTE);
        return stack;
    }
}
