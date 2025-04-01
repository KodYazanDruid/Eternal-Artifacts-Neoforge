package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.core.ModSounds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.registries.DeferredHolder;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class SoundDefinitionsProvider extends net.neoforged.neoforge.common.data.SoundDefinitionsProvider {

    protected SoundDefinitionsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, MODID, helper);
    }

    @Override
    public void registerSounds() {
        registerDefinition(ModSounds.HOLY_DAGGER_ACTIVATE);
        registerDefinition(ModSounds.DUCK_AMBIENT, "duck_idle_1", "duck_idle_2", "duck_idle_3", "duck_idle_4", "duck_idle_5");
        registerDefinition(ModSounds.DUCK_STEP, "duck_step_1", "duck_step_2", "duck_step_3", "duck_step_4", "duck_step_5", "duck_step_6");
        registerDefinition(ModSounds.WRENCH);

    }

    private void registerDefinition(DeferredHolder<SoundEvent, SoundEvent> sound) {
        SoundDefinition definition = definition().subtitle("%s.subtitle.%s".formatted(MODID, sound.get().getLocation().getPath()));
        add(sound.get(), definition.with(sound(sound.getId())));

    }

    private void registerDefinition(DeferredHolder<SoundEvent, SoundEvent> sound, String... sounds) {
        SoundDefinition definition = definition().subtitle("%s.subtitle.%s".formatted(MODID, sound.get().getLocation().getPath()));
        ResourceLocation[] soundArray = createSoundArray(sounds);
        for (ResourceLocation location : soundArray) {
            definition.with(sound(location));
        }
        add(sound.get(), definition);
    }

    private ResourceLocation[] createSoundArray(String[] array) {
        ResourceLocation[] ids = new ResourceLocation[array.length];
        for(int i = 0; i < array.length; i++) {
            ids[i] = new ResourceLocation(MODID, array[i]);
        }
        return ids;
    }
}
