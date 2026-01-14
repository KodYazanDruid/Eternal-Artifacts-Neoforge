package com.sonamorningstar.eternalartifacts.api.cauldron;

import com.sonamorningstar.eternalartifacts.content.block.base.ModLayeredCauldronBlack;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class ModCauldronDrainInteraction extends ModCauldronInteraction {
    private final Fluid type;
    private ModCauldronDrainInteraction(Fluid type) {
        this.type = type;
    }

    public static final ModCauldronDrainInteraction WATER = createLayered(Fluids.WATER);
    public static final ModCauldronDrainInteraction PLASTIC = createBasic(ModFluids.LIQUID_PLASTIC.getFluid());
    public static final ModCauldronDrainInteraction LAVA = createBasic(Fluids.LAVA);
    public static final ModCauldronDrainInteraction CRUDE_OIL = createBasic(ModFluids.CRUDE_OIL.getFluid());
    public static final ModCauldronDrainInteraction NAPHTHA = createLayered(ModFluids.NAPHTHA.getFluid(), 4);

    //Fills the bucket. Empties the cauldron.
    @Override
    public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player,
                                      InteractionHand hand, ItemStack stack) {
        IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if(fluidHandlerItem != null) {
            FluidStack fluidStack = new FluidStack(type, FluidType.BUCKET_VOLUME);
            if (fluidHandlerItem.getTankCapacity(0) - fluidHandlerItem.getFluidInTank(0).getAmount() >= FluidType.BUCKET_VOLUME) {
                if(!level.isClientSide()) {
                    if(!player.getAbilities().instabuild) transferFluid(level, player, hand, pos);
                    emptyTheCauldron(player, level, stack, fluidStack, pos, Blocks.CAULDRON.defaultBlockState());
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    protected void emptyTheCauldron(Player player, Level level, ItemStack stack, FluidStack fluidStack, BlockPos pos, BlockState newState) {
        player.awardStat(Stats.USE_CAULDRON);
        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        level.setBlockAndUpdate(pos, newState);
        SoundEvent fillSound = fluidStack.getFluidType().getSound(player, level, pos, SoundActions.BUCKET_FILL);
        if (fillSound != null) level.playSound(null, pos, fillSound, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        level.invalidateCapabilities(pos);
    }

    private static ModCauldronDrainInteraction createBasic(Fluid fluid) {
        return new ModCauldronDrainInteraction(fluid);
    }
    
    private static ModCauldronDrainInteraction createLayered(Fluid fluid) {
        return createLayered(fluid, LayeredCauldronBlock.MAX_FILL_LEVEL);
    }

    private static ModCauldronDrainInteraction createLayered(Fluid fluid, int maxLevel) {
        return new ModCauldronDrainInteraction(fluid) {
            @Override
            public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
                if (state.getBlock() instanceof ModLayeredCauldronBlack cauldron) {
                    if (state.getValue(cauldron.getLevelProperty()) == cauldron.getMaxLevel()) {
                        return super.interact(state, level, pos, player, hand, stack);
                    }
                    return InteractionResult.PASS;
                } else if (state.getValue(LayeredCauldronBlock.LEVEL) == maxLevel) return super.interact(state, level, pos, player, hand, stack);
                return InteractionResult.PASS;
            }
        };
    }
}
