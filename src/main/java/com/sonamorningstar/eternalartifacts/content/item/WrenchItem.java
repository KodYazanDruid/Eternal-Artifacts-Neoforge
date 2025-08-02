package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.machine.multiblock.MultiblockPatternHelper;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.atomic.AtomicBoolean;

public class WrenchItem extends DiggerItem {
    public WrenchItem(Tier tier, Properties props) {super(2F, -2F, tier, ModTags.Blocks.MINEABLE_WITH_WRENCH, props); }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
        if(state.is(ModTags.Blocks.MINEABLE_WITH_WRENCH)) level.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.BLOCKS, 1, 1);
        return super.mineBlock(stack, level, state, pos, living);
    }

    //Testing and debugging stuff.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        return super.use(level, player, hand);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        
        AtomicBoolean builtMultiblock = new AtomicBoolean(false);
        Multiblock.PATTERNS.forEach((multiblock, pattern) -> {
            var match = MultiblockPatternHelper.findMultiblockPattern(level, pos, pattern);
            if (match != null && match.getForwards().getAxis() != Direction.Axis.Y) {
                match.cache.asMap().forEach((blockPos, blockInWorld) -> {
                    level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                    if (!blockInWorld.getState().isAir()) {
                        var state = multiblock.getMultiblockBlock().get().defaultBlockState();
                        level.setBlockAndUpdate(blockPos, state);
                        
                        BlockEntity mbBE = level.getBlockEntity(blockPos);
                        BlockPos masterPos = match.getBlock(multiblock.getMasterPalmOffset(), multiblock.getMasterThumbOffset(), multiblock.getMasterFingerOffset()).getPos();
                        if (mbBE instanceof AbstractMultiblockBlockEntity ambe) {
                            if (!ambe.isMaster()){
                                ambe.setMasterOffsets(
                                    masterPos.getX() - blockPos.getX(),
                                    masterPos.getY() - blockPos.getY(),
                                    masterPos.getZ() - blockPos.getZ()
                                );
                            } else {
                                ambe.setOrientation(match.getForwards(), match.getUp());
                            }
                        }
                    }
                });
                
                var biw = match.getBlock(multiblock.getMasterPalmOffset(), multiblock.getMasterThumbOffset(), multiblock.getMasterFingerOffset());
                BlockEntity masterBe = level.getBlockEntity(biw.getPos());
                if (masterBe instanceof AbstractMultiblockBlockEntity ambe) {
                    ambe.setMaster(true);
                }
                builtMultiblock.set(true);
            }
        });
        if (builtMultiblock.get()) return InteractionResult.sidedSuccess(level.isClientSide());
        
        return super.useOn(ctx);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        
        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }
}
