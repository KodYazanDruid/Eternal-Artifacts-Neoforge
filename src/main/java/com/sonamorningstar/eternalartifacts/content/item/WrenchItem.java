package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModSounds;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class WrenchItem extends DiggerItem {
    public WrenchItem(Properties props) {super(2F, -2F, Tiers.IRON, ModTags.Blocks.MINEABLE_WITH_WRENCH, props); }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
        if(state.is(ModTags.Blocks.MINEABLE_WITH_WRENCH)) level.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.BLOCKS, 1, 1);
        return super.mineBlock(stack, level, state, pos, living);
    }
    //Testing and debugging stuff.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
        IFluidHandlerItem fluidHandlerItem = offHandStack.getCapability(Capabilities.FluidHandler.ITEM);
        player.sendSystemMessage(Component.literal(String.valueOf(fluidHandlerItem == null)));

        return super.use(level, player, hand);
    }

}
