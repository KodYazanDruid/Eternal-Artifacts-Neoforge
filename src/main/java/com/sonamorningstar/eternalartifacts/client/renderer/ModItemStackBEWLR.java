package com.sonamorningstar.eternalartifacts.client.renderer;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.content.block.FluidCombustionDynamoBlock;
import com.sonamorningstar.eternalartifacts.content.block.JarBlock;
import com.sonamorningstar.eternalartifacts.content.block.NousTankBlock;
import com.sonamorningstar.eternalartifacts.content.block.OilRefineryBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.FluidCombustionDynamoBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.JarBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.NousTankBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.OilRefineryBlockEntity;
import com.sonamorningstar.eternalartifacts.content.item.block.JarBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
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
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
    private final EntityModelSet entityModelSet = Minecraft.getInstance().getEntityModels();
    private ModItemStackBEWLR() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    private final JarBlockEntity jarBlockEntity = new JarBlockEntity(BlockPos.ZERO, ModBlocks.JAR.get().defaultBlockState());
    private final FluidCombustionDynamoBlockEntity fluidCombustionBlockEntity = new FluidCombustionDynamoBlockEntity(BlockPos.ZERO, ModBlocks.FLUID_COMBUSTION_DYNAMO.get().defaultBlockState());
    private final NousTankBlockEntity nousTankBlockEntity = new NousTankBlockEntity(BlockPos.ZERO, ModBlocks.NOUS_TANK.get().defaultBlockState());
    private final OilRefineryBlockEntity oilRefineryBlockEntity = new OilRefineryBlockEntity(BlockPos.ZERO, ModMachines.OIL_REFINERY.getBlock().defaultBlockState());

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack ps, MultiBufferSource buff, int light, int overlay) {
        Item item = stack.getItem();
        Minecraft minecraft = Minecraft.getInstance();
        if(item instanceof BlockItem blockItem) {
            IFluidHandlerItem fluidHandlerItem = FluidUtil.getFluidHandler(stack).orElse(null);
            Block block = blockItem.getBlock();
            if(block instanceof JarBlock) {
                if(fluidHandlerItem != null) jarBlockEntity.tank.setFluid(fluidHandlerItem.getFluidInTank(0), 0);
                CompoundTag tag = stack.getTag();
                boolean isOpen = false;
                if (tag != null) isOpen = tag.getBoolean(JarBlockItem.KEY_OPEN);
                jarBlockEntity.isOpen = isOpen;
                blockEntityRenderDispatcher.renderItem(jarBlockEntity, ps, buff, light, overlay);
            }else if(block instanceof FluidCombustionDynamoBlock) {
                blockEntityRenderDispatcher.renderItem(fluidCombustionBlockEntity, ps, buff, light, overlay);
            }else if(block instanceof NousTankBlock) {
                if(fluidHandlerItem != null) nousTankBlockEntity.tank.setFluid(fluidHandlerItem.getFluidInTank(0), 0);
                blockEntityRenderDispatcher.renderItem(nousTankBlockEntity, ps, buff, light, overlay);
            }else if(block instanceof OilRefineryBlock<? extends OilRefineryBlockEntity>) {
                if(fluidHandlerItem != null) {
                    /*oilRefineryBlockEntity.tanks.get(0).setFluid(fluidHandlerItem.getFluidInTank(0));
                    oilRefineryBlockEntity.tanks.get(1).setFluid(fluidHandlerItem.getFluidInTank(1));
                    oilRefineryBlockEntity.tanks.get(2).setFluid(fluidHandlerItem.getFluidInTank(2));*/
                }
                blockEntityRenderDispatcher.renderItem(oilRefineryBlockEntity, ps, buff, light, overlay);
            }
        }

    }
}
