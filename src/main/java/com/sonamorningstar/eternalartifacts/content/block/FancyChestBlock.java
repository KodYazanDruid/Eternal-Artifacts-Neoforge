package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.entity.FancyChestBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.IRetexturedBlockEntity;
import com.sonamorningstar.eternalartifacts.content.item.block.base.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.util.BlockEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public class FancyChestBlock extends ChestBlock implements EntityBlock {
   public FancyChestBlock(Properties pProperties) {
      super(pProperties, ModBlockEntities.FANCY_CHEST::get);
   }
   
   @Override
   public RenderShape getRenderShape(BlockState state) {
       return RenderShape.MODEL;
   }
   
   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
       return new FancyChestBlockEntity(pPos, pState);
   }

   @Override
   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
       super.setPlacedBy(level, pos, state, placer, stack);
       updateTextureBlock(level, pos, stack);
   }

   @Override
   public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
       return getPickBlock(level, pos, state);
   }

   public static void updateTextureBlock(Level level, BlockPos pos, ItemStack stack) {
       if(stack.hasTag()) BlockEntityHelper.get(IRetexturedBlockEntity.class, level, pos).ifPresent(entity -> entity.updateTexture(RetexturedBlockItem.getTextureName(stack)));
   }

   private ItemStack getPickBlock(LevelReader level, BlockPos pos, BlockState state) {
       Block block = state.getBlock();
       ItemStack stack = new ItemStack(block);
       BlockEntityHelper.get(IRetexturedBlockEntity.class, level, pos).ifPresent(entity -> RetexturedBlockItem.setTexture(stack, entity.getTextureName()));
       return stack;
   }
   
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
       Direction direction = context.getHorizontalDirection().getOpposite();
       FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
       return this.defaultBlockState()
           .setValue(FACING, direction)
           .setValue(TYPE, ChestType.SINGLE)
           .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }
    
    public Block getTexture(BlockGetter level, BlockPos pos) {
        AtomicReference<Block> texture = new AtomicReference<>(Blocks.AIR);
        BlockEntityHelper.get(IRetexturedBlockEntity.class, level, pos).ifPresent(entity -> texture.set(entity.getTexture()));
        return texture.get();
    }
    
    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        Block texture = getTexture(level, pos);
        return texture.getSoundType(texture.defaultBlockState(), level, pos, entity);
    }
    
    @Override
    public boolean hasDynamicLightEmission(BlockState state) {
        return true;
    }
    
    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        //Block texture = getTexture(level, pos);
        AuxiliaryLightManager lightManager = level.getAuxLightManager(pos);
        return lightManager.getLightAt(pos);
        //return texture.getLightEmission(texture.defaultBlockState(), level, pos);
    }
    
    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        Block texture = getTexture(level, pos);
        return texture.getExplosionResistance(texture.defaultBlockState(), level, pos, explosion);
    }
    
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        Block texture = getTexture(level, pos);
        return texture.getDestroyProgress(texture.defaultBlockState(), player, level, pos);
    }
    
    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        Block texture = getTexture(level, pos);
        return super.canHarvestBlock(texture.defaultBlockState(), level, pos, player);
    }
}
