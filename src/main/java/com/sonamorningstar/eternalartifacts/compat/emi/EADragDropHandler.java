package com.sonamorningstar.eternalartifacts.compat.emi;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.client.gui.screen.BlueprintScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.GenericSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.content.block.entity.AdvancedCrafterBlockEntity;
import com.sonamorningstar.eternalartifacts.network.BlueprintUpdateSlotToServer;
import com.sonamorningstar.eternalartifacts.network.Channel;
import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class EADragDropHandler implements EmiDragDropHandler<Screen> {
    @Override
    public boolean dropStack(Screen screen, EmiIngredient emiIngredient, int mX, int mY) {
        if (screen instanceof BlueprintScreen bs) {
            List<EmiStack> emiStacks = emiIngredient.getEmiStacks();
            ItemStack stack = emiStacks.get(0).getItemStack();
            if (stack.isEmpty()) return false;
            int playerInvSize = bs.getMenu().getPlayer().getInventory().items.size();
            int containerSize = bs.getMenu().getFakeItems().getContainerSize();
            for (int i = playerInvSize; i < playerInvSize + containerSize; i++) {
                FakeSlot fakeSlot = ((FakeSlot) bs.getMenu().getSlot(i));
                Rect2i area = new Rect2i(bs.getGuiLeft() + fakeSlot.x, bs.getGuiTop() + fakeSlot.y, 16, 16);
                if (area.contains(mX, mY)) {
                    fakeSlot.set(stack);
                    Channel.sendToServer(new BlueprintUpdateSlotToServer(bs.getMenu().containerId, i - playerInvSize, stack));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void render(Screen screen, EmiIngredient dragged, GuiGraphics draw, int mouseX, int mouseY, float delta) {
        if (screen instanceof BlueprintScreen bs) {
            List<EmiStack> emiStacks = dragged.getEmiStacks();
            ItemStack stack = emiStacks.get(0).getItemStack();
            if (stack.isEmpty()) return;
            int playerInvSize = bs.getMenu().getPlayer().getInventory().items.size();
            int containerSize = bs.getMenu().getFakeItems().getContainerSize();
            for (int i = playerInvSize; i < playerInvSize + containerSize; i++) {
                FakeSlot fakeSlot = ((FakeSlot) bs.getMenu().getSlot(i));
                Rect2i area = new Rect2i(bs.getGuiLeft() + fakeSlot.x, bs.getGuiTop() + fakeSlot.y, 16, 16);
                draw.fill(area.getX(), area.getY(), area.getX() + area.getWidth(), area.getY() + area.getHeight(), 0x807497EA);
            }
            //draw line
            //VertexConsumer consumer = draw.bufferSource().getBuffer(RenderType.lines());


        }
    }
}
