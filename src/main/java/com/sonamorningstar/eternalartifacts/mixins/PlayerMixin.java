package com.sonamorningstar.eternalartifacts.mixins;

import com.mojang.authlib.GameProfile;
import com.sonamorningstar.eternalartifacts.capabilities.item.PlayerCharmsStorage;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.event.custom.charms.CharmTickEvent;
import com.sonamorningstar.eternalartifacts.mixins_interfaces.ICharmProvider;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.charm.UpdateCharmsToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements ICharmProvider {

    @Unique
    private PlayerCharmsStorage eternal_Artifacts_Neoforge$charms;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/InventoryMenu;<init>(Lnet/minecraft/world/entity/player/Inventory;ZLnet/minecraft/world/entity/player/Player;)V"))
    private void inventoryMenu(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        this.eternal_Artifacts_Neoforge$charms = self.getData(ModDataAttachments.PLAYER_CHARMS);

        /*if (self instanceof ServerPlayer sp && !(sp instanceof FakePlayer)) {
            getCharms().addListener(slot ->
                Channel.sendToPlayer(new UpdateCharmsToClient(getCharms().serializeNBT()), sp)
            );
        }*/

    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;tick()V"))
    private void aiStep(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        PlayerCharmsStorage charms = player.getData(ModDataAttachments.PLAYER_CHARMS);
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
    public PlayerCharmsStorage getCharms() {
         return eternal_Artifacts_Neoforge$charms;
    }
}
