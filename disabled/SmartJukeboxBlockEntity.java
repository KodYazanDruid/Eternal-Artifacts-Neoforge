package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.mojang.blaze3d.audio.Channel;
import com.sonamorningstar.eternalartifacts.capabilities.ModItemStorage;
import com.sonamorningstar.eternalartifacts.client.resources.sounds.SmartJukeboxSoundInstance;
import com.sonamorningstar.eternalartifacts.container.SmartJukeboxMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.client.ClientHooks;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

public class SmartJukeboxBlockEntity extends SidedTransferMachineBlockEntity<SmartJukeboxMenu> {
    @Getter
    private boolean isPlaying = false;
    private boolean isMusicPresents = false;
    private SmartJukeboxSoundInstance soundInstance;
    private static SoundManager soundManager;
    public static SoundEngine soundEngine;

    public SmartJukeboxBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.SMART_JUKEBOX.getBlockEntity(), pos, blockState, SmartJukeboxMenu::new);
        soundManager = Minecraft.getInstance().getSoundManager();
        setEnergy(createDefaultEnergy());
        setInventory(new ModItemStorage(6) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {return stack.is(ItemTags.MUSIC_DISCS);}
            @Override
            protected void onContentsChanged(int slot) {
                if (level != null && !level.isClientSide() && slot == 0) {
                    if (this.getStackInSlot(slot).isEmpty()) stopMusic(level, getBlockPos(), getBlockState());
                    else if (this.getStackInSlot(slot).getItem() instanceof RecordItem record && !isMusicPresents) {
                        setMusic(record.getSound(), pos);
                        playMusic();
                    }
                }
                sendUpdate();
            }
        });
        outputSlots.add(1);
        outputSlots.add(2);
        outputSlots.add(3);
        outputSlots.add(4);
        outputSlots.add(5);
    }

    @Override
    protected void saveSynced(CompoundTag tag) {
        super.saveSynced(tag);
        tag.putBoolean("IsPlaying", isPlaying);
        tag.putBoolean("IsMusicPresents", isMusicPresents);
        if (soundInstance != null) soundInstance.writeToNBT(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        isPlaying = tag.getBoolean("IsPlaying");
        isMusicPresents = tag.getBoolean("IsMusicPresents");
        soundInstance = SmartJukeboxSoundInstance.readFromNBT(tag, getBlockPos());
    }

/*    @Override
    public void onLoad() {
        super.onLoad();
        if (isMusicPresents && isPlaying && soundInstance != null) playMusic();
    }*/

    @Override
    public void setRemoved() {
        if(level != null) stopMusic(level, getBlockPos(), getBlockState());
        super.setRemoved();
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInput(lvl, pos, inventory);
        performAutoOutput(lvl, pos, inventory, outputSlots.toArray(Integer[]::new));

        if (isPlaying) {
            lvl.gameEvent(GameEvent.JUKEBOX_PLAY, pos, GameEvent.Context.of(st));
            if (soundInstance != null) soundInstance.playTick++;
        }
    }

    private void setMusic(SoundEvent music, BlockPos pos) {
        this.soundInstance = new SmartJukeboxSoundInstance(music, pos.getCenter());
        isMusicPresents = true;
        sendUpdate();
    }

    public void playMusic() {
        SoundInstance sound = ClientHooks.playSound(soundEngine, soundInstance);
        if (sound != null && level != null) {
            soundManager.play(sound);
            isPlaying = true;
            sendUpdate();
            //level.levelEvent(1010, getBlockPos(), Item.getId(inventory.getStackInSlot(0).getItem()));
            isPlaying = true;
            sendUpdate();
        }
    }

    public void pauseMusic() {
        if(soundInstance != null && level != null){
            soundEngine.instanceToChannel.get(soundInstance).execute(Channel::pause);
            //level.levelEvent(1011, getBlockPos(), 0);
            isPlaying = false;
            sendUpdate();
        }
    }

    public void resumeMusic() {
        if(soundInstance != null) {
            //soundEngine.instanceToChannel.get(soundInstance).execute(Channel::unpause);
            soundEngine.instanceToChannel.get(soundInstance).execute(channel -> {
                AL10.alSourcef(channel.source, AL11.AL_SEC_OFFSET, 50.0F);
                channel.play();
            });
            isPlaying = true;
            sendUpdate();
        }
    }

    public void stopMusic(Level lvl, BlockPos pos, BlockState st) {
        soundManager.stop(soundInstance);
        this.soundInstance = null;
        isMusicPresents = false;
        isPlaying = false;
        lvl.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, pos, GameEvent.Context.of(st));
        //lvl.levelEvent(1011, getBlockPos(), 0);
        sendUpdate();
    }

    public int getPlayTick() {
        return this.soundInstance != null ? this.soundInstance.playTick: 0;
    }
}
