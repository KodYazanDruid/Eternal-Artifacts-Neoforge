package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.cables.CableNetwork;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.util.LootTableHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class WrenchItem extends DiggerItem {
    public WrenchItem(Properties props) {super(2F, -2F, Tiers.IRON, ModTags.Blocks.MINEABLE_WITH_WRENCH, props); }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
        if(state.is(ModTags.Blocks.MINEABLE_WITH_WRENCH)) level.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.BLOCKS, 1, 1);
        return super.mineBlock(stack, level, state, pos, living);
    }

    private IFluidHandler cachedHandler;

    CableNetwork cableNetwork;

    //Testing and debugging stuff.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        /*if (level instanceof ServerLevel sLevel) {
            List<Item> items = LootTableHelper.getItems(sLevel, EntityType.SKELETON.getDefaultLootTable());
            for (Item item : items) {
                System.out.println(item.toString());
            }
        }*/
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        ItemStack stack = ctx.getItemInHand();
        BlockState state = level.getBlockState(pos);

        if(cableNetwork == null) cableNetwork = new CableNetwork(level);

        cableNetwork.addCable(pos);
        if(level.isClientSide()) cableNetwork.printNetwork();

        return super.useOn(ctx);
    }
}
