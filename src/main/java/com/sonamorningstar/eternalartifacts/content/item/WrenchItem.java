package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractPipeBlockEntity;
import com.sonamorningstar.eternalartifacts.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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
        ItemStack stack = ctx.getItemInHand();
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);
        
        /*if (!level.isClientSide() && be instanceof AbstractPipeBlockEntity<?> pipe) {
            System.out.println("Pipe: " + pipe.getBlockPos());
            System.out.println("Printing sources: ");
            pipe.allSources.forEach((k, v) -> {
                System.out.println("Source: " + k);
                System.out.println("Source Cap: " + v.getCapability());
            });
            System.out.println("Printing targets: ");
            pipe.allTargets.forEach((k, v) -> {
                System.out.println("Target: " + k);
                System.out.println("Target Cap: " + v.getCapability());
            });
        }*/
        /*if (!level.isClientSide() && be instanceof Tesseract tesseract) {
            var map = TesseractNetworks.get(level).getTesseracts();
            map.forEach((k, v) -> {
                System.out.println("TesseractNetwork ID: " + k.getUuid());
                System.out.println("TesseractNetwork: " + k.getName());
                System.out.println("Type: " + k.getCapabilityClass().getSimpleName());
                System.out.println("Tesseracts: [");
                for (Tesseract tesseract1 : v) {
                    System.out.println("  Tesseract: " + tesseract1.getBlockPos());
                    System.out.println("  TesseractNetwork ID: " + tesseract1.getNetworkId());
                    System.out.println("  TesseractNetwork: " + tesseract1.getCachedTesseractNetwork().getName());
                    System.out.println("  Tesseract: " + tesseract1);
                    System.out.println("  ");
                }
                System.out.println("]");
            });
        }*/
        
        return super.useOn(ctx);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        
        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }
}
