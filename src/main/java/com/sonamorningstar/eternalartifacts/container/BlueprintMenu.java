package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.container.slot.BlueprintFakeSlot;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.content.item.BlueprintItem;
import com.sonamorningstar.eternalartifacts.content.recipe.blueprint.BlueprintPattern;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.network.BlueprintIngredientsToClient;
import com.sonamorningstar.eternalartifacts.network.Channel;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.List;

@Getter
public class BlueprintMenu extends AbstractModContainerMenu {
    private final ItemStack blueprint;
    private BlueprintPattern pattern;
    private final SimpleContainer fakeResult;
    private final Player player;
    public BlueprintMenu(int id, Inventory inv, ItemStack blueprint) {
        super(ModMenuTypes.BLUEPRINT.get(), id);
        this.blueprint = blueprint;
        this.player = inv.player;
        addPlayerInventoryAndHotbar(inv, 8, 66);
        pattern = BlueprintItem.getPattern(blueprint);
        pattern.getFakeItems().addListener(this::slotsChanged);
        addFakeSlots(30, 17);
        fakeResult = new SimpleContainer(1);
        addSlot(new FakeSlot(fakeResult, 0, 124, 35, true));
        findRecipe();
    }
    
    @Override
    public void initializeContents(int pStateId, List<ItemStack> pItems, ItemStack pCarried) {
        super.initializeContents(pStateId, pItems, pCarried);
    }
    
    public static BlueprintMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf extraData) {
        return new BlueprintMenu(id, inv, extraData.readItem());
    }

    @Override
    public boolean stillValid(Player player) {
        return !player.isDeadOrDying();
    }
    
    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            BlueprintItem.toggleUseTags(blueprint);
        }
        return false;
    }
    
    private void addFakeSlots(int xOff, int yOff) {
        if (blueprint.getItem() instanceof BlueprintItem) {
            for (int i = 0; i < 9; i++) {
                addSlot(new BlueprintFakeSlot(pattern, i, xOff + (i % 3) * 18, yOff + (i / 3) * 18, false));
            }
        }
    }
    
    @Override
    public void slotsChanged(Container con) {
        super.slotsChanged(con);
        if (con == pattern.getFakeItems()) {
            BlueprintItem.updateFakeItems(blueprint, pattern.getFakeItems().getItems());
            pattern = BlueprintItem.getPattern(blueprint);
            pattern.getFakeItems().addListener(this::slotsChanged);
            slots.stream().filter(s -> s instanceof BlueprintFakeSlot).forEach(s -> {
                ((BlueprintFakeSlot) s).pattern = pattern;
            });
            findRecipe();
        }
    }

    private void findRecipe() {
        if (player instanceof ServerPlayer serverPlayer) {
            Level level = serverPlayer.level();
            if (!(level instanceof ServerLevel sl)) return;
            ItemStack stack = ItemStack.EMPTY;
			pattern.findRecipe(serverPlayer);
            CraftingRecipe recipe = pattern.getRecipe();
            if (recipe != null) {
                ItemStack result = recipe.assemble(pattern.getFakeItems(), sl.registryAccess());
                if (result.isItemEnabled(sl.enabledFeatures())) {
                    stack = result;
                }
            }
            if (!stack.isEmpty()) {
                CompoundTag tag = blueprint.getOrCreateTag();
                CompoundTag resultTag = new CompoundTag();
                stack.save(resultTag);
                tag.put("CachedResult", resultTag);
            } else if (blueprint.hasTag()){
                blueprint.removeTagKey("CachedResult");
            }
            synchIngredients(serverPlayer);
            fakeResult.setItem(0, stack);
            setRemoteSlot(slots.size() - 1, stack);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), slots.size() - 1, stack));
        }
    }
    
    public void synchIngredients(ServerPlayer serverPlayer) {
        NonNullList<Ingredient> sorted = NonNullList.withSize(9, Ingredient.EMPTY);
        NonNullList<Ingredient> ingredients = pattern.getIngredients();
        for (int i = 0; i < 9; i++) {
            ItemStack item = pattern.getFakeItems().getItem(i);
            if (!item.isEmpty()) {
                for (Ingredient ingredient : ingredients) {
                    if (ingredient.test(item)) {
                        sorted.set(i, ingredient);
                        break;
                    }
                }
            }
        }
        Channel.sendToPlayer(new BlueprintIngredientsToClient(containerId, sorted), serverPlayer);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int index) {
        return ItemStack.EMPTY;
    }
}
