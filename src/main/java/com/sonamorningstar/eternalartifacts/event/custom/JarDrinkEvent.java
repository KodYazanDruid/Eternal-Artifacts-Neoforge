package com.sonamorningstar.eternalartifacts.event.custom;

import com.sonamorningstar.eternalartifacts.content.item.block.JarBlockItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.BiConsumer;

/**
 * JarDrinkEvent is called when player attempts to drink a fluid from the JarBlockItem. </r>
 * It is called from {@link JarBlockItem#use(Level, Player, InteractionHand)} (). <br>
 * If the event is cancelled, event will not run and no drinking action will play. </br>
 * If the event is not cancelled and the useTime is greater than 0 it will run the drinking action. </br>
 * If the event is not cancelled but useTime and drinkingAmount are 0, drinking action not going to play because they must be greater than 0 to run. </br>
 * BiConsumer afterDrink, is used for custom actions after drinking the fluid. </br>
 */
@Getter
@RequiredArgsConstructor
public class JarDrinkEvent extends Event implements ICancellableEvent {
    private final FluidStack fluidStack;
    private final Player player;
    @Setter
    private int useTime = 0;
    @Setter
    private SoundEvent drinkingSound = SoundEvents.GENERIC_DRINK;
    @Setter
    private SoundEvent eatingSound = SoundEvents.GENERIC_DRINK;
    @Setter
    private BiConsumer<Player, ItemStack> afterDrink = (player, stack) -> {};
    @Setter
    private SoundEvent afterDrinkSound = null;
    @Setter
    private int drinkingAmount = 1000;

    public void setDefaultUseTime() {
        this.useTime = 40;
    }
}
