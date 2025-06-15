package com.sonamorningstar.eternalartifacts.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.content.block.entity.PictureScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModDynamicTextures {
	
	public static void tellMinecraft(Runnable task) {
		Minecraft.getInstance().tell(task);
	}
	
	public static void registerDynamicTexture(PictureScreen screen, InputStream pngStream) {
		Minecraft.getInstance().execute(() -> {
			try{
				NativeImage image = NativeImage.read(pngStream);
				String resourceId = "screen_" + screen.getBlockPos().getX() + "_" + screen.getBlockPos().getY() + "_" + screen.getBlockPos().getZ();
				ResourceLocation resourceLocation = new ResourceLocation(MODID, "textures/dynamic/" + resourceId);
				
				TextureManager textureManager = Minecraft.getInstance().getTextureManager();
				DynamicTexture texture = new DynamicTexture(image);
				textureManager.register(resourceLocation, texture);
				
				screen.setImageResource(resourceLocation);
				screen.setLoading(false);
			} catch (IOException e) {
				screen.setLoading(false);
				screen.setLoadFailed(true);
				EternalArtifacts.LOGGER.error("Failed to load image for PictureScreen at {}: {}", screen.getBlockPos(), e.getMessage());
			}
		});
	}
	
	public static void releaseDynamicTexture(PictureScreen screen) {
		if (screen.getImageResource() != null) {
			Minecraft.getInstance().getTextureManager().release(screen.getImageResource());
			screen.setImageResource(null);
			screen.setLoadFailed(false);
			screen.setLoading(false);
		}
	}
}
