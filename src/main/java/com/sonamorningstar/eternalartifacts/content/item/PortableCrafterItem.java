package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PortableCrafterItem extends ArtifactItem{
    public PortableCrafterItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if(action == ClickAction.SECONDARY && slot.allowModification(player) && other.isEmpty()){
            declare(player);
            return true;
        }else {
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        } else {
            declare(player);
            return InteractionResultHolder.consume(stack);
        }
    }

    private void declare(Player pplayer) {
            pplayer.openMenu(new SimpleMenuProvider((id, slots, player) -> new CraftingMenu(id, slots, ContainerLevelAccess.create(player.level(), player.blockPosition())) {
                @Override
                public boolean stillValid(Player pPlayer) {
                    return !PlayerHelper.findItem(pPlayer, PortableCrafterItem.this).isEmpty();
                }
            }, Component.translatable("container.crafting")));
            pplayer.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("inventory_right_click")).withStyle(ChatFormatting.GRAY));
    }
}
