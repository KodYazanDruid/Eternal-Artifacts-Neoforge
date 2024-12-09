package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.content.item.BlueprintItem;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleContainerCrafterWrapped;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.Optional;

@Getter
public class BlueprintMenu extends AbstractModContainerMenu {
    private final ItemStack blueprint;
    private final SimpleContainerCrafterWrapped fakeItems;
    private final SimpleContainer fakeResult;
    private final Player player;
    public BlueprintMenu(int id, Inventory inv, ItemStack blueprint) {
        super(ModMenuTypes.BLUEPRINT.get(), id);
        this.blueprint = blueprint;
        this.player = inv.player;
        addPlayerInventoryAndHotbar(inv, 8, 66);
        NonNullList<ItemStack> fakeItemList = BlueprintItem.getFakeItems(blueprint);
        fakeItems = new SimpleContainerCrafterWrapped(fakeItemList.toArray(ItemStack[]::new)) {
            @Override
            public void setChanged() {
                super.setChanged();
                BlueprintMenu.this.slotsChanged(this);
            }
        };
        addFakeSlots(30, 17);
        fakeResult = new SimpleContainer(1);
        addSlot(new FakeSlot(fakeResult,0, 124, 35, true));
        findRecipe();
    }

    public static BlueprintMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf extraData) {
        return new BlueprintMenu(id, inv, extraData.readItem());
    }

    @Override
    public boolean stillValid(Player player) {
        return !player.isDeadOrDying();
    }

    private void addFakeSlots(int xOff, int yOff) {
        if (blueprint.getItem() instanceof BlueprintItem) {
            for (int i = 0; i < fakeItems.getContainerSize(); i++) {
                addSlot(new FakeSlot(fakeItems, i, xOff + (i % 3) * 18, yOff + (i / 3) * 18, false));
            }
        }
    }

    @Override
    public void slotsChanged(Container con) {
        super.slotsChanged(con);
        if (con == fakeItems) {
            BlueprintItem.updateFakeItems(blueprint, fakeItems.getItems());
            findRecipe();
        }
    }

    private void findRecipe() {
        if (player instanceof ServerPlayer serverPlayer) {
            Level level = serverPlayer.level();
            ItemStack result = ItemStack.EMPTY;
            MinecraftServer server = level.getServer();
            if (server == null) return;
            Optional<RecipeHolder<CraftingRecipe>> optional = server.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, fakeItems, level);
            if (optional.isPresent()) {
                RecipeHolder<CraftingRecipe> recipeholder = optional.get();
                CraftingRecipe craftingrecipe = recipeholder.value();
                if (recipeChecks(level, recipeholder)) {
                    ItemStack assembled = craftingrecipe.assemble(fakeItems, level.registryAccess());
                    if (assembled.isItemEnabled(level.enabledFeatures())) {
                        result = assembled;
                    }
                }
            }
            fakeResult.setItem(0, result);
            setRemoteSlot(44, result);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 44, result));
        }
    }

    private boolean recipeChecks(Level level, RecipeHolder<?> recipe) {
        return !(!recipe.value().isSpecial() && level.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING));
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int index) {
        return ItemStack.EMPTY;
    }
}
