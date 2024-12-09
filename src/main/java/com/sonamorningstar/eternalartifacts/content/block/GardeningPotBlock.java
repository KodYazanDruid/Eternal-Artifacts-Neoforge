package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.content.block.base.InheritorRetexturedBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.GardeningPotEntity;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;

import static net.minecraft.references.Blocks.ATTACHED_MELON_STEM;
import static net.minecraft.references.Blocks.ATTACHED_PUMPKIN_STEM;

@SuppressWarnings("deprecation")
public class GardeningPotBlock extends InheritorRetexturedBlock implements SimpleWaterloggedBlock{
    private static final VoxelShape TOP = BlockHelper.generateByArea(16, 2, 16, 0, 14, 0);
    private static final VoxelShape BOTTOM = BlockHelper.generateByArea(14, 14, 14, 1, 0, 1);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty LIGHT_LEVEL = BlockStateProperties.LEVEL;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = state -> state.getValue(LIGHT_LEVEL);

    public GardeningPotBlock() {
        super(Properties.of()
                .destroyTime(1.5F)
                .mapColor(MapColor.TERRACOTTA_ORANGE)
                .noOcclusion().randomTicks()
                .lightLevel(LIGHT_EMISSION));
        registerDefaultState(defaultBlockState()
                .setValue(WATERLOGGED, false)
                .setValue(LIGHT_LEVEL, 0));
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
        BlockState upper = level.getBlockState(pos.above());
        if(upper.getBlock() instanceof IPlantable) {
            for(int i = 0; i < 7; i ++) {
                upper.randomTick(level, pos.above(), random);
            }
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {return true;}

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
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        if(pos.above().equals(neighborPos) && levelAccessor instanceof ServerLevel level) {
            level.invalidateCapabilities(pos);
            GardeningPotEntity potEntity = (GardeningPotEntity) level.getBlockEntity(pos);
            IItemHandler inventoryBelow = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.below(), Direction.UP);
            if(potEntity != null){
                Block neighborBlock = neighborState.getBlock();
                //Getting drops
                List<ItemStack> drops = BlockHelper.getBlockDrops(level, neighborState, neighborPos, ItemStack.EMPTY, potEntity, null);

                if (neighborBlock instanceof CropBlock crop) {
                    if (crop.getAge(neighborState) == crop.getMaxAge()) {
                        level.destroyBlock(neighborPos, false);
                        //Replanting and stuff.
                        if (!drops.isEmpty()) {
                            //Setting seed
                            ItemStack seed = ItemStack.EMPTY;
                            boolean seedSet = false;
                            for (int i = 0; i < drops.size(); i++) {
                                ItemStack stack = drops.get(i);
                                if (stack.is(crop.getCloneItemStack(level, neighborPos, neighborState).getItem()) && !seedSet) {
                                    seed = stack.copyWithCount(1);
                                    seedSet = true;
                                    drops.set(i, stack.copyWithCount(stack.getCount() - 1));
                                }
                            }
                            //Replanting
                            if (seed.getItem() instanceof BlockItem seedBlock) {
                                level.setBlockAndUpdate(neighborPos, seedBlock.getBlock().defaultBlockState());
                            }
                            //Harvesting
                            pushOrPop(drops, level, neighborPos, inventoryBelow);
                            level.sendBlockUpdated(pos, state, state, 2);
                        }

                    }
                }//Not all BushBlocks are producing fruits and things, so I am doing it manually.
                else if(neighborBlock instanceof SweetBerryBushBlock bushBlock) {
                    if(neighborState.getValue(SweetBerryBushBlock.AGE) == SweetBerryBushBlock.MAX_AGE) {
                        level.setBlockAndUpdate(neighborPos, bushBlock.defaultBlockState().setValue(BlockStateProperties.AGE_3, 1));
                        pushOrPop(drops, level, neighborPos, inventoryBelow);
                        level.sendBlockUpdated(pos, state, state, 2);
                        level.playSound(null, neighborPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
                    }
                }else if(neighborState.is(ATTACHED_PUMPKIN_STEM) || neighborState.is(ATTACHED_MELON_STEM)) {
                    Direction facing = neighborState.getValue(HorizontalDirectionalBlock.FACING);
                    BlockPos melPos = neighborPos.relative(facing);
                    BlockState harvest = level.getBlockState(melPos);
                    //drops = harvest.getDrops(lootParams);
                    drops = BlockHelper.getBlockDrops(level, harvest, melPos, ItemStack.EMPTY, potEntity, null);
                    pushOrPop(drops, level, melPos, inventoryBelow);
                    level.destroyBlock(melPos, false);
                    level.sendBlockUpdated(pos, state, state, 2);
                }
            }
        }
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        return super.updateShape(state, direction, neighborState, levelAccessor, pos, neighborPos);
    }

    private void pushOrPop(List<ItemStack> resources, Level level, BlockPos pos, IItemHandler inventory) {
        for(ItemStack stack : resources) {
            ItemStack remainder = stack;

            if (!remainder.isEmpty()) {
                remainder = ItemHandlerHelper.insertItemStacked(inventory, remainder, false);
            }
            if(!remainder.isEmpty() && !level.isClientSide()) {
                Block.popResource(level, pos.above(), remainder);
            }
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
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
                .setValue(LIGHT_LEVEL, light);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED, LIGHT_LEVEL);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
