package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.BookDuplicatorMenu;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

public class BookDuplicatorBlockEntity extends MachineBlockEntity implements MenuProvider, ITickable {
    public BookDuplicatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BOOK_DUPLICATOR.get(), pPos, pBlockState);
        setMaxProgress(500);
    }

    // 0 -> input
    // 1 -> output
    // 2 -> book slot
    // 3 -> feather slot
    // 4 -> ink sac slot
    // 5 -> fluid filler
    public ModItemStorage inventory = new ModItemStorage(6) {
        @Override
        protected void onContentsChanged(int slot) {
            BookDuplicatorBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            switch (slot) {
                case 0 -> {return stack.is(Items.ENCHANTED_BOOK) || stack.is(Items.WRITTEN_BOOK);}
                case 1 -> {return false;}
                case 2 -> {return stack.is(Items.BOOK) || stack.is(Items.WRITABLE_BOOK);}
                case 3 -> {return stack.is(Tags.Items.FEATHERS);}
                case 4 -> {return stack.is(Items.INK_SAC);}
                case 5 -> {
                    IFluidHandlerItem fh = FluidUtil.getFluidHandler(stack).orElse(null);
                    if(fh == null) return false;
                    else {
                        FluidStack fluidStack = fh.getFluidInTank(0);
                        return !fluidStack.isEmpty() && fh.isFluidValid(0, fluidStack);
                    }
                }
                default -> {return super.isItemValid(slot, stack);}
            }
        }

        /*@Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == 1 ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }*/

    };

    public ModEnergyStorage energy = new ModEnergyStorage(50000, 2500) {
        @Override
        public void onEnergyChanged() {
            BookDuplicatorBlockEntity.this.sendUpdate();
        }
    };
    public ModFluidStorage tank = new ModFluidStorage(10000) {
        @Override
        protected void onContentsChanged() {
            BookDuplicatorBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.is(ModTags.Fluids.EXPERIENCE);
        }
    };

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.put("Energy", energy.serializeNBT());
        tank.writeToNBT(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        energy.deserializeNBT(tag.get("Energy"));
        tank.readFromNBT(tag);
    }

    @Override
    public void drops() {
        SimpleContainer container = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            container.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(level, this.worldPosition, container);
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
        fillTankFromSlot(inventory, tank, 5);

    }

}
