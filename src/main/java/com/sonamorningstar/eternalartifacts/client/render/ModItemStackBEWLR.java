package com.sonamorningstar.eternalartifacts.client.render;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.render.blockentity.ModSkullBlockRenderer;
import com.sonamorningstar.eternalartifacts.client.render.item.SpellTomeRenderer;
import com.sonamorningstar.eternalartifacts.client.resources.model.TwoLayerSkullModel;
import com.sonamorningstar.eternalartifacts.content.block.*;
import com.sonamorningstar.eternalartifacts.content.block.entity.*;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.content.item.base.AnimatedSpellTomeItem;
import com.sonamorningstar.eternalartifacts.content.item.block.JarBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModItemStackBEWLR extends BlockEntityWithoutLevelRenderer {
    public static final Supplier<ModItemStackBEWLR> INSTANCE = Suppliers.memoize(ModItemStackBEWLR::new);
    static Minecraft minecraft = Minecraft.getInstance();
    private static final BlockEntityRenderDispatcher beRenderer = minecraft.getBlockEntityRenderDispatcher();
    private static final EntityModelSet entityModelSet = minecraft.getEntityModels();
    EntityRendererProvider.Context entityrendererprovider$context = new EntityRendererProvider.Context(
            minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getBlockRenderer(), minecraft.gameRenderer.itemInHandRenderer, minecraft.getResourceManager(), entityModelSet, minecraft.font
    );
    
    private Map<SkullBlock.Type, SkullModelBase> skullModels = SkullBlockRenderer.createSkullRenderers(entityModelSet);

    private ModItemStackBEWLR() {
        super(beRenderer, entityModelSet);
    }

    private final JarBlockEntity jar = new JarBlockEntity(BlockPos.ZERO, ModBlocks.JAR.get().defaultBlockState());
    //private final FluidCombustionDynamo fcDynamo = new FluidCombustionDynamo(BlockPos.ZERO, ModBlocks.FLUID_COMBUSTION_DYNAMO.get().defaultBlockState());
    private final Map<DynamoBlock<?>, AbstractDynamo<?>> dynamoMap = new HashMap<>();
    private final NousTank nousTank = new NousTank(BlockPos.ZERO, ModBlocks.NOUS_TANK.get().defaultBlockState());
    private final OilRefinery refinery = new OilRefinery(BlockPos.ZERO, ModMachines.OIL_REFINERY.getBlock().defaultBlockState());
    private final EnergyDockBlockEntity energyDock = new EnergyDockBlockEntity(BlockPos.ZERO, ModBlocks.ENERGY_DOCK.get().defaultBlockState());
    private final Tesseract tesseract = new Tesseract(BlockPos.ZERO, ModBlocks.TESSERACT.get().defaultBlockState());
    
    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        skullModels = SkullBlockRenderer.createSkullRenderers(entityModelSet);
    }
    
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
            } else if(block instanceof DynamoBlock<?> dynamo) {
                beRenderer.renderItem(dynamoMap.computeIfAbsent(dynamo, d -> (AbstractDynamo<?>) d.newBlockEntity(BlockPos.ZERO, d.defaultBlockState())), ps, buff, light, overlay);
            }else if(block instanceof NousTankBlock) {
                if(fluidHandlerItem != null) nousTank.tank.setFluid(fluidHandlerItem.getFluidInTank(0), 0);
                beRenderer.renderItem(nousTank, ps, buff, light, overlay);
            }else if(block instanceof OilRefineryBlock<? extends OilRefinery>) {
                stack.getAllEnchantments().forEach(refinery::enchant);
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
            }else if (block instanceof ModSkullBlock msb) {
                SkullModelBase skullModel = this.skullModels.get(msb.getType());
                if (skullModel instanceof TwoLayerSkullModel tlsm) {
                    ModSkullBlockRenderer.renderModSkull(null, 180, 0, ps, buff, light, tlsm);
                } else if (skullModel != null) {
                    SkullBlockRenderer.renderSkull(null, 180, 0, ps, buff, light, skullModel, SkullBlockRenderer.getRenderType(msb.getType(), null));
                }
            }
        } else {
            if (item instanceof AnimatedSpellTomeItem<?> tome) {
                SpellTomeRenderer renderer = new SpellTomeRenderer(entityrendererprovider$context);
                renderer.render(stack, minecraft.getPartialTick(), ps, buff, light, overlay);
            }
        }
    }
}
