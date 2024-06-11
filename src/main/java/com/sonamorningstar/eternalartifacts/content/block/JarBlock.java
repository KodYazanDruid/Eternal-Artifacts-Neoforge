package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.content.block.entity.JarBlockEntity;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

public class JarBlock extends BaseEntityBlock {
    public JarBlock(Properties pProperties) {
        super(pProperties);
    }

    private static final VoxelShape BODY = BlockHelper.generateByArea(8, 11, 8, 4, 0, 4);
    private static final VoxelShape LID = BlockHelper.generateByArea(6, 2, 6, 5, 10, 5);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(JarBlock::new);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof JarBlockEntity jar) jar.invalidateCapabilities();
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.join(BODY, LID, BooleanOp.OR);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_309084_, BlockGetter p_309133_, BlockPos p_309097_) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new JarBlockEntity(pPos, pState);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(super.use(state, level, pos, player, hand, hit) ==  InteractionResult.PASS) {
            IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
            if(fluidHandler != null && FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        IFluidHandlerItem fhi = FluidUtil.getFluidHandler(stack).get();
        FluidStack fluidStack = fhi.getFluidInTank(0);
        IFluidHandler fh = FluidUtil.getFluidHandler(level, pos, null).get();
        fh.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
    }

    //It may seem stupid but it works.
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
