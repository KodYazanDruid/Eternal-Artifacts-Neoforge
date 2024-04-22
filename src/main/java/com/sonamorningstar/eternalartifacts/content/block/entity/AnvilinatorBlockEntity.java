package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capablities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capablities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capablities.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.AnvilinatorMenu;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.util.ExperienceHelper;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.Util;

import java.util.Map;
import java.util.Objects;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AnvilinatorBlockEntity extends BlockEntity implements MenuProvider {
    public String name = "";
    public boolean enableNaming = false;
    protected final ContainerData DATA;
    private int progress = 0;
    private int maxProgress = 100;
    private int consumePerTick = 10;
    private int cost = 0;
    private Lazy<FakePlayer> fakePlayer = Lazy.of(() -> FakePlayerHelper.getFakePlayer(level));
    private AnvilUpdateEvent anvilUpdateEvent;

    public AnvilinatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ANVILINATOR.get(), pPos, pBlockState);
        this.DATA = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> AnvilinatorBlockEntity.this.progress;
                    case 1 -> AnvilinatorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }
            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> AnvilinatorBlockEntity.this.progress = pValue;
                    case 1 -> AnvilinatorBlockEntity.this.maxProgress = pValue;
                }
            }
            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public String getName() {
        return this.name;
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
        anvilUpdateEvent = new AnvilUpdateEvent(ITEM_HANDLER.getStackInSlot(INPUT_SLOT), ITEM_HANDLER.getStackInSlot(SECONDARY_SLOT), name, (int) cost, fakePlayer.get());
    }

    @SubscribeEvent
    private static void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ANVILINATOR.get(),
                (be, context) -> be.ITEM_HANDLER);

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ANVILINATOR.get(),
                (be, context) -> be.ENERGY_HANDLER);

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.ANVILINATOR.get(),
                (be, context) -> be.FLUID_TANK);
    }

    private static final int INPUT_SLOT = 0;
    private static final int SECONDARY_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private static final int FLUID_SLOT = 3;
    private final ItemStackHandler ITEM_HANDLER = new ModItemStorage(4) {
        @Override
        protected void onContentsChanged(int slot) {
            if(slot != FLUID_SLOT && slot != OUTPUT_SLOT) progress = 0;
            anvilUpdateEvent = new AnvilUpdateEvent(ITEM_HANDLER.getStackInSlot(INPUT_SLOT), ITEM_HANDLER.getStackInSlot(SECONDARY_SLOT), name, cost, fakePlayer.get());
            AnvilinatorBlockEntity.this.sendUpdate();
        }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            switch (slot) {
                case 0, 1, 2:
                    return true;
                case 3 :
                    IFluidHandlerItem fh = FluidUtil.getFluidHandler(stack).orElse(null);
                    if(fh == null) return false;
                    else {
                        FluidStack fluidStack = fh.getFluidInTank(0);
                        return !fluidStack.isEmpty() && fh.isFluidValid(0, fluidStack);
                    }
                default:
                    return super.isItemValid(slot, stack);
            }
        }
    };

    public final ModEnergyStorage ENERGY_HANDLER = new ModEnergyStorage(50000, 2500) {
        @Override
        public void onEnergyChanged() { AnvilinatorBlockEntity.this.sendUpdate(); }
    };

    private final FluidTank FLUID_TANK = new ModFluidStorage(64000) {
        @Override
        protected void onContentsChanged() {
            AnvilinatorBlockEntity.this.sendUpdate();
        }
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().is(ModTags.Fluids.EXPERIENCE);
        }
    };

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(ITEM_HANDLER.getSlots());
        for(int i = 0; i < ITEM_HANDLER.getSlots(); i++) {
            inventory.setItem(i, ITEM_HANDLER.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(ModBlocks.ANVILINATOR.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new AnvilinatorMenu(pContainerId, pPlayerInventory, this, this.DATA);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        ENERGY_HANDLER.deserializeNBT(pTag.get("Energy"));
        ITEM_HANDLER.deserializeNBT(pTag.getCompound("Inventory"));
        FLUID_TANK.readFromNBT(pTag);
        progress = pTag.getInt("anvilator_progress");
        enableNaming = pTag.getBoolean("enable_naming");
        name = pTag.getString("name");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Energy", ENERGY_HANDLER.serializeNBT());
        pTag.put("Inventory", ITEM_HANDLER.serializeNBT());
        FLUID_TANK.writeToNBT(pTag);
        pTag.putInt("anvilator_progress", progress);
        pTag.putBoolean("enable_naming", enableNaming);
        pTag.putString("name", name);
    }

    //This is the where magic happens.
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        ItemStack input = ITEM_HANDLER.getStackInSlot(INPUT_SLOT);
        ItemStack secondary = ITEM_HANDLER.getStackInSlot(SECONDARY_SLOT);

        //Taking fluid from the bucket.
        ItemStack stack = ITEM_HANDLER.getStackInSlot(FLUID_SLOT);
        if(!stack.isEmpty() && FLUID_TANK.getFluidAmount() < FLUID_TANK.getCapacity()) {
            IFluidHandlerItem itemHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if(itemHandler != null && FLUID_TANK.isFluidValid(itemHandler.getFluidInTank(0)) && FLUID_TANK.getFluid().getFluid() == itemHandler.getFluidInTank(0).getFluid()) {
                int amountToDrain = FLUID_TANK.getCapacity() - FLUID_TANK.getFluidAmount();
                int amount = itemHandler.drain(amountToDrain, IFluidHandler.FluidAction.SIMULATE).getAmount();
                if (amount > 0) {
                    FLUID_TANK.fill(itemHandler.drain(amountToDrain, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                }
                if (amount <= amountToDrain) {
                    ITEM_HANDLER.setStackInSlot(FLUID_SLOT, itemHandler.getContainer());
                }
            }
        }

        //Check if there is an event first.
        if(!onAnvilatorChance(anvilUpdateEvent)) return;

        if(!input.isEmpty() && secondary.is(Items.ENCHANTED_BOOK) && !combineEnchants(input, secondary).isEmpty()) {
            //Transferring enchants from book. Combining books.
            progressAndCraft(input, secondary, combineEnchants(input, secondary));

        }else if(input.isDamageableItem() && input.getItem().isValidRepairItem(input, secondary)) {
            //Repairing item with its repair item.
            int damage = input.getDamageValue() - Math.min(input.getDamageValue(), input.getMaxDamage() / 4);
            if(damage <= 0 ) damage = 0;
            ItemStack copy = input.copyWithCount(1);
            copy.setDamageValue(damage);
            progressAndCraft(input, secondary, copy);

        }else if(input.isDamageableItem() && input.is(secondary.getItem())) {
            int in1 = input.getMaxDamage() - input.getDamageValue();
            int sec1 = secondary.getMaxDamage() - secondary.getDamageValue();
            int perc = sec1 + input.getMaxDamage() * 12 / 100;
            int total = in1 + perc;
            int repair = input.getMaxDamage() - total;
            if(repair <= 0) repair = 0;
            ItemStack copy = input.copy();
            copy.setDamageValue(repair);
            if(!combineEnchants(input, secondary).isEmpty()) progressAndCraft(input, secondary, combineEnchants(copy, secondary));
            else progressAndCraft(input, secondary, copy);
        }else if(secondary.isEmpty() && enableNaming) {
            //Renaming section.
            progressAndCraft(input, null, input.copyWithCount(1));
        }
    }

    private ItemStack combineEnchants(ItemStack item, ItemStack book) {
        if(book.is(Items.ENCHANTED_BOOK) && !item.isBookEnchantable(book)) return ItemStack.EMPTY;
        //Creating two times to compare at the end if both are same or not.
        Map<Enchantment, Integer> itemEnchs = EnchantmentHelper.getEnchantments(item);
        Map<Enchantment, Integer> itemEnchsFirst = EnchantmentHelper.getEnchantments(item);
        Map<Enchantment, Integer> bookEnchs = EnchantmentHelper.getEnchantments(book);
        ItemStack copy = item.copy();

        for(var entry : bookEnchs.entrySet()) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();
            if(!item.is(Items.ENCHANTED_BOOK) && !enchant.canEnchant(item)) continue;

            //Check compatibility.
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

    private boolean onAnvilatorChance(AnvilUpdateEvent event) {
        ItemStack input = ITEM_HANDLER.getStackInSlot(INPUT_SLOT);
        if(input.isEmpty()) return true;
        ItemStack secondary = ITEM_HANDLER.getStackInSlot(SECONDARY_SLOT);
        ItemStack output = ITEM_HANDLER.getStackInSlot(OUTPUT_SLOT);
        if(NeoForge.EVENT_BUS.post(event).isCanceled()) return false;
        ItemStack result = event.getOutput();
        if(result.isEmpty()) return true;
        if(enableNaming && !Util.isBlank(name)) result.setHoverName(Component.literal(name));
        double reelCost = ExperienceHelper.totalXpForLevel(event.getCost());
        int fluidAmount = (int) (reelCost * 20);
        if((output.isEmpty() || ItemHandlerHelper.canItemStacksStack(output, result)) && FLUID_TANK.getFluidAmount() >= fluidAmount) {
            progress++;
            ENERGY_HANDLER.extractEnergy(consumePerTick, false);
            setChanged();
            if (progress >= maxProgress) {
                input.shrink(1);
                secondary.shrink(1);
                FLUID_TANK.drain(fluidAmount, IFluidHandler.FluidAction.EXECUTE);
                ITEM_HANDLER.insertItem(OUTPUT_SLOT, result, false);
                progress = 0;
                return false;
            }
        }else {
            progress = 0;
        }
        return false;
    }

    private void progressAndCraft(ItemStack input, @Nullable ItemStack secondary, ItemStack output){
        if(ITEM_HANDLER.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount() >= 64) return;
        if(output.isEmpty() && !ItemHandlerHelper.canItemStacksStack(ITEM_HANDLER.getStackInSlot(OUTPUT_SLOT), output)) return;
        progress++;
        ENERGY_HANDLER.extractEnergy(consumePerTick, false);
        setChanged();
        if(progress >= maxProgress) {
            input.shrink(1);

            //Renaming stuff.
            if(enableNaming && !Util.isBlank(name) && !input.getHoverName().equals(Component.literal(name))) {
                output.setHoverName(Component.literal(name));
            } else if(enableNaming) {
                output.resetHoverName();
            }

            if(secondary != null) secondary.shrink(1);
            //TODO: Calculate from xp cost instead of level.
            //FLUID_TANK.drain( cost * 20, IFluidHandler.FluidAction.EXECUTE);
            ITEM_HANDLER.insertItem(OUTPUT_SLOT, output, false);
            progress = 0;
        }
    }

    private void sendUpdate(){
        setChanged();
        if(this.level != null) this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public FluidStack getFluidStack() {
        return FLUID_TANK.getFluid();
    }

    public IFluidHandler getFluidHandler() {
        return this.FLUID_TANK;
    }

    public int getStoredEnergy() {
        return ENERGY_HANDLER.getEnergyStored();
    }

    public int getEnergyCapacity() {
        return ENERGY_HANDLER.getMaxEnergyStored();
    }
}
