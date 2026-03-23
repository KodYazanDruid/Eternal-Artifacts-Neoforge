package com.sonamorningstar.eternalartifacts.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.sonamorningstar.eternalartifacts.client.shader.SpellShaders;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModRenderTypes extends RenderType {
	private ModRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}
	
	public static final RenderType AREA_OUTLINE = create(
			MODID + ":area_outline",
			DefaultVertexFormat.POSITION_COLOR_NORMAL,
			VertexFormat.Mode.LINES,
			256,
			false,
			false,
			RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_LINES_SHADER)
				.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(2.0)))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(NO_LIGHTMAP)
				.setLayeringState(POLYGON_OFFSET_LAYERING)
				.setDepthTestState(NO_DEPTH_TEST)
				.setWriteMaskState(COLOR_WRITE)
				.createCompositeState(false)
	);
	
	public static final RenderType AREA_FACE = create(
			MODID + ":area_face",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS,
			256,
			false,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(POSITION_COLOR_SHADER)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(NO_LIGHTMAP)
				.setLayeringState(POLYGON_OFFSET_LAYERING)
				.createCompositeState(false)
	);
	
	public static final RenderType BLACK_HOLE = create(
		MODID + ":black_hole",
		DefaultVertexFormat.POSITION,
		VertexFormat.Mode.QUADS,
		256,
		false,
		false,
		RenderType.CompositeState.builder()
			.setShaderState(new RenderStateShard.ShaderStateShard(() -> SpellShaders.BLACK_HOLE))
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
			.setCullState(NO_CULL)
			.setLightmapState(NO_LIGHTMAP)
			.createCompositeState(false)
	);
	
	public static final RenderType SPELL_CLOUD = create(
		MODID + ":spell_cloud",
		DefaultVertexFormat.POSITION_COLOR,
		VertexFormat.Mode.QUADS,
		256,
		false,
		false,
		RenderType.CompositeState.builder()
			.setShaderState(new RenderStateShard.ShaderStateShard(() -> SpellShaders.SPELL_CLOUD))
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
			.setCullState(NO_CULL)
			.setLightmapState(NO_LIGHTMAP)
			.createCompositeState(false)
	);
}