package com.sonamorningstar.eternalartifacts.registrar;

import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModRegistries {

    public static final Registry<Spell> SPELL = new RegistryBuilder<>(Keys.SPELL).sync(true).create();


    public static final class Keys {
        public static final ResourceKey<Registry<Spell>> SPELL = createRegistryKey("spell");

        private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
            return ResourceKey.createRegistryKey(new ResourceLocation(MODID, name));
        }
    }
}
