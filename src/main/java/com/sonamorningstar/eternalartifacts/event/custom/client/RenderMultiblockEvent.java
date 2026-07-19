package com.sonamorningstar.eternalartifacts.event.custom.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import lombok.Getter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

@Getter
public abstract class RenderMultiblockEvent extends Event {
	private final AbstractMultiblockBlockEntity master;
	private final PoseStack pose;
	private final float partialTick;
	private final MultiBufferSource buffer;
	private final int packedLight;
	private final int packedOverlay;
	
	public RenderMultiblockEvent(AbstractMultiblockBlockEntity master, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		this.master = master;
		this.pose = pose;
		this.partialTick = partialTick;
		this.buffer = buffer;
		this.packedLight = packedLight;
		this.packedOverlay = packedOverlay;
	}
	
	public static class Pre extends RenderMultiblockEvent implements ICancellableEvent {
		public Pre(AbstractMultiblockBlockEntity master, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
			super(master, partialTick, pose, buffer, packedLight, packedOverlay);
		}
	}
	
	public static class Post extends RenderMultiblockEvent {
		public Post(AbstractMultiblockBlockEntity master, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
			super(master, partialTick, pose, buffer, packedLight, packedOverlay);
		}
	}
}
