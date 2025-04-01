package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.container.BlueprintMenu;
import com.sonamorningstar.eternalartifacts.content.item.base.IActiveStack;
import com.sonamorningstar.eternalartifacts.content.recipe.blueprint.BlueprintPattern;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleContainerCrafterWrapped;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.*;

public class BlueprintItem extends Item implements IActiveStack {
    public static final String FILLED = "Filled";
    public static final String USE_TAGS = "UseTags";

    public BlueprintItem(Properties props) {
        super(props);
    }

    public static BlueprintPattern getPattern(ItemStack blueprint) {
        SimpleContainerCrafterWrapped container = new SimpleContainerCrafterWrapped(9);
        if (blueprint.hasTag()) {
            ListTag listTag = blueprint.getTag().getList("Pattern", 10);
            if (!listTag.isEmpty()) {
                for (int i = 0; i < listTag.size(); i++) {
                    CompoundTag compound = listTag.getCompound(i);
                    int slot = compound.getByte("Slot");
                    container.setItem(slot, ItemStack.of(compound));
                }
            }
        }
        return new BlueprintPattern(container);
    }

    public static void updateFakeItems(ItemStack blueprint, NonNullList<ItemStack> itemStacks) {
        ListTag listTag = new ListTag();
        boolean itemPresent = false;
        int size = itemStacks.size();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = i >= size ? ItemStack.EMPTY : itemStacks.get(i);
            if (!stack.isEmpty()) itemPresent = true;
            CompoundTag compound = new CompoundTag();
            compound.putByte("Slot", (byte) i);
            stack.save(compound);
            listTag.add(compound);
        }
        if (!itemPresent) {
            blueprint.removeTagKey("Pattern");
            blueprint.removeTagKey(FILLED);
        } else {
            CompoundTag tag = blueprint.getOrCreateTag();
            tag.put("Pattern", listTag);
            tag.putBoolean(FILLED, true);
        }
    }

    @Override
    public boolean isActive(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(FILLED);
    }
    
    public static boolean isUsingTags(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(USE_TAGS);
    }
    
    public static void toggleUseTags(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.getBoolean(USE_TAGS)) stack.removeTagKey(USE_TAGS);
            else tag.putBoolean(USE_TAGS, true);
        } else {
            stack.getOrCreateTag().putBoolean(USE_TAGS, true);
        }
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack blueprint = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (player instanceof ServerPlayer serverPlayer) {
                BlueprintPattern pattern = getPattern(blueprint);
                pattern.findRecipe(serverPlayer);
                CraftingRecipe recipe = pattern.getRecipe();
                
                if (recipe != null) {
                    ItemStack result = recipe.assemble(pattern.getFakeItems(), level.registryAccess());
                    if(!result.isEmpty() && craftFromBlueprint(serverPlayer, pattern, blueprint, result)) {
                        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, player.getSoundSource(), 0.5F, 1.0F);
                        return InteractionResultHolder.success(blueprint);
                    }
                }
            }
            return InteractionResultHolder.fail(blueprint);
        } else if(player instanceof ServerPlayer serverPlayer) {
            openMenu(serverPlayer, blueprint);
            return InteractionResultHolder.success(blueprint);
        }
        return InteractionResultHolder.pass(blueprint);
    }
    
    private boolean craftFromBlueprint(ServerPlayer player, BlueprintPattern pattern, ItemStack blueprint, ItemStack result) {
        if (!result.isItemEnabled(player.level().enabledFeatures())) return false;
        
        boolean useTags = blueprint.hasTag() && blueprint.getTag().getBoolean(USE_TAGS);
        
        if (useTags) {
            return craftUsingIngredients(player, pattern, result);
        } else {
            Map<StackKey, Integer> consolidatedItems = consolidateItems(pattern.getFakeItems());
            
            if (!hasEnoughConsolidatedItems(player, consolidatedItems)) {
                player.displayClientMessage(Component.translatable("eternalartifacts.blueprint.missing_items"), true);
                return false;
            }
            
            NonNullList<ItemStack> containerItems = NonNullList.create();
            
            for (Map.Entry<StackKey, Integer> entry : consolidatedItems.entrySet()) {
                ItemStack requiredStack = entry.getKey().stack();
                int count = entry.getValue();
                
                if (!consumeItemsAndCollectContainers(player.getInventory(), requiredStack, count, containerItems)) {
                    return false;
                }
            }
            
            result.onCraftedBy(player.level(), player, result.getCount());
            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }
            
            for (ItemStack containerItem : containerItems) {
                if (!player.getInventory().add(containerItem)) {
                    player.drop(containerItem, false);
                }
            }
            return true;
        }
    }
    
    private Map<StackKey, Integer> consolidateItems(SimpleContainerCrafterWrapped container) {
        Map<StackKey, Integer> result = new HashMap<>();
        
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            
            StackKey key = new StackKey(stack);
            result.merge(key, stack.getCount(), Integer::sum);
        }
        
        return result;
    }
    
    private boolean hasEnoughConsolidatedItems(Player player, Map<StackKey, Integer> items) {
        for (Map.Entry<StackKey, Integer> entry : items.entrySet()) {
            ItemStack requiredItem = entry.getKey().stack();
            int needed = entry.getValue();
            
            int available = 0;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (ItemStack.isSameItemSameTags(stack, requiredItem)) {
                    available += stack.getCount();
                }
            }
            
            if (available < needed) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean consumeItemsAndCollectContainers(Inventory inventory, ItemStack toConsume, int count, NonNullList<ItemStack> containerItems) {
        int remainingCount = count;
        
        for (int i = 0; i < inventory.getContainerSize() && remainingCount > 0; i++) {
            ItemStack invStack = inventory.getItem(i).copyWithCount(1);
            
            if (!invStack.isEmpty() && ItemStack.isSameItemSameTags(invStack, toConsume)) {
                while (remainingCount > 0 && !invStack.isEmpty()) {
                    if (invStack.hasCraftingRemainingItem()) {
                        ItemStack containerItem = invStack.getCraftingRemainingItem();
                        if (!containerItem.isEmpty()) {
                            containerItems.add(containerItem.copy());
                        }
                    }
                    inventory.getItem(i).shrink(1);
                    remainingCount--;
                }
            }
        }
        
        return remainingCount == 0;
    }
    
    private boolean craftUsingIngredients(ServerPlayer player, BlueprintPattern pattern, ItemStack result) {
        NonNullList<Ingredient> ingredients = pattern.getIngredients();
        Map<Ingredient, Integer> requiredIngredients = new HashMap<>();
        
        for (int i = 0; i < pattern.getFakeItems().getContainerSize(); i++) {
            ItemStack fakeItem = pattern.getFakeItems().getItem(i);
            if (!fakeItem.isEmpty()) {
                for (Ingredient ingredient : ingredients) {
                    if (ingredient.test(fakeItem)) {
                        requiredIngredients.merge(ingredient, fakeItem.getCount(), Integer::sum);
                        break;
                    }
                }
            }
        }
        
        if (!hasEnoughIngredients(player, requiredIngredients)) {
            player.displayClientMessage(Component.translatable("eternalartifacts.blueprint.missing_items"), true);
            return false;
        }
        
        NonNullList<ItemStack> containerItems = NonNullList.create();
        for (Map.Entry<Ingredient, Integer> entry : requiredIngredients.entrySet()) {
            if (!consumeIngredientAndCollectContainers(player.getInventory(), entry.getKey(), entry.getValue(), containerItems)) {
                return false;
            }
        }
        
        result.onCraftedBy(player.level(), player, result.getCount());
        if (!player.getInventory().add(result)) {
            player.drop(result, false);
        }
        
        for (ItemStack containerItem : containerItems) {
            if (!player.getInventory().add(containerItem)) {
                player.drop(containerItem, false);
            }
        }
        
        return true;
    }
    
    private boolean hasEnoughIngredients(Player player, Map<Ingredient, Integer> requiredIngredients) {
        for (Map.Entry<Ingredient, Integer> entry : requiredIngredients.entrySet()) {
            Ingredient ingredient = entry.getKey();
            int requiredCount = entry.getValue();
            
            int availableCount = 0;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);
                if (!invStack.isEmpty() && ingredient.test(invStack)) {
                    availableCount += invStack.getCount();
                }
            }
            
            if (availableCount < requiredCount) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean consumeIngredientAndCollectContainers(Inventory inventory, Ingredient ingredient, int count, NonNullList<ItemStack> containerItems) {
        int remainingCount = count;
        
        for (int i = 0; i < inventory.getContainerSize() && remainingCount > 0; i++) {
            ItemStack invStack = inventory.getItem(i);
            
            if (!invStack.isEmpty() && ingredient.test(invStack)) {
                int toRemove = Math.min(invStack.getCount(), remainingCount);
                
                for (int j = 0; j < toRemove; j++) {
                    ItemStack copyStack = invStack.copyWithCount(1);
                    if (copyStack.hasCraftingRemainingItem()) {
                        ItemStack containerItem = copyStack.getCraftingRemainingItem();
                        if (!containerItem.isEmpty()) {
                            containerItems.add(containerItem.copy());
                        }
                    }
                }
                
                invStack.shrink(toRemove);
                remainingCount -= toRemove;
            }
        }
        
        return remainingCount == 0;
    }
    
    private record StackKey(ItemStack stack) {
        public StackKey(ItemStack stack) {
            ItemStack copyStack = stack.copy();
            copyStack.setCount(1);
            this.stack = copyStack;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StackKey that)) return false;
            return ItemStack.isSameItemSameTags(stack, that.stack);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(stack.getItem(), stack.getTag());
        }
    }

    private void openMenu(ServerPlayer player, ItemStack blueprint) {
        player.openMenu(new SimpleMenuProvider(
                (id, inv, pl) -> new BlueprintMenu(id, inv, blueprint),
                Component.translatable(blueprint.getDescriptionId())),
            buff -> buff.writeItem(blueprint));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
