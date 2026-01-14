package com.sonamorningstar.eternalartifacts.registrar;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.BaseMachineBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Holds all registered components for a machine.
 * Provides type-safe access to menu, block entity, block, and item.
 */
public record MachineHolder<M extends AbstractMachineMenu, BE extends Machine<M>, B extends BaseMachineBlock<BE>, I extends BlockItem>(
    DeferredHolder<MenuType<?>, MenuType<M>> menuHolder,
    DeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> blockEntityHolder, DeferredHolder<Block, B> blockHolder,
    DeferredHolder<Item, I> itemHolder, boolean hasUniqueTexture, boolean isGeneric, boolean hasCustomRender)
    implements ItemLike {
    
    public MenuType<M> getMenu() {
        return menuHolder.get();
    }
    
    public BlockEntityType<BE> getBlockEntity() {
        return blockEntityHolder.get();
    }
    
    public B getBlock() {
        return blockHolder.get();
    }
    
    public I getItem() {
        return itemHolder.get();
    }
    
    public String getBlockTranslationKey() {
        return getBlock().getDescriptionId();
    }
    
    public ResourceLocation getBlockId() {
        return blockHolder.getId();
    }
    
    @Override
    public Item asItem() {
        return getItem();
    }
}

