package com.sonamorningstar.eternalartifacts.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(LootTableReference.class)
public class LootTableReferenceMixin {
	
	@Shadow
	public ResourceLocation name;
	
	@WrapOperation(method = "createItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V"))
	public void createItemStack(LootTable instance, LootContext consumer, Consumer<ItemStack> itemStackConsumer, Operation<Void> original) {
		instance.getRandomItems(consumer, itemStackConsumer);
	}
}
