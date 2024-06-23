package com.sonamorningstar.eternalartifacts.client.renderer;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.model.FluidCombustionDynamoModel;
import com.sonamorningstar.eternalartifacts.client.renderer.blockentity.FluidCombustionRenderer;
import com.sonamorningstar.eternalartifacts.client.renderer.blockentity.JarRenderer;
import com.sonamorningstar.eternalartifacts.content.block.FluidCombustionDynamoBlock;
import com.sonamorningstar.eternalartifacts.content.block.JarBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.FluidCombustionDynamoBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.JarBlockEntity;
import com.sonamorningstar.eternalartifacts.content.entity.client.ModModelLayers;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.function.Supplier;

public class ModItemStackBEWLR extends BlockEntityWithoutLevelRenderer {
    public static final Supplier<ModItemStackBEWLR> INSTANCE = Suppliers.memoize(ModItemStackBEWLR::new);
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
    private final EntityModelSet entityModelSet = Minecraft.getInstance().getEntityModels();
    private ModItemStackBEWLR() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    private final JarBlockEntity jarBlockEntity = new JarBlockEntity(BlockPos.ZERO, ModBlocks.JAR.get().defaultBlockState());
    private final FluidCombustionDynamoBlockEntity fluidCombustionBlockEntity = new FluidCombustionDynamoBlockEntity(BlockPos.ZERO, ModBlocks.FLUID_COMBUSTION_DYNAMO.get().defaultBlockState());

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack ps, MultiBufferSource buff, int light, int overlay) {
        Item item = stack.getItem();
        Minecraft minecraft = Minecraft.getInstance();
        if(item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if(block instanceof JarBlock jarBlock) {
                IFluidHandlerItem fluidHandlerItem = FluidUtil.getFluidHandler(stack).get();
                jarBlockEntity.tank.setFluid(fluidHandlerItem.getFluidInTank(0));
                JarRenderer.renderWhole(jarBlockEntity, ps, buff, JarRenderer.TEXTURE_JAR, light, overlay);
            }else if(block instanceof FluidCombustionDynamoBlock dynamo) {
                blockEntityRenderDispatcher.renderItem(fluidCombustionBlockEntity, ps, buff, light, overlay);
            }
        }

    }
}
