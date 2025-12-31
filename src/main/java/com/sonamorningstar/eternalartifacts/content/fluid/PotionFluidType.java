package com.sonamorningstar.eternalartifacts.content.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sonamorningstar.eternalartifacts.event.custom.JarDrinkEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

public class PotionFluidType extends BaseFluidType {
	public PotionFluidType(ResourceLocation stillTexture,
						   ResourceLocation flowingTexture,
						   ResourceLocation overlayTexture,
						   Properties properties) {
		super(stillTexture, flowingTexture, overlayTexture, 0xffffffff, new Vector3f(1, 1, 1), properties);
	}
	
	public static void jarDrink(JarDrinkEvent event) {
		FluidStack fluidStack = event.getFluidStack();
		if (fluidStack.getFluid().getFluidType() instanceof PotionFluidType) {
			List<MobEffectInstance> effects = PotionUtils.getAllEffects(fluidStack.getTag());
			if (effects.isEmpty()) return;
			event.setDrinkingAmount(250);
			event.setDefaultUseTime();
			event.setAfterDrink((player, stack) -> {
				for (MobEffectInstance effect : effects) {
					if (effect.getEffect().isInstantenous())
						effect.getEffect().applyInstantenousEffect(player, player, player, effect.getAmplifier(), 1.0);
					else player.addEffect(new MobEffectInstance(effect));
				}
			});
		}
	}
	
	@Override
	public String getDescriptionId(FluidStack stack) {
		return PotionUtils.getPotion(stack.getTag()).getName(Items.POTION.getDescriptionId() + ".effect.");
	}
	
	@Override
	public ItemStack getBucket(FluidStack stack) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public FluidState getStateForPlacement(BlockAndTintGetter getter, BlockPos pos, FluidStack stack) {
		return super.getStateForPlacement(getter, pos, stack);
	}
	
	@Override
	public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
		consumer.accept(new IClientFluidTypeExtensions() {
			@Override
			public ResourceLocation getStillTexture() {
				return stillTexture;
			}
			
			@Override
			public ResourceLocation getFlowingTexture() {
				return flowingTexture;
			}
			
			@Override
			public @Nullable ResourceLocation getOverlayTexture() {
				return overlayTexture;
			}
			
			@Override
			public int getTintColor(FluidStack stack) {
				return 0xFF000000 | PotionUtils.getColor(PotionUtils.getPotion(stack.getTag()));
			}
			
			@Override
			public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
				return IClientFluidTypeExtensions.super.getTintColor(state, getter, pos);
			}
			
			@Override
			public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level,
													int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
				return fluidFogColor;
			}
			
			@Override
			public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick,
										float nearDistance, float farDistance, FogShape shape) {
				RenderSystem.setShaderFogStart(1f);
				RenderSystem.setShaderFogEnd(6f); // distance when the fog starts
			}
		});
	}
}
