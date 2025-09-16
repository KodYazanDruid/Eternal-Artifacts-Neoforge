package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.AbstractPipeBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.Cable;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.IExtensibleEnum;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@SuppressWarnings({"deprecation"})
public class CableBlock extends AbstractPipeBlock<IEnergyStorage> {
    private final CableTier tier;
    private static final VoxelShape SHAPE = BlockHelper.generateByArea(6, 6, 6, 5, 5, 5);
    private static final VoxelShape SHAPE_NORTH = BlockHelper.generateByArea(6, 6, 5, 5, 5, 0);
    private static final VoxelShape SHAPE_SOUTH = BlockHelper.generateByArea(6, 6, 5, 5, 5, 11);
    private static final VoxelShape SHAPE_EAST = BlockHelper.generateByArea(5, 6, 6, 11, 5, 5);
    private static final VoxelShape SHAPE_WEST = BlockHelper.generateByArea(5, 6, 6, 0, 5, 5);
    private static final VoxelShape SHAPE_UP = BlockHelper.generateByArea(6, 5, 6, 5, 11, 5);
    private static final VoxelShape SHAPE_DOWN = BlockHelper.generateByArea(6, 5, 6, 5, 0, 5);

    public CableBlock(CableTier tier, Properties props) {
        super(IEnergyStorage.class, props);
        this.tier = tier;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape joinedShape = SHAPE;
        boolean isNorth = state.getValue(NORTH);
        boolean isEast = state.getValue(EAST);
        boolean isSouth = state.getValue(SOUTH);
        boolean isWest = state.getValue(WEST);
        boolean isUp = state.getValue(UP);
        boolean isDown = state.getValue(DOWN);

        if (isNorth) joinedShape = Shapes.or(joinedShape, SHAPE_NORTH);
        if (isEast) joinedShape = Shapes.or(joinedShape, SHAPE_EAST);
        if (isSouth) joinedShape = Shapes.or(joinedShape, SHAPE_SOUTH);
        if (isWest) joinedShape = Shapes.or(joinedShape, SHAPE_WEST);
        if (isUp) joinedShape = Shapes.or(joinedShape, SHAPE_UP);
        if (isDown) joinedShape = Shapes.or(joinedShape, SHAPE_DOWN);

        return joinedShape;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new Cable(pos, state); }
    
    @Override
    public InteractionResult use(BlockState pState, Level level, BlockPos pPos, Player pPlayer, InteractionHand hand, BlockHitResult hit) {
        /*if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND){
            Direction relativeDir = getClickedRelativePos(hit.getDirection(), pPos, hit.getLocation(), 6);
        }*/
        return super.use(pState, level, pPos, pPlayer, hand, hit);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(stack, pLevel, pTooltip, pFlag);
        pTooltip.add(Component.translatable(ModConstants.BLOCK.withSuffix("pipe.range"), tier.maxRange).withColor(0xADD8E6));
        pTooltip.add(Component.translatable(ModConstants.BLOCK.withSuffix("pipe.energy.transfer_rate"), tier.transferRate).withColor(0xADD8E6));
        if (this instanceof UncoveredCableBlock) pTooltip.add(Component.translatable(ModConstants.BLOCK.withSuffix("pipe.energy.damage_cost"), tier.damageCost).withColor(0xADD8E6));
    }
    
    @Getter
    public enum CableTier implements IExtensibleEnum {
        TIN(16, 100, 10),
        COPPER(24, 1000, 50),
        GOLD(32, 4000, 100);
        
        private final int maxRange;
        private final int transferRate;
        private final int damageCost;
        
        CableTier(int maxRange, int transferRate, int damageCost) {
            this.maxRange = maxRange;
            this.transferRate = transferRate;
            this.damageCost = damageCost;
        }
        
        public static CableTier create(String name, int maxRange, int transferRate, int damageCost) {
            throw new IllegalStateException("Enum not extended");
        }
    }
}
