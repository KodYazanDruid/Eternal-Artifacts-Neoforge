package com.sonamorningstar.eternalartifacts.mixins;

import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.client.gui.TabHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {

    @Shadow protected int leftPos;
    @Shadow protected int topPos;

    protected AbstractContainerScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double mx, double my, int button, CallbackInfoReturnable<Boolean> cir) {
        if (Config.CHARMS_ENABLED.getAsBoolean()){
            TabHandler instance = TabHandler.INSTANCE;
            if (instance != null) {
                boolean isChanged = instance.listenClicks(leftPos, topPos, mx, my, this);
                if (!isChanged) return;
                cir.setReturnValue(true);
            }
        }
    }
}
