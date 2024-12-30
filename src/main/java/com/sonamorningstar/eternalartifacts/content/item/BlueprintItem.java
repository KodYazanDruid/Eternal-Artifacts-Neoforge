package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.container.BlueprintMenu;
import com.sonamorningstar.eternalartifacts.content.item.base.IActiveStack;
import com.sonamorningstar.eternalartifacts.content.recipe.blueprint.RecipePattern;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleContainerCrafterWrapped;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class BlueprintItem extends Item implements IActiveStack {
    public static final String FILLED = "Filled";

    public BlueprintItem(Properties props) {
        super(props);
    }

    public static NonNullList<ItemStack> getFakeItems(ItemStack blueprint) {
        NonNullList<ItemStack> fakeItems = NonNullList.withSize(9, ItemStack.EMPTY);
        if (blueprint.hasTag()) {
            ListTag listTag = blueprint.getTag().getList("Pattern", 10);
            if (!listTag.isEmpty()) {
                for (int i = 0; i < listTag.size(); i++) {
                    CompoundTag compound = listTag.getCompound(i);
                    int slot = compound.getByte("Slot");
                    fakeItems.set(slot, ItemStack.of(compound));
                }
            }
        }
        return fakeItems;
    }

    public static void updateFakeItems(ItemStack blueprint, NonNullList<ItemStack> itemStacks) {
        ListTag listTag = new ListTag();
        for (int i = 0; i < itemStacks.size(); i++) {
            ItemStack stack = itemStacks.get(i);
            if (!stack.isEmpty()){
                CompoundTag compound = new CompoundTag();
                compound.putByte("Slot", (byte) i);
                stack.save(compound);
                listTag.add(compound);
            }
        }
        if (!listTag.isEmpty()) {
            CompoundTag tag = blueprint.getOrCreateTag();
            tag.put("Pattern", listTag);
            tag.putBoolean(FILLED, true);
        }else {
            blueprint.removeTagKey("Pattern");
            blueprint.removeTagKey(FILLED);
        }
    }

    @Override
    public boolean isActive(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(FILLED);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack blueprint = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (player instanceof ServerPlayer serverPlayer) {
                var container = new SimpleContainerCrafterWrapped(getFakeItems(blueprint).toArray(ItemStack[]::new));
                //var container = new SimpleContainerCrafterWrapped(EMPTY_ITEMS.toArray(ItemStack[]::new));
                if (container.isEmpty()) return InteractionResultHolder.pass(blueprint);
                RecipePattern pattern = new RecipePattern(container);
                pattern.findRecipe(serverPlayer);

                CraftingRecipe recipe = pattern.getRecipe();
                ItemStack result = recipe.assemble(container, level.registryAccess());
                List<ItemStack> remainders = recipe.getRemainingItems(container);
                if (!result.isEmpty()) {
                    List<ItemStack> foundItems = new ArrayList<>();
                    for (int i = 0; i < container.getContainerSize(); i++) {
                        ItemStack patternStack = container.getItem(i);
                        Item patternItem = patternStack.getItem();
                        ItemStack foundItem;
                        if (patternStack.hasTag()) {
                            CompoundTag patternTag = patternStack.getTag();
                            foundItem = PlayerHelper.findItemWithTag(serverPlayer, patternItem, patternTag);
                            foundItems.add(foundItem);
                        } else {
                            foundItem = PlayerHelper.findItem(serverPlayer, patternItem);
                            foundItems.add(foundItem);
                        }
                    }
                    for (int i = 0; i < container.getContainerSize(); i++) {
                        ItemStack conStack = container.getItem(i);
                        for (ItemStack foundItem : foundItems) {
                            if (ItemStack.isSameItemSameTags(conStack, foundItem)) {
                                container.removeItemNoUpdate(i);
                            }
                        }
                    }
                    if (container.isEmpty()) {
                        PlayerHelper.giveItemOrPop(serverPlayer, result);
                        remainders.forEach(s -> PlayerHelper.giveItemOrPop(serverPlayer, s));
                        //serverPlayer.triggerRecipeCrafted(recipePair.getSecond(), foundItems);
                        serverPlayer.triggerRecipeCrafted(pattern.getRecipeHolder(), foundItems);
                        player.awardStat(Stats.ITEM_USED.get(blueprint.getItem()));
                        for (ItemStack foundItem : foundItems) {
                            foundItem.shrink(1);
                        }
                    }
                }
            }
            return InteractionResultHolder.sidedSuccess(blueprint, level.isClientSide());
        }else if(player instanceof ServerPlayer serverPlayer) {
            openMenu(serverPlayer, blueprint);
            return InteractionResultHolder.success(blueprint);
        }
        return InteractionResultHolder.pass(blueprint);
    }

    private void openMenu(ServerPlayer player, ItemStack blueprint) {
        player.openMenu(new SimpleMenuProvider(
                (id, inv, pl) -> new BlueprintMenu(id, inv, blueprint),
                Component.translatable(blueprint.getDescriptionId())),
            buff -> buff.writeItem(blueprint));
    }

/*    private Pair<CraftingRecipe, RecipeHolder<CraftingRecipe>> findRecipe(ServerPlayer player, SimpleContainerCrafterWrapped fakeItems) {
        Level level = player.level();
        MinecraftServer server = level.getServer();
        if (server == null) return null;
        Optional<RecipeHolder<CraftingRecipe>> optional = server.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, fakeItems, level);
        if (optional.isPresent()) {
            RecipeHolder<CraftingRecipe> recipeholder = optional.get();
            CraftingRecipe craftingrecipe = recipeholder.value();
            if (recipeChecks(level, recipeholder)) {
                return Pair.of(craftingrecipe, recipeholder);
            }
        }
        return null;
    }

    private boolean recipeChecks(Level level, RecipeHolder<?> recipe) {
        return !(!recipe.value().isSpecial() && level.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING));
    }*/

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
