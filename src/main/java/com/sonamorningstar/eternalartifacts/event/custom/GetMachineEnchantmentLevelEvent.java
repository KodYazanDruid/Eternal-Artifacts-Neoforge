package com.sonamorningstar.eternalartifacts.event.custom;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
public class GetMachineEnchantmentLevelEvent extends GetEnchantmentLevelEvent {
	private final ModBlockEntity blockEntity;
	public GetMachineEnchantmentLevelEvent(ModBlockEntity blockEntity, Map<Enchantment, Integer> enchantments, @Nullable Enchantment targetEnchant) {
		super(ItemStack.EMPTY, enchantments, targetEnchant);
		this.blockEntity = blockEntity;
	}
}
