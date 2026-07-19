package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.item.ToolBlockPlaceContext;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.network.BlockPlaceOnClient;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.util.ExperienceHelper;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import com.sonamorningstar.eternalartifacts.util.collections.ListIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.ToolActions;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Iterator;
import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class ChiselItem extends DiggerItem {
    public ChiselItem(Tier tier, Properties props) {
        super(1, -3.0F, tier, BlockTags.MINEABLE_WITH_PICKAXE, props);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        if (stack.getEnchantmentLevel(ModEnchantments.VERSATILITY.get()) > 0) {
            return ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction) ||
                    ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction) ||
                    ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
        } else return super.canPerformAction(stack, toolAction);
    }
    
    @SubscribeEvent
    public static void onBlockDrops(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        ItemStack handItem = player.getMainHandItem();
        if (!(handItem.getItem() instanceof ChiselItem)) return;
        if (!(event.getLevel() instanceof ServerLevel sLevel)) return;
        
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        if (!state.canHarvestBlock(sLevel, pos, player)) return;
        
        BlockHitResult hitResult = RayTraceHelper.retrace(player, ClipContext.Fluid.NONE);;
        ListIterator<ItemStack> blocks = PlayerHelper.itemWithClassIterable(player, BlockItem.class);
        ItemStack offHand = player.getOffhandItem();
        if (!offHand.isEmpty() && offHand.getItem() instanceof BlockItem) {
            blocks.putFirst(offHand);
        }
        
        List<ItemStack> drops = Block.getDrops(state, sLevel, pos, sLevel.getBlockEntity(pos), player, handItem);
        if (tryPlace(blocks, player, state, pos, hitResult)) {
            if (!player.isCreative()) {
                drops.forEach(drop -> PlayerHelper.giveItemOrPop(player, drop, pos.getX(), pos.getY(), pos.getZ()));
                int expToDrop = event.getExpToDrop();
                int remainingXp = ExperienceHelper.mendItem(handItem, expToDrop);
                if (remainingXp > 0) player.giveExperiencePoints(remainingXp);
            }
            event.setExpToDrop(-1);
        }
    }
    
    private static boolean tryPlace(Iterator<ItemStack> iterator, Player player,
                                    BlockState brokenState, BlockPos pos, BlockHitResult hitResult) {
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            
            if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem blockItem))
                continue;
            
            if (blockItem.getBlock() == brokenState.getBlock())
                continue;
            
            ItemStack clientStack = stack.copy();
            
            ToolBlockPlaceContext ctx = new ToolBlockPlaceContext(
                player, InteractionHand.MAIN_HAND, stack, pos, hitResult
            );
            blockItem.updatePlacementContext(ctx);
            ctx.replaceClicked = true;
            
            if (blockItem.place(ctx).consumesAction()) {
                ServerPlayerGameMode gameMode = ((ServerPlayer) player).gameMode;
                gameMode.delayedTickStart = gameMode.gameTicks;
                gameMode.hasDelayedDestroy = false;
                player.level().destroyBlockProgress(player.getId(), pos, -1);
                Channel.sendToPlayer(
                    new BlockPlaceOnClient(clientStack, pos, InteractionHand.MAIN_HAND, hitResult),
                    (ServerPlayer) player
                );
                return true;
            }
        }
        return false;
    }
}
