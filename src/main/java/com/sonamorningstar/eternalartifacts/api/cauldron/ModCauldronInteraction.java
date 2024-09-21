package com.sonamorningstar.eternalartifacts.api.cauldron;

import com.sonamorningstar.eternalartifacts.content.block.BluePlasticCauldronBlock;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

public abstract class ModCauldronInteraction implements CauldronInteraction {
    public static CauldronInteraction.InteractionMap PLASTIC = CauldronInteraction.newInteractionMap("plastic");
    public static CauldronInteraction.InteractionMap BLUE_PLASTIC = CauldronInteraction.newInteractionMap("blue_plastic");

    public static final ModCauldronInteraction EMPTY = new ModCauldronInteraction() {
        //Fills the cauldron empties the bucket.
        @Override
        public InteractionResult interact(BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, ItemStack stack) {
            IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (fluidHandlerItem != null) {
                FluidStack fluidStack = fluidHandlerItem.getFluidInTank(0);
                BlockState newState = getState(fluidStack);
                if(newState != null) {
                    if(fluidHandlerItem.getFluidInTank(0).getAmount() >= FluidType.BUCKET_VOLUME) {
                        if(!level.isClientSide()) {
                            if(!player.getAbilities().instabuild) transferFluid(level, player, hand, pos);
                            fillTheCauldron(player, level, stack, fluidStack, pos, newState);
                        }
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            }
            return InteractionResult.PASS;
        }
    };

    public static final ModCauldronInteraction DYE_PLASTIC = new ModCauldronInteraction() {
        @Override
        public InteractionResult interact(BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, ItemStack stack) {
            if (stack.getItem() instanceof DyeItem dye) {
                //If dye is blue.
                if(dye.getDyeColor().getId() == 11) {
                    player.awardStat(Stats.USE_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                    level.setBlockAndUpdate(pos, ModBlocks.BLUE_PLASTIC_CAULDRON.get().defaultBlockState().setValue(BluePlasticCauldronBlock.LEVEL, BluePlasticCauldronBlock.MAX_FILL_LEVEL));
                    level.playSound(null, pos, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                    stack.shrink(1);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            return InteractionResult.PASS;
        }
    };

    @Nullable
    private static BlockState getState(FluidStack stack) {
        if(stack.is(Fluids.WATER)) {
            return Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, LayeredCauldronBlock.MAX_FILL_LEVEL);
        } else if(stack.is(Fluids.LAVA)) {
            return Blocks.LAVA_CAULDRON.defaultBlockState();
        } else if(stack.is(ModFluids.LIQUID_PLASTIC.getFluid())) {
            return ModBlocks.PLASTIC_CAULDRON.get().defaultBlockState();
        }
        return null;
    }

    protected void fillTheCauldron(Player player, Level level, ItemStack stack, FluidStack fluidStack, BlockPos pos, BlockState newState) {
        player.awardStat(Stats.FILL_CAULDRON);
        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        level.setBlockAndUpdate(pos, newState);
        SoundEvent emptySound = fluidStack.getFluidType().getSound(player, level, pos, SoundActions.BUCKET_EMPTY);
        if (emptySound != null) level.playSound(null, pos, emptySound, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(null, GameEvent.FLUID_PLACE, pos);

    }

    protected void transferFluid(Level level, Player player, InteractionHand hand, BlockPos pos) {
        IFluidHandler cauldron = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        if(cauldron != null) FluidUtil.interactWithFluidHandler(player, hand, cauldron);
    }
}
