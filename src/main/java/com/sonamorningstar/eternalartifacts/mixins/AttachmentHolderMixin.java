package com.sonamorningstar.eternalartifacts.mixins;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AttachmentHolder.class)
public abstract class AttachmentHolderMixin {
	
	@Shadow protected abstract void deserializeAttachments(CompoundTag tag);
	
	@Unique
	public void deserializeAttachments_Exp(CompoundTag tag) {
		deserializeAttachments(tag);
	}
}
