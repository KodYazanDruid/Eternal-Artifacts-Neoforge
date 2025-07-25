package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> HOLY_DAGGER_ACTIVATE = registerSoundEvent("holy_dagger_activate");
    public static final DeferredHolder<SoundEvent, SoundEvent> FINAL_CUT_EFFECT = registerSoundEvent("final_cut_effect");

    public static final DeferredHolder<SoundEvent, SoundEvent> DUCK_AMBIENT = registerSoundEvent("duck_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> DUCK_STEP = registerSoundEvent("duck_step");

    public static final DeferredHolder<SoundEvent, SoundEvent> WRENCH = registerSoundEvent("wrench");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        return SOUNDS.register(name, ()-> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, name)));
    }
}
