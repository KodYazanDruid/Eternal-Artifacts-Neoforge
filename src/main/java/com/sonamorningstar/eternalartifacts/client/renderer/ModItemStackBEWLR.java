package com.sonamorningstar.eternalartifacts.client.renderer;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.renderer.item.SpellTomeRenderer;
import com.sonamorningstar.eternalartifacts.content.block.*;
import com.sonamorningstar.eternalartifacts.content.block.entity.*;
import com.sonamorningstar.eternalartifacts.content.item.base.AnimatedSpellTomeItem;
import com.sonamorningstar.eternalartifacts.content.item.block.JarBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
    static Minecraft minecraft = Minecraft.getInstance();
    private static final BlockEntityRenderDispatcher beRenderer = minecraft.getBlockEntityRenderDispatcher();
    private static final EntityModelSet entityModelSet = minecraft.getEntityModels();
    EntityRendererProvider.Context entityrendererprovider$context = new EntityRendererProvider.Context(
            minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getBlockRenderer(), minecraft.gameRenderer.itemInHandRenderer, minecraft.getResourceManager(), entityModelSet, minecraft.font
    );

    private ModItemStackBEWLR() {
        super(beRenderer, entityModelSet);
    }

    private final JarBlockEntity jar = new JarBlockEntity(BlockPos.ZERO, ModBlocks.JAR.get().defaultBlockState());
    private final FluidCombustionDynamoBlockEntity fcDynamo = new FluidCombustionDynamoBlockEntity(BlockPos.ZERO, ModBlocks.FLUID_COMBUSTION_DYNAMO.get().defaultBlockState());
    private final NousTankBlockEntity nousTank = new NousTankBlockEntity(BlockPos.ZERO, ModBlocks.NOUS_TANK.get().defaultBlockState());
    private final OilRefineryBlockEntity refinery = new OilRefineryBlockEntity(BlockPos.ZERO, ModMachines.OIL_REFINERY.getBlock().defaultBlockState());
    private final EnergyDockBlockEntity energyDock = new EnergyDockBlockEntity(BlockPos.ZERO, ModBlocks.ENERGY_DOCK.get().defaultBlockState());
    private final TesseractBlockEntity tesseract = new TesseractBlockEntity(BlockPos.ZERO, ModBlocks.TESSERACT.get().defaultBlockState());

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack ps, MultiBufferSource buff, int light, int overlay) {
        Item item = stack.getItem();
        if(item instanceof BlockItem blockItem) {
            IFluidHandlerItem fluidHandlerItem = FluidUtil.getFluidHandler(stack).orElse(null);
            Block block = blockItem.getBlock();
            if(block instanceof JarBlock) {
                if(fluidHandlerItem != null) jar.tank.setFluid(fluidHandlerItem.getFluidInTank(0), 0);
                CompoundTag tag = stack.getTag();
                boolean isOpen = false;
                if (tag != null) isOpen = tag.getBoolean(JarBlockItem.KEY_OPEN);
                jar.isOpen = isOpen;
                beRenderer.renderItem(jar, ps, buff, light, overlay);
            }else if(block instanceof FluidCombustionDynamoBlock) {
                beRenderer.renderItem(fcDynamo, ps, buff, light, overlay);
            }else if(block instanceof NousTankBlock) {
                if(fluidHandlerItem != null) nousTank.tank.setFluid(fluidHandlerItem.getFluidInTank(0), 0);
                beRenderer.renderItem(nousTank, ps, buff, light, overlay);
            }else if(block instanceof OilRefineryBlock<? extends OilRefineryBlockEntity>) {
                if (fluidHandlerItem != null) {
                    refinery.tank.setFluid(fluidHandlerItem.getFluidInTank(0), 0);
                    refinery.tank.setFluid(fluidHandlerItem.getFluidInTank(1), 1);
                    refinery.tank.setFluid(fluidHandlerItem.getFluidInTank(2), 2);
                }
                beRenderer.renderItem(refinery, ps, buff, light, overlay);
            }else if (block instanceof EnergyDockBlock) {
                beRenderer.renderItem(energyDock, ps, buff, light, overlay);
            }else if (block instanceof TesseractBlock) {
                beRenderer.renderItem(tesseract, ps, buff, light, overlay);
            }
        } else {
            if (item instanceof AnimatedSpellTomeItem<?> tome) {
                SpellTomeRenderer renderer = new SpellTomeRenderer(entityrendererprovider$context);
                renderer.render(stack, minecraft.getPartialTick(), ps, buff, light, overlay);
            }
        }
    }
}
