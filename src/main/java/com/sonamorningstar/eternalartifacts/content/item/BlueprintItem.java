package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.container.BlueprintMenu;
import com.sonamorningstar.eternalartifacts.content.item.base.IActiveStack;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BlueprintItem extends Item implements IActiveStack {
    public static final String FILLED = "Filled";

    public BlueprintItem(Properties props) {
        super(props);
    }

/*    public static NonNullList<ItemStack> getFakeItems(ItemStack blueprint) {
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
    }*/

/*    public static void updateFakeItems(ItemStack blueprint, NonNullList<ItemStack> itemStacks) {
        ListTag listTag = new ListTag();
        for (int i = 0; i < itemStacks.size(); i++) {
            ItemStack stack = itemStacks.get(i);
            CompoundTag compound = new CompoundTag();
            compound.putByte("Slot", (byte) i);
            stack.save(compound);
            listTag.add(compound);
        }
        CompoundTag tag = blueprint.getOrCreateTag();
        tag.put("Pattern", listTag);
    }*/

/*    public static void updateFakeItem(ItemStack blueprint, int slot, ItemStack itemStack) {
        ListTag listTag = blueprint.getTag() != null ? blueprint.getTag().getList("Pattern", 10) : new ListTag();
        CompoundTag compound = new CompoundTag();
        compound.putByte("Slot", (byte) slot);
        itemStack.save(compound);
        listTag.set(slot, compound);
        CompoundTag tag = blueprint.getOrCreateTag();
        tag.put("Pattern", listTag);
    }*/

    @Override
    public boolean isActive(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(FILLED);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }else if(player instanceof ServerPlayer serverPlayer){
            serverPlayer.openMenu(
                    new SimpleMenuProvider(
                            (id, inv, pl) -> new BlueprintMenu(id, inv, stack),
                            Component.translatable(stack.getDescriptionId())),
                    buff -> buff.writeItem(stack));
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }
}
