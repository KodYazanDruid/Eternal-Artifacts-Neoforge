package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class EncumbatorItem extends ArtifactItem {

    public static final String ACTIVE = MODID+":active";
    private final String TOOLTIP = "key." + MODID + ".tooltip.encumbator";

    public EncumbatorItem(Properties pProperties) {
        super(pProperties);
    }

    public static boolean isStackActive(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(ACTIVE);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if(action == ClickAction.SECONDARY && slot.allowModification(player) && other.isEmpty()){
            CompoundTag tag = stack.getOrCreateTag();
            tag.putBoolean(ACTIVE, !tag.getBoolean(ACTIVE));
            return true;
        }else {
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if(stack.hasTag() && stack.getTag().getBoolean(ACTIVE)) tooltip.add(Component.translatable(TOOLTIP + ".active").withStyle(ChatFormatting.RED));
        else tooltip.add(Component.translatable(TOOLTIP + ".passive").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(TOOLTIP + ".switch"));
        super.appendHoverText(stack, level, tooltip, flag);
    }

}
