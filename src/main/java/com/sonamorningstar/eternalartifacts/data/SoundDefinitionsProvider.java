package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.core.ModSounds;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class SoundDefinitionsProvider extends net.neoforged.neoforge.common.data.SoundDefinitionsProvider {

    protected SoundDefinitionsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, MODID, helper);
    }

    @Override
    public void registerSounds() {
        add(ModSounds.HOLY_DAGGER_ACTIVATE.get(),
                definition().subtitle("%s.subtitle.%s".formatted(MODID, ModSounds.HOLY_DAGGER_ACTIVATE.get().getLocation().getPath()))
                        .with(sound(ModSounds.HOLY_DAGGER_ACTIVATE.getId()))
        );
    }
}
