package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.*;
import com.sonamorningstar.eternalartifacts.container.BookDuplicatorMenu;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BookDuplicatorBlockEntity extends SidedTransferBlockEntity<BookDuplicatorMenu> implements IHasInventory, IHasFluidTank, IHasEnergy {
    public BookDuplicatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BOOK_DUPLICATOR.get(), pPos, pBlockState, BookDuplicatorMenu::new);
        setMaxProgress(500);
    }

    // 0 -> input
    // 1 -> output
    // 2 -> book/writable book slot
    // 3 -> fluid filler
    @Getter
    public ModItemStorage inventory = new ModItemStorage(4) {
        @Override
        protected void onContentsChanged(int slot) {
            if(slot != 1) progress = 0;
            BookDuplicatorBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            switch (slot) {
                case 0 -> {return stack.is(Items.ENCHANTED_BOOK) || stack.is(Items.WRITTEN_BOOK);}
                case 1 -> {return false;}
                case 2 -> {return stack.is(Items.BOOK) || stack.is(Items.WRITABLE_BOOK);}
                case 3 -> {
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
    };
    @Getter
    public ModEnergyStorage energy = new ModEnergyStorage(50000, 2500) {
        @Override
        public void onEnergyChanged() {
            BookDuplicatorBlockEntity.this.sendUpdate();
        }
    };
    @Getter
    public ModFluidStorage tank = new ModFluidStorage(10000) {
        @Override
        protected void onContentsChanged() {
            BookDuplicatorBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.is(ModTags.Fluids.EXPERIENCE);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
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

    public void tick(Level lvl, BlockPos pos, BlockState st) {
        fillTankFromSlot(inventory, tank, 3);
        performAutoInputFluids(lvl, pos, tank);
        performAutoInput(lvl, pos, inventory);
        performAutoOutput(lvl, pos, inventory, 1, 3);
        ItemStack inputBook = inventory.getStackInSlot(0);
        ItemStack output = inventory.getStackInSlot(1);
        ItemStack consumableBook = inventory.getStackInSlot(2);

        if (inputBook.getItem() == Items.ENCHANTED_BOOK &&
                consumableBook.getItem() == Items.BOOK &&
                output.isEmpty() &&
                hasEnergy(consume, energy)) {

            Map<Enchantment, Integer> enchantmentMap = EnchantmentHelper.getEnchantments(inputBook);
            //Book should have one enchantment.
            if(enchantmentMap.size() == 1){
                Map.Entry<Enchantment, Integer> enchant = enchantmentMap.entrySet().stream().findFirst().get();
                //Check if it is a treasure or not. Do not allow copying treasure enchantments.
                if(!enchant.getKey().isTreasureOnly()){
                    int level = enchant.getValue();
                    //Only level 3 and lower enchantment levels can be duped.
                    if (level > 0 && level <= 3) {
                        progressAndCraft(inputBook.copy(), consumableBook, 1000 * level);
                    }
                }
            }
            //Written book copy stuff.
        } else if (inputBook.getItem() == Items.WRITTEN_BOOK &&
                    consumableBook.getItem() == Items.WRITABLE_BOOK &&
                    output.isEmpty() &&
                    hasEnergy(consume, energy) &&
                    inputBook.getTag() != null &&
                    WrittenBookItem.getGeneration(inputBook) < 2) {
            ItemStack copy = new ItemStack(Items.WRITTEN_BOOK);
            CompoundTag compoundtag = inputBook.getTag().copy();
            compoundtag.putInt("generation", WrittenBookItem.getGeneration(inputBook) + 1);
            copy.setTag(compoundtag);
            net.neoforged.neoforge.attachment.AttachmentUtils.copyStackAttachments(inputBook, copy);
            progressAndCraft(copy, consumableBook, 500);
        }

    }

    private void progressAndCraft(ItemStack result, @Nullable ItemStack consumableBook, int nousCost) {
        if(nousCost > tank.getFluidAmount()) {
            progress = 0;
            return;
        }
        energy.extractEnergyForced(consume, false);
        progress++;
        if (progress >= maxProgress) {
            consumableBook.shrink(1);
            tank.drainForced(nousCost, IFluidHandler.FluidAction.EXECUTE);
            inventory.setStackInSlot(1, result);
            progress = 0;
        }
    }
}
