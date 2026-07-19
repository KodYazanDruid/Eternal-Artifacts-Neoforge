package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.content.block.base.InheritorRetexturedBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.GardeningPotEntity;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.PlantType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;

@SuppressWarnings("deprecation")
public class GardeningPotBlock extends InheritorRetexturedBlock implements SimpleWaterloggedBlock{
    private static final VoxelShape TOP = BlockHelper.generateByArea(16, 2, 16, 0, 14, 0);
    private static final VoxelShape BOTTOM = BlockHelper.generateByArea(14, 14, 14, 1, 0, 1);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    //public static final IntegerProperty LIGHT_LEVEL = BlockStateProperties.LEVEL;
    //public static final ToIntFunction<BlockState> LIGHT_EMISSION = state -> state.getValue(LIGHT_LEVEL);

    public GardeningPotBlock() {
        super(Properties.of()
                .destroyTime(1.5F)
                .mapColor(MapColor.TERRACOTTA_ORANGE)
                .noOcclusion().randomTicks());
                //.lightLevel(LIGHT_EMISSION));
        registerDefaultState(defaultBlockState()
                .setValue(WATERLOGGED, false));
                //.setValue(LIGHT_LEVEL, 0));
    }

    @Override
    protected MapCodec<? extends Block> codec() {return simpleCodec(p -> new GardeningPotBlock());}
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {return Shapes.join(TOP, BOTTOM, BooleanOp.OR);}
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new GardeningPotEntity(pPos, pState);
    }
    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {return false;}

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);
        IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, hit.getDirection());
        if (itemStack.getCapability(Capabilities.FluidHandler.ITEM) == null || fluidHandler == null) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            var playerInventory = player.getCapability(Capabilities.ItemHandler.ENTITY);
            Objects.requireNonNull(playerInventory, "Player item handler is null");
            FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection());
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos above = pos.above();
        var bonemealed = BoneMealItem.applyBonemeal(Items.BONE_MEAL.getDefaultInstance(), level, above, FakePlayerHelper.getFakePlayer(level));
        if (bonemealed) {
            level.levelEvent(1505, above, 0);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        } else {
            return (lvl, pos, st, be) -> {
                if (be instanceof GardeningPotEntity entity) {
                    entity.tick(lvl, pos, st);
                }
            };
        }
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction direction, IPlantable plantable) {
        PlantType plantType = plantable.getPlantType(level, pos);
        return plantType == PlantType.CROP || plantType == PlantType.PLAINS || plantType == PlantType.CAVE ||
                plantType == PlantType.DESERT || plantType == PlantType.BEACH;
    }

    @Override
    public boolean onTreeGrow(BlockState state, LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config) {
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        FluidState fluidstate = level.getFluidState(pos);
        int light = 0;
        ItemStack stack = ctx.getItemInHand();
        Block texture = RetexturedHelper.getBlock(RetexturedHelper.getTextureName(stack.getTag()));
        //Block texture = getTexture(level, pos);
        if(texture != Blocks.AIR) light = texture.getLightEmission(texture.defaultBlockState(), ctx.getLevel(), ctx.getClickedPos());
        return defaultBlockState()
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
                //.setValue(LIGHT_LEVEL, light);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED/*, LIGHT_LEVEL*/);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
