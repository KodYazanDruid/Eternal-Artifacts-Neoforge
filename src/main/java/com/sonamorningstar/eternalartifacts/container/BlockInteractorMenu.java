package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.BlockBreaker;
import lombok.Getter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

@Getter
public class BlockInteractorMenu extends AbstractMachineMenu {
    protected final boolean isBlockBreaker;
    
    public BlockInteractorMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(menuType, id, inv, entity, data);
        this.isBlockBreaker = entity instanceof BlockBreaker;
        this.fakeSlots.addListener(this::slotsChanged);
        
        if (beInventory != null) {
            if (isBlockBreaker) {
                addSlot(new SlotItemHandler(beInventory, 0, 54, 35));
                
                for (int i = 1; i < beInventory.getSlots(); i++) {
                    int outputIndex = i - 1;
                    int col = outputIndex % 4;
                    int row = outputIndex / 4;
                    int x = 80 + col * 18;
                    int y = 26 + row * 18;
                    addSlot(new SlotItemHandler(beInventory, i, x, y));
                }
            } else {
                for (int i = 0; i < beInventory.getSlots(); i++) {
                    int col = i % 2;
                    int row = i / 2;
                    int x = 80 + col * 18;
                    int y = 26 + row * 18;
                    addSlot(new SlotItemHandler(beInventory, i, x, y));
                }
            }
        }
        
    }

}
