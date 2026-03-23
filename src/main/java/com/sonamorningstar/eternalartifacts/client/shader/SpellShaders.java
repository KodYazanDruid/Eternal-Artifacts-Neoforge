package com.sonamorningstar.eternalartifacts.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class SpellShaders {
	private static final Map<String, ShaderInstance> shaders = new HashMap<>();
	
	public static ShaderInstance SPELL_CLOUD;
	public static ShaderInstance BLACK_HOLE;
	
	public static void registerShaders(RegisterShadersEvent event) {
		try {
			registerShader("spell_cloud", DefaultVertexFormat.POSITION_COLOR, event, s -> SPELL_CLOUD = s);
			registerShader("black_hole", DefaultVertexFormat.POSITION, event, s -> BLACK_HOLE = s);
		} catch (IOException e) {
			EternalArtifacts.LOGGER.error("Failed to parse shader: ", e);
		}
	}
	
	private static void registerShader(String name, VertexFormat vertexFormat, RegisterShadersEvent event, Consumer<ShaderInstance> cons) throws IOException {
		ShaderInstance shader = new ShaderInstance(
			event.getResourceProvider(),
			new ResourceLocation(EternalArtifacts.MODID, name),
			vertexFormat
		);
		event.registerShader(shader, shaderInstance -> {
			shaders.put(name, shaderInstance);
			cons.accept(shaderInstance);
		});
	}
	
	public static void useShader(String name) {
		if (shaders.containsKey(name)) {
			RenderSystem.setShader(() -> shaders.get(name));
		}
	}
	
	public static void releaseShader() {
		RenderSystem.setShader(() -> null);
	}
	
	public static void updateBlackHoleTime() {
		if (BLACK_HOLE != null && BLACK_HOLE.getUniform("Time") != null) {
			float time = (float) Minecraft.getInstance().level.getGameTime() / 20.0F;
			BLACK_HOLE.getUniform("Time").set(time);
		}
	}
}
