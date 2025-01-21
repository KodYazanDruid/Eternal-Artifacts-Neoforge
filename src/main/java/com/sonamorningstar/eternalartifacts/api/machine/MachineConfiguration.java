package com.sonamorningstar.eternalartifacts.api.machine;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;

import static com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity.TransferType;
import static com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity.RedstoneType;

@Getter
@RequiredArgsConstructor
public class MachineConfiguration implements INBTSerializable<CompoundTag> {
    private final SidedTransferMachineBlockEntity<?> machine;
    private final SideConfig sideConfig = new SideConfig();
    private final RedstoneConfig redstoneConfig = new RedstoneConfig();
    private final AutoConfig autoConfig = new AutoConfig();

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("SideConfigs", sideConfig.serializeNBT());
        nbt.put("AutoConfigs", autoConfig.serializeNBT());
        nbt.put("RedstoneConfigs", redstoneConfig.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        sideConfig.deserializeNBT(nbt.getList("SideConfigs", Tag.TAG_COMPOUND));
        autoConfig.deserializeNBT(nbt.getList("AutoConfigs", Tag.TAG_COMPOUND));
        redstoneConfig.deserializeNBT(nbt.getList("RedstoneConfigs", Tag.TAG_COMPOUND));
    }

    public static class AutoConfig implements INBTSerializable<ListTag> {
        private final Map<Integer, Boolean> configs = new HashMap<>();
        @Override
        public ListTag serializeNBT() {
            ListTag autoConfigs = new ListTag();
            this.configs.forEach((k, v) -> {
                CompoundTag entry = new CompoundTag();
                entry.putInt("Index", k);
                entry.putBoolean("Enabled", v);
                autoConfigs.add(entry);
            });
            return autoConfigs;
        }

        @Override
        public void deserializeNBT(ListTag nbt) {
            for(int i = 0; i < nbt.size(); i++) {
                CompoundTag entry = nbt.getCompound(i);
                configs.put(entry.getInt("Index"), entry.getBoolean("Enabled"));
            }
        }
    }

    public static class RedstoneConfig implements INBTSerializable<ListTag> {
        private final Map<Integer, RedstoneType> configs = new HashMap<>();
        @Override
        public ListTag serializeNBT() {
            ListTag redstoneConfigs = new ListTag();
            configs.forEach((k, v) -> {
                CompoundTag entry = new CompoundTag();
                entry.putInt("Index", k);
                entry.putString("Type", v.toString());
                redstoneConfigs.add(entry);
            });
            return redstoneConfigs;
        }

        @Override
        public void deserializeNBT(ListTag nbt) {
            for(int i = 0; i < nbt.size(); i++) {
                CompoundTag entry = nbt.getCompound(i);
                configs.put(entry.getInt("Index"), RedstoneType.valueOf(entry.getString("Type")));
            }
        }
    }

    public static class SideConfig implements INBTSerializable<ListTag> {
        private final Map<Integer, TransferType> configs = new HashMap<>();
        @Override
        public ListTag serializeNBT() {
            ListTag sideConfigs = new ListTag();
            configs.forEach((k, v) -> {
                CompoundTag entry = new CompoundTag();
                entry.putInt("Index", k);
                entry.putString("Type", v.toString());
                sideConfigs.add(entry);
            });
            return sideConfigs;
        }

        @Override
        public void deserializeNBT(ListTag nbt) {
            for(int i = 0; i < nbt.size(); i++) {
                CompoundTag entry = nbt.getCompound(i);
                configs.put(entry.getInt("Index"), TransferType.valueOf(entry.getString("Type")));
            }
        }
    }

}
