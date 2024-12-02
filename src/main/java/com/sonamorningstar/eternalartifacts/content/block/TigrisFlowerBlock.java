package com.sonamorningstar.eternalartifacts.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Objects;

public class TigrisFlowerBlock extends FlowerBlock {
    public TigrisFlowerBlock() {
        super(()-> MobEffects.WATER_BREATHING, 20, BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_CYAN)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .pushReaction(PushReaction.DESTROY));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        level.invalidateCapabilities(pos);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getCapability(Capabilities.FluidHandler.ITEM) == null) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
            var playerInventory = player.getCapability(Capabilities.ItemHandler.ENTITY);
            Objects.requireNonNull(playerInventory, "Player item handler is null");
            if (fluidHandler != null) {
                FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection());
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }


}
