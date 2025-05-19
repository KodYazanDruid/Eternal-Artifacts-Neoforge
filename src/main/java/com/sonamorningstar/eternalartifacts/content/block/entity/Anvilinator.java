package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.google.common.util.concurrent.Runnables;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.AnvilinatorMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.ExperienceHelper;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.Util;

import java.util.Map;
import java.util.Objects;

public class Anvilinator extends SidedTransferMachine<AnvilinatorMenu> {
    @Getter
    public String name = "";
    public boolean enableNaming = false;
    private int cost = 0;
    private final FakePlayer fakePlayer;
    private AnvilUpdateEvent anvilUpdateEvent;

    /*INPUT_SLOT = 0;
    SECONDARY_SLOT = 1;
    OUTPUT_SLOT = 2;
    FLUID_SLOT = 3;*/

    public Anvilinator(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ANVILINATOR.get(), pPos, pBlockState, AnvilinatorMenu::new);
        setEnergy(this::createDefaultEnergy);
        fakePlayer = FakePlayerHelper.getFakePlayer(this, level);
        setTank(() -> createBasicTank(64000, fs-> fs.is(ModTags.Fluids.EXPERIENCE), true, true, Runnables.doNothing()));
        outputSlots.add(2);
        setInventory(() -> new ModItemStorage(4) {
            @Override
            protected void onContentsChanged(int slot) {
                if(slot != 3 && !outputSlots.contains(slot)) progress = 0;
                if(slot == 0 && this.getStackInSlot(0).isEmpty()) progress = 0;
                anvilUpdateEvent = new AnvilUpdateEvent(this.getStackInSlot(0), this.getStackInSlot(1), name, cost, fakePlayer);
                Anvilinator.this.sendUpdate();
            }
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                switch (slot) {
                    case 0, 1: return true;
                    case 2: return false;
                    case 3 :
                        IFluidHandlerItem fh = FluidUtil.getFluidHandler(stack).orElse(null);
                        if(fh == null) return false;
                        else {
                            FluidStack fluidStack = fh.getFluidInTank(0);
                            return !fluidStack.isEmpty() && fh.isFluidValid(0, fluidStack);
                        }
                    default:
                        return super.isItemValid(slot, stack);
                }
            }
        });
    }

    public void setName(String name) {
        if(!name.equals(this.name)) {
            this.name = name;
            sendUpdate();
        }
    }

    public boolean getEnableNaming() {
        return this.enableNaming;
    }
    public void setEnableNaming(boolean enableNaming) {
        if(enableNaming != this.enableNaming) {
            this.enableNaming = enableNaming;
            sendUpdate();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        anvilUpdateEvent = new AnvilUpdateEvent(inventory.getStackInSlot(0), inventory.getStackInSlot(1), name, cost, fakePlayer);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        enableNaming = tag.getBoolean("EnableNaming");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("EnableNaming", enableNaming);
    }

    //This is the where magic happens.
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        ItemStack input = inventory.getStackInSlot(0);
        ItemStack secondary = inventory.getStackInSlot(1);
        performAutoOutputItems(lvl, pos);
        performAutoInputFluids(lvl, pos);
        performAutoOutputItems(lvl, pos);
        fillTankFromSlot(inventory, tank, 3);

        //Check if there is an event first.
        if(!onAnvilatorChance(anvilUpdateEvent)) return;

        if(!input.isEmpty() && secondary.is(Items.ENCHANTED_BOOK) && !combineEnchants(input, secondary).isEmpty()) {
            //Transferring enchants from stack. Combining books.
            progressAndCraft(input, secondary, combineEnchants(input, secondary));

        }else if(input.isDamageableItem() && input.getItem().isValidRepairItem(input, secondary)) {
            //Repairing item with its repair item.
            int damage = input.getDamageValue() - Math.min(input.getDamageValue(), input.getMaxDamage() / 4);
            if(damage <= 0 ) damage = 0;
            ItemStack copy = input.copyWithCount(1);
            copy.setDamageValue(damage);
            progressAndCraft(input, secondary, copy);

        }else if(input.isDamageableItem() && input.is(secondary.getItem())) {
            int in1 = input.getMaxDamage() - input.getDamageValue();
            int sec1 = secondary.getMaxDamage() - secondary.getDamageValue();
            int perc = sec1 + input.getMaxDamage() * 12 / 100;
            int total = in1 + perc;
            int repair = input.getMaxDamage() - total;
            if(repair <= 0) repair = 0;
            ItemStack copy = input.copy();
            copy.setDamageValue(repair);
            if(!combineEnchants(input, secondary).isEmpty()) progressAndCraft(input, secondary, combineEnchants(copy, secondary));
            else progressAndCraft(input, secondary, copy);
        }else if(secondary.isEmpty() && enableNaming) {
            //Renaming section.
            progressAndCraft(input, null, input.copyWithCount(1));
        }
    }

    private ItemStack combineEnchants(ItemStack item, ItemStack book) {
        if(book.is(Items.ENCHANTED_BOOK) && !item.isBookEnchantable(book)) return ItemStack.EMPTY;
        //Creating two times to compare at the end if both are same or not.
        Map<Enchantment, Integer> itemEnchs = EnchantmentHelper.getEnchantments(item);
        Map<Enchantment, Integer> itemEnchsFirst = EnchantmentHelper.getEnchantments(item);
        Map<Enchantment, Integer> bookEnchs = EnchantmentHelper.getEnchantments(book);
        ItemStack copy = item.copy();

        for(var entry : bookEnchs.entrySet()) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();
            if(!item.is(Items.ENCHANTED_BOOK) && !enchant.canEnchant(item)) continue;

            //Check compatibility.
            boolean compatFlag = true;
            for (Enchantment existing : itemEnchs.keySet()) {
                compatFlag = existing.equals(enchant) || enchant.isCompatibleWith(existing);
            }
            if(!compatFlag) continue;

            if(!itemEnchs.containsKey(enchant)) {
                itemEnchs.put(enchant, level);
                EnchantmentHelper.setEnchantments(itemEnchs, copy);
            } else if(Objects.equals(itemEnchs.get(enchant), level)) {
                itemEnchs.put(enchant, Math.min(level + 1, enchant.getMaxLevel()));
                EnchantmentHelper.setEnchantments(itemEnchs, copy);
            } else {
                itemEnchs.put(enchant, Math.max(level, itemEnchs.get(enchant)));
                EnchantmentHelper.setEnchantments(itemEnchs, copy);
            }
        }

        if(itemEnchs.equals(itemEnchsFirst)) return ItemStack.EMPTY;
        return copy;
    }

    private boolean onAnvilatorChance(AnvilUpdateEvent event) {
        ItemStack input = inventory.getStackInSlot(0);
        if(input.isEmpty()) return true;
        ItemStack secondary = inventory.getStackInSlot(1);
        ItemStack output = inventory.getStackInSlot(2);
        if(NeoForge.EVENT_BUS.post(event).isCanceled()) return false;
        ItemStack result = event.getOutput();
        if(result.isEmpty()) return true;
        if(enableNaming && !Util.isBlank(name)) result.setHoverName(Component.literal(name));
        double reelCost = ExperienceHelper.totalXpForLevel(event.getCost());
        int fluidAmount = (int) (reelCost * 20);
        if(output.getCount() + result.getCount() >= 64) return true;
        if((output.isEmpty() || ItemHandlerHelper.canItemStacksStack(output, result)) && tank.getFluidAmount(0) >= fluidAmount) {
            progress++;
            spendEnergy(energyPerTick, energy);
            setChanged();
            if (progress >= maxProgress) {
                input.shrink(1);
                secondary.shrink(1);
                tank.drainForced(fluidAmount, IFluidHandler.FluidAction.EXECUTE);
                //result.setCount(result.getCount() + output.getCount());
                inventory.insertItemForced(2, result, false);
                progress = 0;
                return false;
            }
        }else {
            progress = 0;
        }
        return false;
    }

    private void progressAndCraft(ItemStack input, @Nullable ItemStack secondary, ItemStack result){
        ItemStack output = inventory.getStackInSlot(2);
        if(output.getCount() + result.getCount() >= 64) return;
        if(result.isEmpty() || ( !output.isEmpty() && !ItemHandlerHelper.canItemStacksStack(output, result))) return;
        progress++;
        spendEnergy(energyPerTick, energy);
        setChanged();
        if(progress >= maxProgress) {
            input.shrink(1);

            //Renaming stuff.
            if(enableNaming && !Util.isBlank(name) && !input.getHoverName().equals(Component.literal(name))) {
                result.setHoverName(Component.literal(name));
            } else if(enableNaming) {
                result.resetHoverName();
            }

            if(secondary != null) secondary.shrink(1);
            //TODO: Calculate from xp cost instead of level.
            inventory.insertItemForced(2, result, false);
            progress = 0;
        }
    }
}
