package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.google.common.util.concurrent.Runnables;
import com.sonamorningstar.eternalartifacts.container.AnvilinatorMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.network.AnvilinatorXpCostToClient;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.util.ExperienceHelper;
import lombok.Getter;
import lombok.Setter;
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
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import oshi.util.Util;

import java.util.Map;
import java.util.Objects;

public class Anvilinator extends SidedTransferMachine<AnvilinatorMenu> {
    @Getter
    public String name = "";
    public boolean enableNaming = false;
    private AnvilUpdateEvent anvilUpdateEvent;
    private ItemStack pendingResult = ItemStack.EMPTY;
    private int pendingFluidCost = 0;
    @Setter
    @Getter
    private int currentXpCost = 0;

    public Anvilinator(BlockPos pPos, BlockState pBlockState) {
        super(ModMachines.ANVILINATOR.getBlockEntity(), pPos, pBlockState, (a, b, c, d) -> new AnvilinatorMenu(ModMachines.ANVILINATOR.getMenu(), a, b, c, d));
        setEnergy(this::createDefaultEnergy);
        setTank(() -> createBasicTank(50000, fs-> fs.is(ModTags.Fluids.EXPERIENCE), true, true, Runnables.doNothing()));
        outputSlots.add(2);
        setInventory(() -> createRecipeFinderInventory(3, outputSlots));
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
        fireEvent();
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
    
    @Override
    protected void findRecipe() {
        fireEvent();
    }
    
    private void fireEvent() {
        int xpCost = 0;
        anvilUpdateEvent = new AnvilUpdateEvent(inventory.getStackInSlot(0), inventory.getStackInSlot(1), name, xpCost, getFakePlayer());
        NeoForge.EVENT_BUS.post(anvilUpdateEvent);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoOutputItems(lvl, pos);
        performAutoInputFluids(lvl, pos);
        performAutoOutputItems(lvl, pos);
        
        ItemStack input = inventory.getStackInSlot(0);
        ItemStack secondary = inventory.getStackInSlot(1);
        ItemStack output = inventory.getStackInSlot(2);
        
        // Event-based anvil operations
        if (anvilUpdateEvent != null && !anvilUpdateEvent.isCanceled() && !anvilUpdateEvent.getOutput().isEmpty()) {
            ItemStack eventResult = anvilUpdateEvent.getOutput();
            if (ItemHandlerHelper.canItemStacksStack(output, eventResult) || output.isEmpty()) {
                if (output.getCount() + eventResult.getCount() <= 64) {
                    int cost = anvilUpdateEvent.getCost();
                    currentXpCost = cost;
                    Channel.sendToChunk(new AnvilinatorXpCostToClient(pos, currentXpCost), lvl.getChunkAt(pos));
                    int fluidCost = ExperienceHelper.totalXpForLevel(cost) * 20;
                    if (tank.getFluidAmount(0) >= fluidCost) {
                        ItemStack result = eventResult.copy();
                        if (enableNaming && !Util.isBlank(name)) result.setHoverName(Component.literal(name));
                        setPendingOperation(result, fluidCost);
                        progress(() -> pendingResult.isEmpty() || !canOutputResult(pendingResult), this::craftAnvilResult, energy);
                        return;
                    }
                }
            }
        }
        
        // Vanilla-style enchant combining
        if (!input.isEmpty() && secondary.is(Items.ENCHANTED_BOOK) && !combineEnchants(input, secondary).isEmpty()) {
            ItemStack result = combineEnchants(input, secondary);
            currentXpCost = calculateEnchantCost(secondary);
            Channel.sendToChunk(new AnvilinatorXpCostToClient(pos, currentXpCost), lvl.getChunkAt(pos));
            int fluidCost = currentXpCost * 20;
            if (canOutputResult(result) && tank.getFluidAmount(0) >= fluidCost) {
                setPendingOperation(applyNaming(result), fluidCost);
                progress(() -> pendingResult.isEmpty() || !canOutputResult(pendingResult), this::craftAnvilResult, energy);
            } else progress = 0;
        } else if (input.isDamageableItem() && input.getItem().isValidRepairItem(input, secondary)) {
            // Repairing item with its repair material
            int damage = input.getDamageValue() - Math.min(input.getDamageValue(), input.getMaxDamage() / 4);
            if (damage < 0) damage = 0;
            ItemStack result = input.copyWithCount(1);
            result.setDamageValue(damage);
            currentXpCost = calculateRepairCost(input);
            Channel.sendToChunk(new AnvilinatorXpCostToClient(pos, currentXpCost), lvl.getChunkAt(pos));
            int fluidCost = currentXpCost * 20;
            if (canOutputResult(result) && tank.getFluidAmount(0) >= fluidCost) {
                setPendingOperation(applyNaming(result), fluidCost);
                progress(() -> pendingResult.isEmpty() || !canOutputResult(pendingResult), this::craftAnvilResult, energy);
            } else progress = 0;
        } else if (input.isDamageableItem() && input.is(secondary.getItem())) {
            // Combining two identical damageable items
            int in1 = input.getMaxDamage() - input.getDamageValue();
            int sec1 = secondary.getMaxDamage() - secondary.getDamageValue();
            int perc = sec1 + input.getMaxDamage() * 12 / 100;
            int total = in1 + perc;
            int repair = input.getMaxDamage() - total;
            if (repair < 0) repair = 0;
            ItemStack copy = input.copy();
            copy.setDamageValue(repair);
            ItemStack result = !combineEnchants(input, secondary).isEmpty() ? combineEnchants(copy, secondary) : copy;
            currentXpCost = calculateCombineCost(input, secondary);
            Channel.sendToChunk(new AnvilinatorXpCostToClient(pos, currentXpCost), lvl.getChunkAt(pos));
            int fluidCost = currentXpCost * 20;
            if (canOutputResult(result) && tank.getFluidAmount(0) >= fluidCost) {
                setPendingOperation(applyNaming(result), fluidCost);
                progress(() -> pendingResult.isEmpty() || !canOutputResult(pendingResult), this::craftAnvilResult, energy);
            } else progress = 0;
        } else if (secondary.isEmpty() && enableNaming && !input.isEmpty()) {
            // Renaming section
            ItemStack result = input.copyWithCount(1);
            currentXpCost = calculateRenameCost();
            Channel.sendToChunk(new AnvilinatorXpCostToClient(pos, currentXpCost), lvl.getChunkAt(pos));
            int fluidCost = currentXpCost * 20;
            if (canOutputResult(result) && tank.getFluidAmount(0) >= fluidCost) {
                setPendingOperation(applyNaming(result), fluidCost);
                progress(() -> pendingResult.isEmpty() || !canOutputResult(pendingResult), this::craftAnvilResult, energy);
            } else progress = 0;
        } else {
            currentXpCost = 0;
            Channel.sendToChunk(new AnvilinatorXpCostToClient(pos, currentXpCost), lvl.getChunkAt(pos));
            progress = 0;
        }
    }
    
    private void setPendingOperation(ItemStack result, int fluidCost) {
        this.pendingResult = result;
        this.pendingFluidCost = fluidCost;
    }
    
    private boolean canOutputResult(ItemStack result) {
        ItemStack output = inventory.getStackInSlot(2);
        if (output.isEmpty()) return true;
        return ItemHandlerHelper.canItemStacksStack(output, result) && output.getCount() + result.getCount() <= 64;
    }
    
    private ItemStack applyNaming(ItemStack result) {
        if (enableNaming && !Util.isBlank(name) && !result.getHoverName().equals(Component.literal(name))) {
            result.setHoverName(Component.literal(name));
        } else if (enableNaming && Util.isBlank(name)) {
            result.resetHoverName();
        }
        return result;
    }
    
    private void craftAnvilResult() {
        if (pendingResult.isEmpty()) return;
        ItemStack input = inventory.getStackInSlot(0);
        ItemStack secondary = inventory.getStackInSlot(1);
        
        input.shrink(1);
        if (!secondary.isEmpty()) secondary.shrink(1);
        tank.drainForced(pendingFluidCost, IFluidHandler.FluidAction.EXECUTE);
        inventory.insertItemForced(2, pendingResult.copy(), false);
        
        pendingResult = ItemStack.EMPTY;
        pendingFluidCost = 0;
    }
    
    private int calculateEnchantCost(ItemStack book) {
        Map<Enchantment, Integer> bookEnchs = EnchantmentHelper.getEnchantments(book);
        int levelCost = 0;
        for (var entry : bookEnchs.entrySet()) {
            levelCost += entry.getValue();
        }
        return ExperienceHelper.totalXpForLevel(Math.max(1, levelCost));
    }
    
    private int calculateRepairCost(ItemStack input) {
        int levelCost = 1 + (input.getBaseRepairCost() / 2);
        return ExperienceHelper.totalXpForLevel(levelCost);
    }
    
    private int calculateCombineCost(ItemStack input, ItemStack secondary) {
        int levelCost = 2;
        if (!combineEnchants(input, secondary).isEmpty()) {
            Map<Enchantment, Integer> bookEnchs = EnchantmentHelper.getEnchantments(secondary);
            for (var entry : bookEnchs.entrySet()) {
                levelCost += entry.getValue();
            }
        }
        levelCost += (input.getBaseRepairCost() / 2);
        return ExperienceHelper.totalXpForLevel(levelCost);
    }
    
    private int calculateRenameCost() {
        //return ExperienceHelper.totalXpForLevel(1);
        //Renaming is from the house.
        return 0;
    }

    private ItemStack combineEnchants(ItemStack item, ItemStack book) {
        if(book.is(Items.ENCHANTED_BOOK) && !item.isBookEnchantable(book)) return ItemStack.EMPTY;
        Map<Enchantment, Integer> itemEnchs = EnchantmentHelper.getEnchantments(item);
        Map<Enchantment, Integer> itemEnchsFirst = EnchantmentHelper.getEnchantments(item);
        Map<Enchantment, Integer> bookEnchs = EnchantmentHelper.getEnchantments(book);
        ItemStack copy = item.copy();

        for(var entry : bookEnchs.entrySet()) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();
            if(!item.is(Items.ENCHANTED_BOOK) && !enchant.canEnchant(item)) continue;

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
}
