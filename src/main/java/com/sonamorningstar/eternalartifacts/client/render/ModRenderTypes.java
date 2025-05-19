package com.sonamorningstar.eternalartifacts.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.sonamorningstar.eternalartifacts.client.shader.SpellShaders;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModRenderTypes extends RenderType {
	private ModRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}
	
	public static final Supplier<RenderType> SPELL_CLOUD = () ->  {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
			.setShaderState(new RenderStateShard.ShaderStateShard(() -> SpellShaders.SPELL_CLOUD.get()))
			.setTransparencyState(new RenderStateShard.TransparencyStateShard(
				"spell_cloud_transparency",
				() -> {
					RenderSystem.enableBlend();
					RenderSystem.blendFuncSeparate(
						GlStateManager.SourceFactor.SRC_ALPHA,
						GlStateManager.DestFactor.ONE,
						GlStateManager.SourceFactor.ONE,
						GlStateManager.DestFactor.ZERO
					);
				},
				() -> {
					RenderSystem.disableBlend();
					RenderSystem.defaultBlendFunc();
				}
			))
			.setCullState(NO_CULL)
			.setLightmapState(LIGHTMAP)
			.setWriteMaskState(COLOR_WRITE)
			.setOutputState(TRANSLUCENT_TARGET)
			.createCompositeState(false);
		
		return create(
			MODID + ":spell_cloud",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS,
			256,
			false,
			false,
			state
		);
	};
}