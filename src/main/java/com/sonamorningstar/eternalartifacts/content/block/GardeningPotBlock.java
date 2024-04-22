package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.content.block.entity.GardeningPotEntity;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.PlantType;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;

import static net.minecraft.references.Blocks.ATTACHED_MELON_STEM;
import static net.minecraft.references.Blocks.ATTACHED_PUMPKIN_STEM;

public class GardeningPotBlock extends RetexturedBlock implements SimpleWaterloggedBlock{
    private static final VoxelShape TOP = BlockHelper.generateByArea(16, 2, 16, 0, 14, 0);
    private static final VoxelShape BOTTOM = BlockHelper.generateByArea(14, 14, 14, 1, 0, 1);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty LIGHT_LEVEL = BlockStateProperties.LEVEL;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = state -> state.getValue(LIGHT_LEVEL);

    public GardeningPotBlock(Properties props) {
        super(props.lightLevel(LIGHT_EMISSION));
        registerDefaultState(defaultBlockState()
                .setValue(WATERLOGGED, false)
                .setValue(LIGHT_LEVEL, 0));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(GardeningPotBlock::new);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.join(TOP, BOTTOM, BooleanOp.OR);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new GardeningPotEntity(pPos, pState);
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
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
            GardeningPotEntity potEntity = (GardeningPotEntity) level.getBlockEntity(pos);
            IItemHandler inventoryBelow = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.below(), Direction.UP);
            if(potEntity != null){
                Block neighborBlock = neighborState.getBlock();
                //Getting drops
                LootParams.Builder lootParams = new LootParams.Builder((ServerLevel) level)
                        .withParameter(LootContextParams.BLOCK_ENTITY, potEntity)
                        .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                        .withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
                List<ItemStack> drops = neighborState.getDrops(lootParams);

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
                }//Not all BushBlocks are producing fruits and things so i am doing it manually.
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
                    drops = harvest.getDrops(lootParams);
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
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        int light = 0;
        ItemStack stack = pContext.getItemInHand();
        Block texture = RetexturedHelper.getBlock(RetexturedHelper.getTextureName(stack.getTag()));
        if(texture != Blocks.AIR) light = texture.getLightEmission(texture.defaultBlockState(), pContext.getLevel(), pContext.getClickedPos());
        return defaultBlockState()
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
                .setValue(LIGHT_LEVEL, light);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED, LIGHT_LEVEL);
    }

    @Override
    public FluidState getFluidState(BlockState p_221523_) {
        return p_221523_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_221523_);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        Block texture = ((GardeningPotEntity) blockEntity).getTexture();
        return texture.getSoundType(state, level, pos, entity);
    }
    
}
