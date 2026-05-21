package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMultiblockMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.Map;

public class MultiblockItemHatchMenu extends AbstractMultiblockMenu {
	public MultiblockItemHatchMenu(int id, Inventory inv, FriendlyByteBuf buf) {
		this(id, inv, ((AbstractMultiblockBlockEntity) inv.player.level().getBlockEntity(buf.readBlockPos())), new SimpleContainerData(2));
	}
	public MultiblockItemHatchMenu(int id, Inventory inv, AbstractMultiblockBlockEntity multiblockEntity, ContainerData data) {
		super(ModMenuTypes.MULTIBLOCK_ITEM_HATCH.get(), id, inv, multiblockEntity, data);
        
        if (beInventory != null) {
            String[] pattern = multiblockEntity.getHatchSlotPattern();
            Map<Character, Integer> config = multiblockEntity.getHatchSlotConfig();
            
            if (pattern != null && config != null) {
                generateSlotPattern(beInventory, pattern, config, multiblockEntity.getHatchSlotStartX(), multiblockEntity.getHatchSlotStartY(), 18, 18);
            } else {
                int[] accessibleSlots = multiblockEntity.getHatchSlots();
                if (accessibleSlots != null) {
                    for (int i = 0; i < accessibleSlots.length; i++) {
                        addSlot(new SlotItemHandler(beInventory, accessibleSlots[i], 8 + (i % 9) * 18, 18 + (i / 9) * 18));
                    }
                } else {
                    for (int i = 0; i < beInventory.getSlots(); i++) {
                        addSlot(new SlotItemHandler(beInventory, i, 8 + (i % 9) * 18, 18 + (i / 9) * 18));
                    }
                }
            }
        }
	}
}
