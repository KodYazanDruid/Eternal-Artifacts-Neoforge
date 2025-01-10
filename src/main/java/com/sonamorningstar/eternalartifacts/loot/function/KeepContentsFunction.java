package com.sonamorningstar.eternalartifacts.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModularEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModLoots;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KeepContentsFunction extends LootItemConditionalFunction {
    public static final Codec<KeepContentsFunction> CODEC = RecordCodecBuilder.create(p -> commonFields(p).apply(p, KeepContentsFunction::new));

    public static @NotNull Builder<?> builder() {
        return simpleBuilder(KeepContentsFunction::new);
    }

    protected KeepContentsFunction(List<LootItemCondition> pPredicates) {
        super(pPredicates);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext ctx) {
        BlockEntity entity = ctx.getParam(LootContextParams.BLOCK_ENTITY);
        if (!(entity instanceof MachineBlockEntity<?> machine)) return stack;
        Level level = ctx.getLevel();
        ItemStack tool = ctx.getParam(LootContextParams.TOOL);
        if (!tool.is(ModTags.Items.TOOLS_WRENCH)) return stack;
        CompoundTag nbt = stack.getOrCreateTag();
        IFluidHandler tankBe = level.getCapability(Capabilities.FluidHandler.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, null);
        IEnergyStorage energyBe = level.getCapability(Capabilities.EnergyStorage.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, null);
        IItemHandler invBe = level.getCapability(Capabilities.ItemHandler.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, null);
        if (tankBe instanceof AbstractFluidTank tank) {
            CompoundTag fluidNbt = new CompoundTag();
            ListTag listTag = new ListTag();
            for (int i = 0; i < tank.getTanks(); i++) {
                AbstractFluidTank tanki = tank.get(i);
                CompoundTag entry = new CompoundTag();
                FluidStack fluidStack = tanki.getFluidInTank(0);
                if (fluidStack.isEmpty()) continue;
                fluidStack.writeToNBT(entry);
                entry.putInt("TankNo", i);
                listTag.add(entry);
            }
            fluidNbt.put("Tanks", listTag);
            fluidNbt.putInt("BiggestTankIndex", tank.getTanks());
            nbt.put("Fluid", fluidNbt);
        }
        if (energyBe instanceof ModEnergyStorage energy && !(energyBe instanceof ModularEnergyStorage)) {
            CompoundTag energyNbt = new CompoundTag();
            energyNbt.put("EnergyAmount", energy.serializeNBT());
            energyNbt.putInt("MaxExtract", energy.getMaxExtract());
            energyNbt.putInt("MaxReceive", energy.getMaxReceive());
            energyNbt.putInt("MaxEnergyStored", energy.getMaxEnergyStored());
            nbt.put("Energy", energyNbt);
        }
        if (invBe instanceof ModItemStorage inventory) {
            nbt.put("Inventory", inventory.serializeNBT());
        }
        CompoundTag additionalTag = new CompoundTag();
        machine.saveContents(additionalTag);
        nbt.put("MachineData", additionalTag);
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLoots.KEEP_CONTENTS_FUNCTION.get();
    }
}
