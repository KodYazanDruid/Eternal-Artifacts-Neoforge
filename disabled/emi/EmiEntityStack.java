package com.sonamorningstar.eternalartifacts.compat.emi;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class EmiEntityStack extends EmiStack {
    private final @Nullable
    Entity entity;
    private final EntityRenderContext ctx;

    public EmiEntityStack(@Nullable Entity entity) {
        this(entity,8.0);
    }

    protected EmiEntityStack(@Nullable Entity entity, double scale) {
        this.entity = entity;
        /*if (entity != null) {
            boolean hasTransform = ClientResourceData.MOB_ROTATIONS.containsKey(entity.getType());
            Vector3f transform = ClientResourceData.MOB_ROTATIONS.getOrDefault(entity.getType(),new Vector3f(0,0,0)).mul(0.017453292F);
            ctx = new EntityRenderContext(scale,hasTransform,transform);
        } else {
            ctx = new EntityRenderContext(scale,false,new Vector3f(0,0,0));
        }*/
        ctx = new EntityRenderContext(scale,false,new Vector3f(0,0,0));

    }

    public static EmiEntityStack of(@Nullable Entity entity) {
        return new EmiEntityStack(entity);
    }

    public static EmiEntityStack ofScaled(@Nullable Entity entity, double scale) {
        return new EmiEntityStack(entity, scale);
    }

    @Override
    public EmiStack copy() {
        EmiEntityStack stack = new EmiEntityStack(entity);
        stack.setRemainder(getRemainder().copy());
        stack.comparison = comparison;
        return stack;
    }

    @Override
    public boolean isEmpty() {
        return entity == null;
    }

    @Override
    public void render(GuiGraphics matrices, int x, int y, float delta, int flags) {
        if (entity != null) {
            if (entity instanceof LivingEntity living)
                renderEntity(matrices.pose() ,x + 8, (int) (y + 8 + ctx.size), ctx, living);
            else
                renderEntity(matrices.pose(),(int) (x + (2 * ctx.size / 2)), (int) (y + (2 * ctx.size)), ctx, entity);
        }
    }

    @Override
    public CompoundTag getNbt() {
        throw new UnsupportedOperationException("EntityEmiStack is not intended for NBT handling");
    }

    @Override
    public Object getKey() {
        return entity;
    }

    @Override
    public ResourceLocation getId() {
        if (entity == null) throw new RuntimeException("Entity is null");
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
    }

    @Override
    public List<Component> getTooltipText() {
        return List.of(getName());
    }

    @Override
    public List<ClientTooltipComponent> getTooltip() {
        List<ClientTooltipComponent> list = new ArrayList<>();
        if (entity != null) {
            /*list.addAll(getTooltipText().stream().map().map(ClientTooltipComponent::toString).toList());
            String mod = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace();
            list.add(ClientTooltipComponent.of(EmiPort.ordered(EmiPort.literal(mod, Formatting.BLUE, Formatting.ITALIC))));
            if (!getRemainder().isEmpty()) {
                list.add(new RemainderTooltipComponent(this));
            }*/
            String mod = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace();
            String path = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath();
            //list.add(Component.literal(mod + ": "+ path));
        }
        return list;
    }

    @Override
    public Component getName() {
        return entity != null ? entity.getName() : Component.literal("yet another missingno");
    }

    public static void renderEntity(PoseStack matrices, int x, int y, EntityRenderContext ctx, LivingEntity entity) {
        Minecraft minecraft = Minecraft.getInstance();

        double width = minecraft.getWindow().getWidth();
        double height = minecraft.getWindow().getHeight();
        float mouseX = (float)(minecraft.mouseHandler.xpos() * width / (double)minecraft.getWindow().getWidth());
        float mouseY = (float)(minecraft.mouseHandler.ypos() * height / (double)minecraft.getWindow().getHeight());
        double posX = mouseX - width/2 + 63;
        double posY = mouseY - height/2;
        float f = (float)Math.atan(-posX / 40.0F);
        float g = (float)Math.atan(-posY / 40.0F);

        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.mulPoseMatrix(matrices.last().pose());
        poseStack.translate(x, y, 1050.0);
        poseStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack poseStack2 = new PoseStack();
        poseStack2.translate(0.0, 0.0, 1000.0);
        poseStack2.scale((float) ctx.size, (float) ctx.size, (float) ctx.size);
        Quaternionf quaternion = new Quaternionf().rotateZ(3.1415927F);
        Quaternionf quaternion2 = new Quaternionf().rotateX(g * 20.0F * 0.017453292F * Mth.cos(ctx.transform.z) - f * 20.0F * 0.017453292F * Mth.sin(ctx.transform.z));
        if (ctx.hasTransform){
            Quaternionf quaternion3 = new Quaternionf().rotateXYZ(ctx.transform.x,ctx.transform.y,ctx.transform.z);
            quaternion.mul(quaternion3);
        }

        quaternion.mul(quaternion2);
        poseStack2.mulPose(quaternion);
        float h = entity.yBodyRot;
        float i = entity.xRotO;
        float j = entity.yRotO;
        float k = entity.yHeadRot;
        float l = entity.yHeadRotO;

        entity.yBodyRot = 180.0F + (f * 20.0F * Mth.cos(ctx.transform.z) + (g * 20.0F * Mth.sin(ctx.transform.z)));
        entity.setYHeadRot(180.0F + (f * 40.0F * Mth.cos(ctx.transform.z) + (g * 40.0F * Mth.sin(ctx.transform.z))));
        entity.setXRot((-g * 20.0F * Mth.cos(ctx.transform.z)) + (- f * 20.0F * Mth.sin(ctx.transform.z)) );
        entity.setYHeadRot(i);
        entity.yHeadRotO = entity.yHeadRot;
        //Lighting.setupFor3DItems();
        EntityRenderDispatcher entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, poseStack2, bufferSource, 15728880));
        bufferSource.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        entity.yBodyRot = h;
        entity.setYRot(i);
        entity.setXRot(j);
        entity.yHeadRotO = k;
        entity.yHeadRot = l;
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupForEntityInInventory();
    }

    public static void renderEntity(PoseStack poseStack,int x, int y, EntityRenderContext ctx, Entity entity) {
        Minecraft minecraft = Minecraft.getInstance();
        MouseHandler mouseHandler = minecraft.mouseHandler;
        float w = 1920;
        float h = 1080;
        Screen screen = minecraft.screen;
        if (screen != null) {
            w = screen.width;
            h = screen.height;
        }
        float mouseX = (float) ((w + 51) - mouseHandler.xpos());
        float mouseY = (float) ((h + 75 - 50) - mouseHandler.ypos());
        float f = (float)Math.atan(mouseX / 40.0F);
        float g = (float)Math.atan(mouseY / 40.0F);
        PoseStack poseStack2 = RenderSystem.getModelViewStack();
        poseStack2.pushPose();
        poseStack2.mulPoseMatrix(poseStack.last().pose());
        poseStack2.translate(x, y, 1050.0);
        poseStack2.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack poseStack3 = new PoseStack();
        poseStack3.translate(0.0, 0.0, 1000.0);
        poseStack3.scale((float) ctx.size, (float) ctx.size, (float) ctx.size);
        Quaternionf quaternion = new Quaternionf().rotateZ(3.1415927F);
        Quaternionf quaternion2 = new Quaternionf().rotateX(g * 20.0F * 0.017453292F * Mth.cos(ctx.transform.z) - f * 20.0F * 0.017453292F * Mth.sin(ctx.transform.z));
        if (ctx.hasTransform){
            Quaternionf quaternion3 = new Quaternionf().rotateXYZ(ctx.transform.x,ctx.transform.y,ctx.transform.z);
            quaternion.mul(quaternion3);
        }

        quaternion.mul(quaternion2);
        poseStack3.mulPose(quaternion);
        float i = entity.getYRot();
        float j = entity.getXRot();
        entity.setYRot(180.0F + (f * 40.0F * Mth.cos(ctx.transform.z) + (g * 40.0F * Mth.sin(ctx.transform.z))));
        entity.setXRot((-g * 20.0F * Mth.cos(ctx.transform.z)) + (- f * 20.0F * Mth.sin(ctx.transform.z)) );
        //Lighting.setupFor3DItems();
        EntityRenderDispatcher entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, poseStack3, buffer, 15728880));
        buffer.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        entity.setYRot(i);
        entity.setXRot(j);
        poseStack2.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupForEntityInInventory();
    }

    private record EntityRenderContext(double size, boolean hasTransform, Vector3f transform){
        static EntityRenderContext EMPTY = new EntityRenderContext(8.0,false,new Vector3f(0,0,0));
    }
}
