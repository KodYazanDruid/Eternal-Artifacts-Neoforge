package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnderPouchItem extends ArtifactItem {
    public EnderPouchItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if(action == ClickAction.SECONDARY && slot.allowModification(player) && other.isEmpty()){
            if(!player.level().isClientSide()) declare(player);
            return true;
        }else {
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if(pLevel.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }else{
            declare(pPlayer);
            return InteractionResultHolder.consume(stack);
        }
    }

    private void declare(Player player) {
        PlayerEnderChestContainer playerenderchestcontainer = player.getEnderChestInventory();
        Level level = player.level();
        level.playSound(
                null,
                player,
                SoundEvents.ENDER_CHEST_OPEN,
                SoundSource.PLAYERS,
                0.5F,
                level.random.nextFloat() * 0.1F + 0.9F
        );
        player.openMenu(new SimpleMenuProvider((row, menu, container) -> ChestMenu.threeRows(row, menu, playerenderchestcontainer), Component.translatable("container.enderchest")));
        player.awardStat(Stats.OPEN_ENDERCHEST);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("inventory_right_click")).withStyle(ChatFormatting.GRAY));
    }
}
