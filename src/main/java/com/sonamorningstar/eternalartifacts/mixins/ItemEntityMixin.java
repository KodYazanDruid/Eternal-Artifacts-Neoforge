package com.sonamorningstar.eternalartifacts.mixins;

import com.sonamorningstar.eternalartifacts.content.recipe.custom.SteelCraftingInWorld;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<? extends ItemEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Inject(at = @At("RETURN"), method = "tick")
    private void handleTransform(CallbackInfo ci) {
        if(this.isRemoved()) return;
        ItemEntity self = (ItemEntity) (Object) this;
        if(!SteelCraftingInWorld.isValidItem(self.getItem().getItem())) return;
        if(SteelCraftingInWorld.isInCorrectEnvironment(blockPosition(), level())) {
            if(!level().isClientSide) SteelCraftingInWorld.tryTransform(self);
        }
    }
}
