package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.capabilities.item.CharmStorage;
import com.sonamorningstar.eternalartifacts.client.BlockTexture;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CharmStorage>> CHARMS = ATTACHMENT_TYPES
            .register("player_charms", () -> AttachmentType.serializable(CharmStorage::new).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<BlockTexture>> TEXTURE = ATTACHMENT_TYPES
            .register("texture", () -> AttachmentType.serializable(BlockTexture::new).build());


}
