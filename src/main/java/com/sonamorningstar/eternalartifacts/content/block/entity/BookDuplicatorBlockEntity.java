package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capablities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capablities.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.BookDuplicatorMenu;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BookDuplicatorBlockEntity extends MachineBlockEntity implements MenuProvider, ITickable {
    public BookDuplicatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BOOK_DUPLICATOR.get(), pPos, pBlockState);
    }

    public ModItemStorage inventory = new ModItemStorage(3) {
        @Override
        protected void onContentsChanged(int slot) {
            BookDuplicatorBlockEntity.this.sendUpdate();
        }
    };
    public ModEnergyStorage energy = new ModEnergyStorage(50000, 2500) {
        @Override
        public void onEnergyChanged() {
            BookDuplicatorBlockEntity.this.sendUpdate();
        }
    };

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.put("Energy", energy.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        energy.deserializeNBT(tag.get("Energy"));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(ModBlocks.BOOK_DUPLICATOR.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BookDuplicatorMenu(pContainerId, pPlayerInventory, this, data);
    }

    public void tick(Level lvl, BlockPos pos, BlockState st) {

    }


}
