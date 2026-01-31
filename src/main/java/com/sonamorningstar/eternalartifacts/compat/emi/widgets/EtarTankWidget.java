package com.sonamorningstar.eternalartifacts.compat.emi.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.TankWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

/**
 * Custom EMI TankWidget that uses the mod's tank texture instead of EMI's default slot texture.
 * Supports both large tanks (18x56) and small tanks (18x18).
 */
public class EtarTankWidget extends TankWidget {
	private static final ResourceLocation BARS_TEXTURE = new ResourceLocation(MODID, "textures/gui/bars.png");
	
	private static final int LARGE_TANK_U = 30;
	private static final int LARGE_TANK_V = 0;
	private static final int LARGE_TANK_WIDTH = 18;
	private static final int LARGE_TANK_HEIGHT = 56;
	
	private static final int SMALL_TANK_U = 66;
	private static final int SMALL_TANK_V = 37;
	private static final int SMALL_TANK_WIDTH = 18;
	private static final int SMALL_TANK_HEIGHT = 18;
	
	private final int width;
	private final int height;
	private final long capacity;
	private final boolean useModTexture;
	
	/**
	 * Creates a tank widget with specified dimensions (compatibility constructor)
	 */
	public EtarTankWidget(EmiIngredient stack, int x, int y, int width, int height, long capacity) {
		super(stack, x, y, width, height, capacity);
		this.width = width;
		this.height = height;
		this.capacity = capacity;
		this.useModTexture = true;
	}
	
	/**
	 * Creates a large tank widget (18x56) with mod texture
	 */
	public static EtarTankWidget large(EmiIngredient stack, int x, int y, long capacity) {
		return new EtarTankWidget(stack, x, y, LARGE_TANK_WIDTH, LARGE_TANK_HEIGHT, capacity);
	}
	
	/**
	 * Creates a small tank widget (18x18) with mod texture
	 */
	public static EtarTankWidget small(EmiIngredient stack, int x, int y, long capacity) {
		return new EtarTankWidget(stack, x, y, SMALL_TANK_WIDTH, SMALL_TANK_HEIGHT, capacity);
	}
	
	@Override
	public Bounds getBounds() {
		return new Bounds(x, y, width, height);
	}
	
	@Override
	public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
		if (!useModTexture) {
			super.render(gui, mouseX, mouseY, delta);
			return;
		}
		
		EmiIngredient ingredient = getStack();
		if (!ingredient.isEmpty()) {
			for (EmiStack emiStack : ingredient.getEmiStacks()) {
				Object key = emiStack.getKey();
				if (key instanceof net.minecraft.world.level.material.Fluid fluid) {
					FluidStack fluidStack = new FluidStack(fluid, (int) emiStack.getAmount());
					renderFluid(gui, x, y, width, height, fluidStack);
					break;
				}
			}
		}
		
		renderTankFrame(gui, x, y);
		
		Bounds bounds = getBounds();
		if (mouseX >= bounds.x() && mouseX < bounds.x() + bounds.width() &&
			mouseY >= bounds.y() && mouseY < bounds.y() + bounds.height()) {
			EmiRender.renderTagIcon(ingredient, gui, x + 1, y + 1);
		}
	}
	
	private void renderTankFrame(GuiGraphics gui, int x, int y) {
		if (height <= SMALL_TANK_HEIGHT) {
			gui.blit(BARS_TEXTURE, x, y, SMALL_TANK_U, SMALL_TANK_V, SMALL_TANK_WIDTH, SMALL_TANK_HEIGHT);
		} else {
			gui.blit(BARS_TEXTURE, x, y, LARGE_TANK_U, LARGE_TANK_V, LARGE_TANK_WIDTH, LARGE_TANK_HEIGHT);
		}
	}
	
	private void renderFluid(GuiGraphics gui, int x, int y, int width, int height, FluidStack fluidStack) {
		if (fluidStack.isEmpty()) return;
		
		IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
		ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(fluidStack);
		if (stillTexture == null) return;
		
		TextureAtlasSprite sprite = Minecraft.getInstance()
			.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
			.apply(stillTexture);
		
		int tintColor = fluidTypeExtensions.getTintColor(fluidStack);
		float alpha = ((tintColor >> 24) & 0xFF) / 255f;
		float red = ((tintColor >> 16) & 0xFF) / 255f;
		float green = ((tintColor >> 8) & 0xFF) / 255f;
		float blue = ((tintColor) & 0xFF) / 255f;
		
		long amount = fluidStack.getAmount();
		int fluidHeight = height - 6; // Account for tank frame padding
		int fillHeight = capacity > 0 ? (int) (amount * fluidHeight / capacity) : fluidHeight;
		fillHeight = Math.min(fillHeight, fluidHeight);
		
		if (fillHeight <= 0) return;
		
		int fluidWidth = width - 6; // Account for tank frame padding
		int fluidX = x + 3;
		int fluidY = y + 3 + (fluidHeight - fillHeight);
		
		RenderSystem.enableBlend();
		gui.setColor(red, green, blue, alpha);
		gui.blitTiledSprite(
			sprite,
			fluidX,
			fluidY,
			0,
			fluidWidth,
			fillHeight,
			0, 0,
			16, 16,
			16, 16
		);
		gui.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.disableBlend();
	}
}
