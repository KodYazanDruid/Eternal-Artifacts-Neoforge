package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.machine.multiblock.MultiblockPatternHelper;
import com.sonamorningstar.eternalartifacts.client.render.util.DirectionRotationCache;
import com.sonamorningstar.eternalartifacts.content.block.base.AbstractPipeBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractPipeBlockEntity;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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

import java.util.HashSet;
import java.util.Set;
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
        /*int idx = 1;
        for (Direction forward : Direction.values()) {
            for (Direction up : Direction.values()) {
                var facing = PlayerHelper.getFacingDirection(player);
                var local = DirectionRotationCache.transform(forward, up, facing);
                if (local != null){
                    System.out.println("[" + idx++ + "] Player Facing: " + facing);
                    System.out.println("Forward: " + forward + ", Up: " + up + " -> Local: " + local);
                }
            }
        }*/
        return super.use(level, player, hand);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);
        BlockEntity be = level.getBlockEntity(pos);
        
        if (be instanceof AbstractPipeBlockEntity<?> pipe && player != null && !level.isClientSide()) {
            
            player.sendSystemMessage(Component.literal("Pipe Info:"));
            player.sendSystemMessage(Component.literal(" - Network Size: " + pipe.networkPipes.size()));
            player.sendSystemMessage(Component.literal(" - All Sources: " + pipe.allSources.size()));
            pipe.allSources.forEach((blockPos, cap) -> {
                player.sendSystemMessage(Component.literal("   - " + blockPos + " : " + cap.context().getName()));
            });
            player.sendSystemMessage(Component.literal(" - All Targets: " + pipe.allTargets.size()));
            pipe.allTargets.forEach((blockPos, cap) -> {
                player.sendSystemMessage(Component.literal("   - " + blockPos + " : " + cap.context().getName()));
            });
            player.sendSystemMessage(Component.literal(" - Sources: " + pipe.sources.size()));
            pipe.sources.forEach((blockPos, cap) -> {
                player.sendSystemMessage(Component.literal("   - " + blockPos + " : " + cap.context().getName()));
            });
            player.sendSystemMessage(Component.literal(" - Targets: " + pipe.targets.size()));
            pipe.targets.forEach((blockPos, cap) -> {
                player.sendSystemMessage(Component.literal("   - " + blockPos + " : " + cap.context().getName()));
            });
            pipe.sourceToTargetDistances.forEach((sourcePos, map) -> {
                map.forEach((targetPos, dist) -> {
                    player.sendSystemMessage(Component.literal(" - Distance from " + sourcePos + " to " + targetPos + " = " + dist));
                });
            });
        }
        
        AtomicBoolean builtMultiblock = new AtomicBoolean(false);
        Multiblock.PATTERNS.forEach((multiblock, pattern) -> {
            var match = MultiblockPatternHelper.findMultiblockPattern(level, pos, pattern);
            if (match != null) {
                if (multiblock.isLockedRotation() && match.getForwards().getAxis() != Direction.Axis.Y) return;
                match.cache.asMap().forEach((blockPos, blockInWorld) -> {
                    level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                    if (!blockInWorld.getState().isAir()) {
                        var cachedState = blockInWorld.getState();
                        var mbState = multiblock.getMultiblockBlock().get().defaultBlockState();
                        level.setBlockAndUpdate(blockPos, mbState);
                        
                        BlockEntity mbBE = level.getBlockEntity(blockPos);
                        BlockPos masterPos = match.getBlock(multiblock.getMasterPalmOffset(), multiblock.getMasterThumbOffset(), multiblock.getMasterFingerOffset()).getPos();
                        if (mbBE instanceof AbstractMultiblockBlockEntity ambe) {
                            if (!ambe.isMaster()){
                                ambe.setMasterOffsets(
                                    masterPos.getX() - blockPos.getX(),
                                    masterPos.getY() - blockPos.getY(),
                                    masterPos.getZ() - blockPos.getZ()
                                );
                            }
                            ambe.setDeformState(cachedState);
                        }
                    }
                });
                
                var biw = match.getBlock(multiblock.getMasterPalmOffset(), multiblock.getMasterThumbOffset(), multiblock.getMasterFingerOffset());
                BlockEntity masterBe = level.getBlockEntity(biw.getPos());
                if (masterBe instanceof AbstractMultiblockBlockEntity ambe) {
                    ambe.setMaster(true);
                    ambe.setOrientation(match.getForwards(), match.getUp());
                }
                
                Set<BlockPos> slaves = new HashSet<>();
                match.cache.asMap().forEach((blockPos, blockInWorld) -> {
                    if (!blockInWorld.getState().isAir() && !blockPos.equals(biw.getPos())) {
                        slaves.add(blockPos);
                    }
                });
                
                if (masterBe instanceof AbstractMultiblockBlockEntity ambe) {
                    ambe.setSlaves(slaves);
                }
                
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

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        
        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }
}
