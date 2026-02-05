package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
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
import net.neoforged.neoforge.attachment.AttachmentUtils;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Map;

@Getter
public class BookDuplicator extends GenericMachine {
    public BookDuplicator(BlockPos pPos, BlockState pBlockState) {
        super(ModMachines.BOOK_DUPLICATOR, pPos, pBlockState);
        setMaxProgress(500);
        setEnergy(this::createDefaultEnergy);
        setTank(() -> createBasicTank(16000, fs -> fs.is(ModTags.Fluids.EXPERIENCE), true, true));
        outputSlots.add(2);
        setInventory(() -> createBasicInventory(3, outputSlots, (slot, stack) -> switch (slot) {
			case 0 -> stack.is(Items.ENCHANTED_BOOK) || stack.is(Items.WRITTEN_BOOK);
			case 1 -> stack.is(Items.BOOK) || stack.is(Items.WRITABLE_BOOK);
			default -> false;
		}));
        screenInfo.setArrowPos(104, 49);
        screenInfo.setSlotPosition(80, 48, 0);
        screenInfo.setSlotPosition(80, 26, 1);
        screenInfo.setSlotPosition(134, 48, 2);
    }

    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoInputFluids(lvl, pos);
        performAutoInputItems(lvl, pos);
        performAutoOutputItems(lvl, pos);
        
        ItemStack inputBook = inventory.getStackInSlot(0);
        ItemStack consumableBook = inventory.getStackInSlot(1);
        ItemStack output = inventory.getStackInSlot(2);

        if (inputBook.getItem() == Items.ENCHANTED_BOOK &&
            consumableBook.getItem() == Items.BOOK &&
            output.isEmpty() && canWork(energy)) {

            Map<Enchantment, Integer> enchantmentMap = EnchantmentHelper.getEnchantments(inputBook);
            //Book should only have one enchantment.
            if(enchantmentMap.size() == 1) {
                Map.Entry<Enchantment, Integer> enchantEntry = enchantmentMap.entrySet().stream().findFirst().get();
                //Check if it is a treasure or not. Do not allow copying treasure enchantments.
                Enchantment enchantment = enchantEntry.getKey();
                if(canDupeEnch(enchantment)) {
                    int level = enchantEntry.getValue();
                    //Only level 3 and lower enchantment levels can be duped.
                    if (level > 0 && level <= 3) {
                        progress(()-> tank.getFluidAmount(0) < 100 * level, () -> {
                            inventory.extractItem(1, 1, false);
                            tank.drainForced(100 * level, IFluidHandler.FluidAction.EXECUTE);
                            ItemStack copy = new ItemStack(Items.ENCHANTED_BOOK);
                            EnchantmentHelper.setEnchantments(Map.of(enchantment, level), copy);
                            inventory.setStackInSlot(2, copy);
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
            progress(()-> tank.getFluidAmount(0) < 50, ()-> {
                inventory.extractItem(1, 1, false);
                tank.drainForced(50, IFluidHandler.FluidAction.EXECUTE);
                ItemStack copy = new ItemStack(Items.WRITTEN_BOOK);
                CompoundTag compoundtag = inputBook.getTag().copy();
                compoundtag.putInt("generation", WrittenBookItem.getGeneration(inputBook) + 1);
                copy.setTag(compoundtag);
                AttachmentUtils.copyStackAttachments(inputBook, copy);
                inventory.setStackInSlot(2, copy);
            }, energy);
        }
    }
    
    protected boolean canDupeEnch(Enchantment enchantment) {
        return isVersatile() || !enchantment.isTreasureOnly();
    }
}
