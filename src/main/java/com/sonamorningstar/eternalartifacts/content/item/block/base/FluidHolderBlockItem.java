package com.sonamorningstar.eternalartifacts.content.item.block.base;

import com.sonamorningstar.eternalartifacts.client.renderer.ModItemStackBEWLR;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class FluidHolderBlockItem extends BlockItem implements ICapabilityListener {
    public FluidHolderBlockItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Component fluidName = getFluidName(stack);
        IFluidHandlerItem fhi = stack.getCapability(Capabilities.FluidHandler.ITEM);
        FluidStack fs = getFluidStack(stack);
        if(!Objects.equals(fluidName, Component.empty()) && fhi != null)
            tooltip.add(fluidName.copy().append(" ").append(String.valueOf(fs.getAmount())).append(" / ").append(String.valueOf(fhi.getTankCapacity(0))));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public Component getName(ItemStack stack) {
        Component fluidName = getFluidName(stack);
        if(!Objects.equals(fluidName, Component.empty())) return Component.translatable(getDescriptionId()+".filled", fluidName);
        else return super.getName(stack);
    }

    protected Fluid getFluid(ItemStack stack) {
        return getFluidStack(stack).getFluid();
    }

    protected FluidStack getFluidStack(ItemStack stack) {
        IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem != null) return fluidHandlerItem.getFluidInTank(0);
        return FluidStack.EMPTY;
    }

    protected Component getFluidName(ItemStack stack) {
        Fluid fluid = getFluid(stack);
        if(fluid.isSame(Fluids.EMPTY)) return Component.empty();
        else {
            String descriptionId = fluid.getFluidType().getDescriptionId();
            return Component.translatable(descriptionId).withColor(BlockHelper.getFluidTintColor(fluid));
        }
    }

    protected boolean isEmpty(ItemStack stack) {
        return getFluidStack(stack).isEmpty();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem != null) {
            return (int) ((fluidHandlerItem.getFluidInTank(0).getAmount() / (float) fluidHandlerItem.getTankCapacity(0)) * 13);
        }
        return 0;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getFluidStack(stack).getAmount() > 0 && !(stack.getCount() > 1);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BlockHelper.getFluidTintColor(getFluid(stack));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ModItemStackBEWLR.INSTANCE.get();
            }
        });
    }

    public abstract static class ModCauldronInteraction implements CauldronInteraction {
        public static CauldronInteraction.InteractionMap PLASTIC = CauldronInteraction.newInteractionMap("plastic");

        public static final ModCauldronInteraction EMPTY = new ModCauldronInteraction() {
            @Nullable
            private BlockState getState(FluidStack stack) {
                if(stack.is(Fluids.WATER)) {
                    return Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3);
                } else if(stack.is(Fluids.LAVA)) {
                    return Blocks.LAVA_CAULDRON.defaultBlockState();
                } else if(stack.is(ModFluids.LIQUID_PLASTIC.get().getSource())) {
                    return ModBlocks.PLASTIC_CAULDRON.get().defaultBlockState();
                }
                return null;
            }

            //Fills the cauldron empties the bucket.
            @Override
            public InteractionResult interact(BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, ItemStack stack) {
                IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
                if (fluidHandlerItem != null) {
                    FluidStack fluidStack = fluidHandlerItem.getFluidInTank(0);
                    BlockState newState = getState(fluidStack);
                    if(newState != null) {
                        if(fluidHandlerItem.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE).getAmount() >= FluidType.BUCKET_VOLUME) {
                            if(!level.isClientSide()) {
                                if(!player.getAbilities().instabuild) {
                                    fluidHandlerItem.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                                }
                                fillTheCauldron(player, level, stack, fluidStack, pos, newState);
                            }
                            return InteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                }
                return InteractionResult.PASS;
            }
        };

        protected void fillTheCauldron(Player player, Level level, ItemStack stack, FluidStack fluidStack, BlockPos pos, BlockState newState) {
            player.awardStat(Stats.FILL_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            level.setBlockAndUpdate(pos, newState);
            SoundEvent emptySound = fluidStack.getFluidType().getSound(player, level, pos, SoundActions.BUCKET_EMPTY);
            if (emptySound != null) level.playSound(null, pos, emptySound, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }
    }

    public static class ModCauldronDrainInteraction extends ModCauldronInteraction {
        private final Fluid type;
        private ModCauldronDrainInteraction(Fluid type) {
            this.type = type;
        }

        public static final ModCauldronDrainInteraction WATER = createLayered(Fluids.WATER);
        public static final ModCauldronDrainInteraction PLASTIC = createBasic(ModFluids.LIQUID_PLASTIC.get().getSource());
        public static final ModCauldronDrainInteraction LAVA = createBasic(Fluids.LAVA);

        //Fills the bucket. Empties the cauldron.
        @Override
        public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, ItemStack stack) {
            IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
            FluidStack fluidStack = new FluidStack(type, FluidType.BUCKET_VOLUME);
            if(fluidHandlerItem != null) {
                int remainder = fluidHandlerItem.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
                if(remainder > 0) {
                    if(!level.isClientSide()) {
                        if(!player.getAbilities().instabuild) {
                            fluidHandlerItem.fill(new FluidStack(type, remainder), IFluidHandler.FluidAction.EXECUTE);
                            //FluidUtil.tryFluidTransfer(fluidHandlerItem, );
                        }
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
        }

        private static ModCauldronDrainInteraction createBasic(Fluid fluid) {
            return new ModCauldronDrainInteraction(fluid);
        }

        private static ModCauldronDrainInteraction createLayered(Fluid fluid) {
            return new ModCauldronDrainInteraction(fluid) {
                @Override
                public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
                    if (state.getValue(LayeredCauldronBlock.LEVEL) == 3) return super.interact(state, level, pos, player, hand, stack);
                    return InteractionResult.PASS;
                }
            };
        }

    }
}
