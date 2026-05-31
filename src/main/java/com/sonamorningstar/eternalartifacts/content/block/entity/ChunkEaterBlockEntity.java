package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.filter.BlockFilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.BlockStateEntry;
import com.sonamorningstar.eternalartifacts.container.MultiblockEnergyHatchMenu;
import com.sonamorningstar.eternalartifacts.container.MultiblockFluidHatchMenu;
import com.sonamorningstar.eternalartifacts.container.MultiblockItemHatchMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Filterable;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModMultiblocks;
import com.sonamorningstar.eternalartifacts.util.ExperienceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ChunkEaterBlockEntity extends AbstractMultiblockBlockEntity implements Filterable {
    public long miningPos;
    public boolean isDone = false;
    public BlockPos southWestCorner;
    public BlockPos northEastCorner;
    public int destroyTickStart = -1;
    
    private final NonNullList<BlockFilterEntry> blockFilters = NonNullList.withSize(9, BlockStateEntry.EMPTY);
    private boolean blockFilterWhitelist = true;
    private boolean blockFilterIgnoreProperties = true;

    @Override public NonNullList<BlockFilterEntry> getBlockFilters() { return blockFilters; }
    @Override public boolean isBlockFilterWhitelist() { return blockFilterWhitelist; }
    @Override public void setBlockFilterWhitelistSilent(boolean w) { blockFilterWhitelist = w; }
    @Override public boolean isBlockFilterIgnoreProperties() { return blockFilterIgnoreProperties; }
    @Override public void setBlockFilterIgnorePropertiesSilent(boolean w) { blockFilterIgnoreProperties = w; }
    
    public ChunkEaterBlockEntity(BlockPos pos, BlockState state) {
        super(ModMultiblocks.CHUNK_EATER.getBlockEntity(), pos, state, ModMultiblocks.CHUNK_EATER.getMultiblock());
        setEnergy(() -> createBasicEnergy(20000, 1000, true, false));
        setTank(() -> createBasicTank(16000, true, false));
        outputSlots.addAll(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        setInventory(() -> createBasicInventory(10, outputSlots, (slot, stack) -> true));
        setEnergyPerTick(100);
    }
    
    @Override
    public boolean hasBlockFilters() {
        return true;
    }
    
    @Override
    public int getHatchSlotStartX() {
        if (getPartIndex() == 25) return 8 + 3 * 18;
        if (getPartIndex() == 7) return 8 + 4 * 18;
        return 8;
    }
    
    @Override
    public int getHatchSlotStartY() {
        if (getPartIndex() == 7) return 36;
        return 18;
    }
    
    @Nullable
    @Override
    public int[] getHatchSlots() {
        if (getPartIndex() == 25) {
            return outputSlots.stream().mapToInt(i -> i).toArray();
        }
        return new int[]{0};
    }
    
    @Nullable
    @Override
    public String[] getHatchSlotPattern() {
        if (getPartIndex() == 25) {
            return new String[] {
                "123",
                "456",
                "789"
            };
        }
        return new String[] { "0" };
    }
    
    @Nullable
    @Override
    public Map<Character, Integer> getHatchSlotConfig() {
        if (getPartIndex() == 25) {
            return Map.of(
                    '1', 1, '2', 2, '3', 3,
                    '4', 4, '5', 5, '6', 6,
                    '7', 7, '8', 8, '9', 9
            );
        }
        return Map.of('0', 0);
    }
    
    @Override
    public int[] getAvaiableInventorySlots() {
        return getPartIndex() == 25 ? outputSlots.stream().mapToInt(i -> i).toArray() : new int[]{0};
    }
    
    //7 is master
    //25 is item output
    //29 is fluid output
    //47 is energy
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ChunkEaterBlockEntity chunkEater) {
            int partIndex = chunkEater.getPartIndex();
            if (partIndex == 25 || partIndex == 7) {
                setMenuConstructor((id, inv, be, data) -> new MultiblockItemHatchMenu(id, inv, chunkEater, data));
            }
            if (partIndex == 29) {
                setMenuConstructor((id, inv, be, data) -> new MultiblockFluidHatchMenu(id, inv, chunkEater, data));
            }
            if (partIndex == 47) {
                setMenuConstructor((id, inv, be, data) -> new MultiblockEnergyHatchMenu(id, inv, chunkEater, data));
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        if (isMaster() && level != null && !level.isClientSide()) {
            ChunkPos currentChunk = new ChunkPos(getBlockPos());
            southWestCorner = new BlockPos(
                Math.min((currentChunk.x - 1) * 16, (currentChunk.x + 1) * 16 + 15),
                level.getMinBuildHeight(),
                Math.min((currentChunk.z - 1) * 16, (currentChunk.z + 1) * 16 + 15)
            );
            northEastCorner = new BlockPos(
                Math.max((currentChunk.x - 1) * 16, (currentChunk.x + 1) * 16 + 15),
                level.getMaxBuildHeight() - 1,
                Math.max((currentChunk.z - 1) * 16, (currentChunk.z + 1) * 16 + 15)
            );
            if (miningPos == 0) {
                miningPos = northEastCorner.asLong();
            }
        }
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (isMaster()) {
            tag.putLong("MiningPos", miningPos);
            tag.putBoolean("Done", isDone);
        }
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (isMaster()) {
            miningPos = tag.getLong("MiningPos");
            isDone = tag.getBoolean("Done");
        }
    }
    
    @Override
    public void tickMaster(ServerLevel lvl, BlockPos pos, BlockState st) {
        getFakePlayer();
        if (fakePlayer == null) return;
        
        BlockPos current = BlockPos.of(miningPos);
        
        // Check bounds in case world height changed or miningPos is invalid
        if (current.getY() > northEastCorner.getY() || current.getY() < southWestCorner.getY() ||
            current.getX() > northEastCorner.getX() || current.getX() < southWestCorner.getX() ||
            current.getZ() > northEastCorner.getZ() || current.getZ() < southWestCorner.getZ()) {
            current = northEastCorner;
        }

        if (!redstoneChecks(lvl) || !canWork(energy)) return;
        int skipped = 0;
        // Skip up to 2304 air blocks or unbreakable blocks per tick to fast-forward
        while (skipped < 2304) {
            BlockState targetState = lvl.getBlockState(current);
            if(!canProcessMining(lvl, targetState, current)) {
                current = getNextPos(current);
                if (current == null) {
                    isDone = true;
                    markDirty();
                    return;
                }
                skipped++;
            } else {
                break;
            }
        }
        miningPos = current.asLong();

        BlockState targetState = lvl.getBlockState(current);
        if (canProcessMining(lvl, targetState, current)) {
            spendEnergy(energy);
            fakePlayer.getInventory().selected = 0; // Use pickaxe in first slot
            
            if (destroyTickStart == -1) {
                fakePlayer.gameMode.delayedTickStart = fakePlayer.gameMode.gameTicks;
                destroyTickStart = fakePlayer.gameMode.gameTicks;
            }
            
            fakePlayer.gameMode.delayedDestroyPos = current;
            fakePlayer.gameMode.hasDelayedDestroy = true;
            fakePlayer.gameMode.tick();
            
            // If block is destroyed (now air), reset destroyTickStart
            if (lvl.getBlockState(current).isAir()) {
                destroyTickStart = -1;
                lvl.destroyBlockProgress(fakePlayer.getId(), current, -1);
                
                ItemStack tool = inventory.getStackInSlot(0);
                int xp = targetState.getExpDrop(lvl, lvl.getRandom(), current,
                    tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE),
                    tool.getEnchantmentLevel(Enchantments.SILK_TOUCH));
                if (xp > 0) {
                    int remainingXp = xp;
                    ItemStack toolCopy = inventory.getStackInSlot(0).copy();
                    remainingXp = ExperienceHelper.mendItem(toolCopy, remainingXp);
                    inventory.setStackInSlot(0, toolCopy);
                    if (tank != null) {
                        int filled = tank.fillForced(ModFluids.NOUS.getFluidStack(remainingXp * 20), IFluidHandler.FluidAction.EXECUTE);
                        remainingXp -= filled / 20;
                    }
                    if (remainingXp > 0) {
                        targetState.getBlock().popExperience(lvl, current, remainingXp);
                    }
                }
            }
        }
    }
    
    public void resetMining() {
        miningPos = northEastCorner.asLong();
        isDone = false;
        markDirty();
    }
    
    private boolean canProcessMining(Level lvl, BlockState targetState, BlockPos current) {
        return !targetState.isAir() && targetState.getDestroySpeed(lvl, current) >= 0 &&
            !targetState.is(getBlockState().getBlock()) && matchesBlockFilter(targetState) &&
            targetState.getBlock().canHarvestBlock(targetState, lvl, current, fakePlayer);
    }
    
    private BlockPos getNextPos(BlockPos current) {
        if (isDone || current == null) return null;
        int x = current.getX();
        int y = current.getY();
        int z = current.getZ();
        x--;
        if (x < southWestCorner.getX()) {
            x = northEastCorner.getX();
            z--;
            if (z < southWestCorner.getZ()) {
                z = northEastCorner.getZ();
                y--;
                if (y < southWestCorner.getY()) {
                    return null;
                }
            }
        }
        return new BlockPos(x, y, z);
    }
}
