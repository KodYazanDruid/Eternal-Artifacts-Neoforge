package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.client.resources.ModDynamicTextures;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Setter
@Getter
public class PictureScreen extends ModBlockEntity {
	private String imageUrl = "";
	private ResourceLocation imageResource = null;
	private boolean isLoading = false;
	private boolean loadFailed = false;
	
	public PictureScreen(BlockPos pos, BlockState state) {
		super(ModBlockEntities.PICTURE_SCREEN.get(), pos, state);
	}
	
	public void setImageUrl(String url) {
		if (!url.equals(this.imageUrl)) {
			this.imageUrl = url;
			ModDynamicTextures.releaseDynamicTexture(this);
			loadImageAsync();
			setChanged();
		}
	}
	
	private void loadImageAsync() {
		if (isLoading() || imageUrl.isEmpty() ||
				level == null || !level.isClientSide()) {
			return;
		}
		
		isLoading = true;
		
		CompletableFuture.supplyAsync(() -> fetchImage(imageUrl)).thenAcceptAsync(image -> {
			if (image == null) {
				EternalArtifacts.LOGGER.error("Image could not be read from URL: {}", imageUrl);
			} else {
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(image, "PNG", baos);
					byte[] bytes = baos.toByteArray();
					
					try (InputStream pngStream = new ByteArrayInputStream(bytes)) {
						ModDynamicTextures.registerDynamicTexture(this, pngStream);
					}
				} catch (Exception e) {
					EternalArtifacts.LOGGER.error("Failed to load image from URL: {}", imageUrl);
					this.loadFailed = true;
					this.isLoading = false;
				}
			}
		}, ModDynamicTextures::tellMinecraft);
	}
	
	private BufferedImage fetchImage(String url)  {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			connection.setRequestProperty("Accept", "image/avif,image/webp,image/apng,image/*,*/*;q=0.8");
			connection.setRequestProperty("Referer", "https://imgur.com/");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			
			try (InputStream inputStream = connection.getInputStream()) {
				return ImageIO.read(inputStream);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putString("ImageUrl", imageUrl);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		String url = tag.getString("ImageUrl");
		if (!url.isEmpty()) {
			setImageUrl(url);
		}
	}
}
