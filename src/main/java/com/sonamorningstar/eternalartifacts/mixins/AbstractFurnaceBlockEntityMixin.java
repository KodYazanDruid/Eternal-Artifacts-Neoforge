package com.sonamorningstar.eternalartifacts.mixins;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
	
	@Inject(method = "burn", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/core/NonNullList;get(I)Ljava/lang/Object;", ordinal = 0))
	private void burn(RegistryAccess pRecipe, RecipeHolder<?> pInventory, NonNullList<ItemStack> pMaxStackSize, int p_267157_, CallbackInfoReturnable<Boolean> cir) {
		ItemStack itemstack = pMaxStackSize.get(0);
		
		if (itemstack.is(ModBlocks.WET_INDUSTRIAL_SPONGE.asItem()) &&
			!pMaxStackSize.get(1).isEmpty() && pMaxStackSize.get(1).is(Items.BUCKET)) {
			pMaxStackSize.set(1, new ItemStack(Items.WATER_BUCKET));
		}
	}
}
