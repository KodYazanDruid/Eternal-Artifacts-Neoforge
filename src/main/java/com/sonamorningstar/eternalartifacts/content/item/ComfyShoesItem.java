package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModArmorMaterials;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTiers;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;


public class ComfyShoesItem extends ArmorItem {

    @Getter
    private static final AttributeModifier stepHeight = new AttributeModifier(UUID.fromString("4dc7c48f-a69e-4849-b07b-1b0bbecf8e16"), ModConstants.withId("comfy_shoes_step"), 0.5, AttributeModifier.Operation.ADDITION);
    public ComfyShoesItem(Properties pProperties) {
        super(ModArmorMaterials.COMFY, Type.BOOTS, pProperties.stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if(pEntity instanceof LivingEntity living && pSlotId == 36) {
            AttributeInstance step = living.getAttribute(NeoForgeMod.STEP_HEIGHT.value());
            if(step != null && !step.hasModifier(stepHeight) && !living.isCrouching()){
                step.addTransientModifier(stepHeight);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable(ModConstants.TRANSLATE_KEY_PREFIX.ofItem(ModItems.COMFY_SHOES)).withStyle(ChatFormatting.GRAY));
    }
}
