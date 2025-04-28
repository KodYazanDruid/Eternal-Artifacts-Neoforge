package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.BookDuplicatorMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
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

@Getter
public class BookDuplicatorMachineBlockEntity extends SidedTransferMachineBlockEntity<BookDuplicatorMenu> {
    public BookDuplicatorMachineBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BOOK_DUPLICATOR.get(), pPos, pBlockState, BookDuplicatorMenu::new);
        setMaxProgress(500);
        setEnergy(this::createDefaultEnergy);
        setTank(() -> createBasicTank(16000, fs -> fs.is(ModTags.Fluids.EXPERIENCE), true, true));
        setInventory(() -> new ModItemStorage(4) {
            @Override
            protected void onContentsChanged(int slot) {
                if(slot != 1) progress = 0;
                BookDuplicatorMachineBlockEntity.this.sendUpdate();
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
        });
        outputSlots.add(1);
        outputSlots.add(3);
    }

    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        fillTankFromSlot(inventory, tank, 3);
        performAutoInputFluids(lvl, pos);
        performAutoInputItems(lvl, pos);
        performAutoOutputItems(lvl, pos);
        ItemStack inputBook = inventory.getStackInSlot(0);
        ItemStack output = inventory.getStackInSlot(1);
        ItemStack consumableBook = inventory.getStackInSlot(2);

        if (inputBook.getItem() == Items.ENCHANTED_BOOK &&
                consumableBook.getItem() == Items.BOOK &&
                output.isEmpty() &&
                hasEnergy(energyPerTick, energy)) {

            Map<Enchantment, Integer> enchantmentMap = EnchantmentHelper.getEnchantments(inputBook);
            //Book should have one enchantment.
            if(enchantmentMap.size() == 1){
                Map.Entry<Enchantment, Integer> enchant = enchantmentMap.entrySet().stream().findFirst().get();
                //Check if it is a treasure or not. Do not allow copying treasure enchantments.
                if(!enchant.getKey().isTreasureOnly()){
                    int level = enchant.getValue();
                    //Only level 3 and lower enchantment levels can be duped.
                    if (level > 0 && level <= 3) {
                        progress(()-> tank.getFluidAmount(0) < 1000 * level, () -> {
                            consumableBook.shrink(1);
                            tank.drainForced(1000 * level, IFluidHandler.FluidAction.EXECUTE);
                            ItemStack copy = new ItemStack(Items.ENCHANTED_BOOK);
                            EnchantmentHelper.setEnchantments(Map.of(enchant.getKey(), level), copy);
                            inventory.setStackInSlot(1, copy);
                        }, energy);
                    }
                }
            }
            //Written stack copy stuff.
        } else if (inputBook.getItem() == Items.WRITTEN_BOOK &&
                    consumableBook.getItem() == Items.WRITABLE_BOOK &&
                    output.isEmpty() &&
                    inputBook.getTag() != null &&
                    WrittenBookItem.getGeneration(inputBook) < 2) {
            progress(()-> tank.getFluidAmount(0) < 500, ()-> {
                consumableBook.shrink(1);
                tank.drainForced(500, IFluidHandler.FluidAction.EXECUTE);
                ItemStack copy = new ItemStack(Items.WRITTEN_BOOK);
                CompoundTag compoundtag = inputBook.getTag().copy();
                compoundtag.putInt("generation", WrittenBookItem.getGeneration(inputBook) + 1);
                copy.setTag(compoundtag);
                net.neoforged.neoforge.attachment.AttachmentUtils.copyStackAttachments(inputBook, copy);
                inventory.setStackInSlot(1, copy);
            }, energy);
        }
    }
}
