package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capablities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.container.BioFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BioFurnaceEntity extends MachineBlockEntity implements MenuProvider, ITickable {
    @SubscribeEvent
    private static void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.BIOFURNACE.get(),
                (be, context) -> be.ITEM_HANDLER);

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.BIOFURNACE.get(),
                (be, context) -> be.ENERGY_HANDLER);
    }

    private final ItemStackHandler ITEM_HANDLER = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) { BioFurnaceEntity.this.sendUpdate(); }
    };
    private static final int FUEL_SLOT = 0;
    private final ModEnergyStorage ENERGY_HANDLER = new ModEnergyStorage(100000, 40, 5000) {
        @Override
        public void onEnergyChanged() { BioFurnaceEntity.this.sendUpdate(); }
    };

    public BioFurnaceEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BIOFURNACE.get(), pPos, pBlockState);
    }

    @Override
    public Component getDisplayName() { return Component.translatable(ModBlocks.BIOFURNACE.get().getDescriptionId()); }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BioFurnaceMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        ENERGY_HANDLER.deserializeNBT(pTag.get("Energy"));
        ITEM_HANDLER.deserializeNBT(pTag.getCompound("Inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Energy", ENERGY_HANDLER.serializeNBT());
        pTag.put("Inventory", ITEM_HANDLER.serializeNBT());
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        generatePower();
        distributePower();
    }

    private void generatePower() {
        ItemStack fuel = ITEM_HANDLER.getStackInSlot(FUEL_SLOT);
        if(ENERGY_HANDLER.getEnergyStored() < ENERGY_HANDLER.getMaxEnergyStored()) {
            if(progress <= 0) {
                if(fuel.isEmpty() || !fuel.is(ModItems.ORANGE.get())) return;
                ITEM_HANDLER.extractItem(FUEL_SLOT, 1, false);
                progress = maxProgress;
            }else{
                progress--;
                ENERGY_HANDLER.receiveEnergy(40, false);
            }
            setChanged();
        }
    }

    private void distributePower() {
        if(ENERGY_HANDLER.getEnergyStored() <=0 ) return;
        for(Direction direction : Direction.values()){
            BlockEntity be = level.getBlockEntity(getBlockPos().relative(direction));
            if(be != null) {
                IEnergyStorage es = level.getCapability(Capabilities.EnergyStorage.BLOCK, be.getBlockPos(), direction.getOpposite());
                if(es != null && es.canReceive()) {
                    int received = es.receiveEnergy(Math.min(ENERGY_HANDLER.getEnergyStored() , 5000), false);
                    ENERGY_HANDLER.extractEnergy(received, false);
                    setChanged();
                }
            }
        }
    }

}


