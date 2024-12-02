package com.sonamorningstar.eternalartifacts.mixins;

import com.mojang.authlib.GameProfile;
import com.sonamorningstar.eternalartifacts.capabilities.item.CharmStorage;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.mixins_interfaces.ICharmProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements ICharmProvider {

    @Unique
    private CharmStorage eternal_Artifacts_Neoforge$charms;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/InventoryMenu;<init>(Lnet/minecraft/world/entity/player/Inventory;ZLnet/minecraft/world/entity/player/Player;)V"))
    private void inventoryMenu(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        this.eternal_Artifacts_Neoforge$charms = self.getData(ModDataAttachments.CHARMS);

        /*if (self instanceof ServerPlayer sp && !(sp instanceof FakePlayer)) {
            getCharms().addListener(slot ->
                Channel.sendToPlayer(new UpdateCharmsToClient(getCharms().serializeNBT()), sp)
            );
        }*/

    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;tick()V"))
    private void aiStep(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        CharmStorage charms = player.getData(ModDataAttachments.CHARMS);
        /*for (int i = 0; i < charms.getSlots(); i++) {
            ItemStack stack = charms.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            CharmTickEvent event = new CharmTickEvent(player, stack, i);
            NeoForge.EVENT_BUS.post(event);
        }*/
    }

    @Override
    public CharmStorage getCharms() {
         return eternal_Artifacts_Neoforge$charms;
    }
}
