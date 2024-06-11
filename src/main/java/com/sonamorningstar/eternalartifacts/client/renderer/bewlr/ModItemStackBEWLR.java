package com.sonamorningstar.eternalartifacts.client.renderer.bewlr;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.content.block.JarBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.JarBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.function.Supplier;

public class ModItemStackBEWLR extends BlockEntityWithoutLevelRenderer {
    public static final Supplier<ModItemStackBEWLR> INSTANCE = Suppliers.memoize(ModItemStackBEWLR::new);
    private ModItemStackBEWLR() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }

    private final JarBlockEntity jarBlockEntity = new JarBlockEntity(BlockPos.ZERO, ModBlocks.JAR.get().defaultBlockState());

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack ps, MultiBufferSource buff, int light, int overlay) {
        Item item = stack.getItem();
        if(item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            Minecraft minecraft = Minecraft.getInstance();
            if(block instanceof JarBlock jarBlock) {
                IFluidHandlerItem fluidHandlerItem = FluidUtil.getFluidHandler(stack).get();
                jarBlockEntity.tank.setFluid(fluidHandlerItem.getFluidInTank(0));
                minecraft.getBlockEntityRenderDispatcher().renderItem(jarBlockEntity, ps, buff, light, overlay);
            }
        }

    }
}
