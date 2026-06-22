package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.content.block.base.FluidTankEntityBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.DrumBlockEntity;
import com.sonamorningstar.eternalartifacts.content.entity.PrimedBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.event.custom.DrumExplosionEvent;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class DrumBlock extends FluidTankEntityBlock {
    @Getter
    private final int capacity;
    private static final VoxelShape shape = BlockHelper.generateByArea(14, 15, 14, 1, 0 ,1);

    public DrumBlock(Properties props, int capacity) {
        super(props);
        this.capacity = capacity;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {return shape;}

    @Override
    public RenderShape getRenderShape(BlockState pState) {return RenderShape.MODEL;}

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {return simpleCodec(p-> new DrumBlock(p, capacity));}

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return new DrumBlockEntity(pos, state);}
    
    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        if (fluidHandler != null) {
            float fluidAmount = fluidHandler.getFluidInTank(0).getAmount();
            boolean isEmpty = fluidAmount == 0;
            float capacity = fluidHandler.getTankCapacity(0);
            return Math.min(Mth.floor(fluidAmount / capacity * 14.0F) + (isEmpty ? 0 : 1), 15);
        }
        return super.getAnalogOutputSignal(state, level, pos);
    }
    
    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        if (fluidHandler != null) {
            DrumExplosionEvent event = new DrumExplosionEvent(fluidHandler.getFluidInTank(0), null, defaultBlockState());
            if (!NeoForge.EVENT_BUS.post(event).isCanceled() && event.getFuseTime() > 0 && !defaultBlockState().is(ModBlocks.NETHERITE_DRUM)) {
                Vector3d position = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                PrimedBlockEntity primedBlock = new PrimedBlockEntity(level, position, null,
                    event.getFuseTime(), event.getRadius(), event.getState());
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
                level.addFreshEntity(primedBlock);
            }
        }
    }
    
    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!projectile.isOnFire()) return;
        IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, hit.getBlockPos(), null);
        if (fluidHandler != null) {
            Player player = projectile.getOwner() instanceof Player ? (Player) projectile.getOwner() : null;
            DrumExplosionEvent event = new DrumExplosionEvent(fluidHandler.getFluidInTank(0), player, state);
            if (!NeoForge.EVENT_BUS.post(event).isCanceled() && event.getFuseTime() > 0 && !state.is(ModBlocks.NETHERITE_DRUM)) {
                Vector3d position = new Vector3d(hit.getBlockPos().getX() + 0.5D, hit.getBlockPos().getY() + 0.5D, hit.getBlockPos().getZ() + 0.5D);
                PrimedBlockEntity primedBlock = new PrimedBlockEntity(level, position, null,
                    event.getFuseTime(), event.getRadius(), event.getState());
                level.setBlock(hit.getBlockPos(), Blocks.AIR.defaultBlockState(), 11);
                level.addFreshEntity(primedBlock);
            }
        }
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        if(fluidHandler != null && FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) return InteractionResult.sidedSuccess(level.isClientSide());
        else if (fluidHandler != null){
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.FIRE_CHARGE)) {
                DrumExplosionEvent event = new DrumExplosionEvent(fluidHandler.getFluidInTank(0), player, state);
                if (!NeoForge.EVENT_BUS.post(event).isCanceled() && event.getFuseTime() > 0 && !state.is(ModBlocks.NETHERITE_DRUM)) {
                    Vector3d position = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                    PrimedBlockEntity primedBlock = new PrimedBlockEntity(level, position, event.getPlayer(), event.getFuseTime(), event.getRadius(), event.getState());
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
                    level.addFreshEntity(primedBlock);
                    level.gameEvent(player, GameEvent.PRIME_FUSE, pos);
                    if (stack.is(Items.FLINT_AND_STEEL)) stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                    else if (stack.is(Items.FIRE_CHARGE)) stack.shrink(1);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }
}
