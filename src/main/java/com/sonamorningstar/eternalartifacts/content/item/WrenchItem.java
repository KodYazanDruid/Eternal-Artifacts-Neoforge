package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.machine.multiblock.MultiblockPatternHelper;
import com.sonamorningstar.eternalartifacts.api.machine.multiblock.OilDepositData;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.core.*;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
        if (!level.isClientSide()) {
            OilDepositData oilData = OilDepositData.get(((ServerLevel) level));
            long oilAmount = oilData.getOilAmount(player.chunkPosition(), ((ServerLevel) level));
            player.displayClientMessage(Component.literal("Oil deposit in this chunk: " + oilAmount + " mB"), true);
            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, hand);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        BlockPos pos = ctx.getClickedPos();
        
        if (!level.isClientSide() && level.getBlockState(pos).is(Blocks.MAGMA_BLOCK)) {
            OilDepositData data = OilDepositData.get(((ServerLevel) level));
            data.deposits.clear();
        }
        
        AtomicBoolean builtMultiblock = new AtomicBoolean(false);
        Multiblock.PATTERNS.forEach((multiblock, pattern) -> {
            var match = MultiblockPatternHelper.findMultiblockPattern(level, pos, pattern,
                    multiblock.getClickablePalmOffset(),
                    multiblock.getClickableThumbOffset(),
                    multiblock.getClickableFingerOffset()
            );
            
            if (match != null) {
                if (multiblock.isLockedHorizontally() && match.getForwards().getAxis() != Direction.Axis.Y) return;
          
                for (int d = 0; d < pattern.getDepth(); d++) {
                    for (int h = 0; h < pattern.getHeight(); h++) {
                        for (int w = 0; w < pattern.getWidth(); w++) {
                            var blockInWorld = match.getBlock(w, h, d);
                            BlockPos blockPos = blockInWorld.getPos();
                            
                            level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                            if (!blockInWorld.getState().isAir()) {
                                var cachedState = blockInWorld.getState();
                                var mbState = multiblock.getMultiblockBlock().get().defaultBlockState();
                                level.setBlockAndUpdate(blockPos, mbState);
                                
                                BlockEntity mbBE = level.getBlockEntity(blockPos);
                                BlockPos masterPos = match.getBlock(
                                    multiblock.getMasterPalmOffset(),
                                    multiblock.getMasterThumbOffset(),
                                    multiblock.getMasterFingerOffset()
                                ).getPos();
                                
                                if (mbBE instanceof AbstractMultiblockBlockEntity ambe) {
                                    if (!ambe.isMaster()) {
                                        ambe.setMasterOffsets(
                                            masterPos.getX() - blockPos.getX(),
                                            masterPos.getY() - blockPos.getY(),
                                            masterPos.getZ() - blockPos.getZ()
                                        );
                                    }
                                    ambe.setDeformState(cachedState);
                                    ambe.setPartIndex(w + h * pattern.getWidth() + d * pattern.getWidth() * pattern.getHeight());
                                }
                            }
                        }
                    }
                }
                
                var biw = match.getBlock(multiblock.getMasterPalmOffset(), multiblock.getMasterThumbOffset(), multiblock.getMasterFingerOffset());
                BlockEntity masterBe = level.getBlockEntity(biw.getPos());
                if (masterBe instanceof AbstractMultiblockBlockEntity ambe) {
                    ambe.setMaster(true);
                    ambe.setOrientation(match.getForwards(), match.getUp());
                    ambe.setMbWidth(pattern.getWidth());
                    ambe.setMbHeight(pattern.getHeight());
                    ambe.setMbDepth(pattern.getDepth());
                }
                
                LongSet disciples = new LongArraySet();
                match.cache.asMap().forEach((blockPos, blockInWorld) -> {
                    if (!blockInWorld.getState().isAir() && !blockPos.equals(biw.getPos())) {
                        disciples.add(blockPos.asLong());
                    }
                });
                
                if (masterBe instanceof AbstractMultiblockBlockEntity ambe) {
                    ambe.setDisciples(disciples);
                    ambe.onFormed(level, ambe.getBlockPos());
                }
                
                match.cache.asMap().forEach((blockPos, blockInWorld) -> {
                    level.invalidateCapabilities(blockPos);
                });
                
                builtMultiblock.set(true);
            }
        });
        
        if (builtMultiblock.get()) {
            ItemStack stack = ctx.getItemInHand();
            if (player != null) stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(ctx.getHand()));
            else stack.hurt(1, ctx.getLevel().getRandom(), null);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        
        return super.useOn(ctx);
    }
}
