package com.sonamorningstar.eternalartifacts.client.resources.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SmartJukeboxSoundInstance extends AbstractTickableSoundInstance {
    private SoundEvent music;
    public int playTick;
    public SmartJukeboxSoundInstance(SoundEvent soundEvent, Vec3 location) {
        super(soundEvent, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.x = location.x();
        this.y = location.y();
        this.z = location.z();
        this.music = soundEvent;
        this.volume = 4.0F;
        this.delay = 10;
    }

    public void writeToNBT(CompoundTag nbt) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Name", music.getLocation().toString());
        tag.putInt("PlayTick", this.playTick);
        nbt.put("Music", tag);
    }

    public static @Nullable SmartJukeboxSoundInstance readFromNBT(CompoundTag nbt, BlockPos pos) {
        CompoundTag tag = nbt.getCompound("Music");
        SoundEvent music = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(tag.getString("Name")));
        if (music == null) return null;
        int playTick = tag.getInt("PlayTick");
        SmartJukeboxSoundInstance soundInstance = new SmartJukeboxSoundInstance(music, pos.getCenter());
        soundInstance.playTick = playTick;
        return soundInstance;
    }

    @Override
    public void tick() {

    }
}
