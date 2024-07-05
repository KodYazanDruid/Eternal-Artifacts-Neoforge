package com.sonamorningstar.eternalartifacts.event.custom;

import com.sonamorningstar.eternalartifacts.content.item.block.JarBlockItem;
import lombok.Getter;
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
 * JarDrinkEvent is called when player attemps to drink a fluid from the JarBlockItem. </r>
 * It is called from {@link JarBlockItem#use(Level, Player, InteractionHand)} ()}. <br>
 * If the event is cancelled, event will not run and no drinking action will play. </br>
 * If the event is not cancelled and the useTime is greater than 0 it will run the drinking action. </br>
 * If the event is not cancelled but useTime is 0, drinking action not going to play because it must be greater than 0 to run. </br>
 */
public class JarDrinkEvent extends Event implements ICancellableEvent {
    @Getter
    private final FluidStack fluidStack;
    @Getter
    private final Player player;
    @Setter
    @Getter
    private int useTime = 0;
    @Setter
    @Getter
    private SoundEvent drinkingSound = SoundEvents.GENERIC_DRINK;
    @Setter
    @Getter
    private SoundEvent eatingSound = SoundEvents.GENERIC_DRINK;
    @Setter
    @Getter
    private BiConsumer<Player, ItemStack> afterDrink = (player, stack) -> {};
    @Getter
    @Setter
    private int drinkingAmount = 1000;

    public JarDrinkEvent(FluidStack fluidStack, Player player) {
        this.fluidStack = fluidStack;
        this.player = player;
    }

}
