package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.TooltipHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.*;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public abstract class AbstractMachineScreen<T extends AbstractMachineMenu> extends AbstractModContainerScreen<T> {
    protected static final ResourceLocation bars = new ResourceLocation(MODID, "textures/gui/bars.png");
    protected static final ResourceLocation buttons = new ResourceLocation(MODID, "textures/gui/buttons.png");
    private final Map<String, Integer> energyLoc = new HashMap<>();
    private final Map<Integer, Map<String, Integer>> fluidLocs = new HashMap<>();
    protected static final int labelColor = 4210752;

    public AbstractMachineScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        renderEffects = false;
    }
    
    protected void renderExtra(GuiGraphics gui, int mx, int my, float partialTick) {}

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        inventoryLabelX = 46;
        super.render(gui, mx, my, partialTick);
        if (menu.getBeEnergy() != null) renderEnergyTooltip(gui, mx, my);
        if (menu.getBeTank() != null) renderFluidTooltip(gui, mx, my);
        renderExtra(gui, mx, my, partialTick);
        renderTooltip(gui, mx, my);
    }
    
    protected void renderEnergyTooltip(GuiGraphics gui, int mx, int my) {
        renderEnergyTooltip(gui, mx, my, true);
    }
    
    protected void renderEnergyTooltip(GuiGraphics gui, int mx, int my, boolean renderEPT) {
        if(!energyLoc.isEmpty() && isCursorInBounds(energyLoc.get("x"), energyLoc.get("y"), energyLoc.get("width"), energyLoc.get("height"), mx, my)) {
            Machine<?> machine = (Machine<?>) menu.getBlockEntity();
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(Component.translatable(ModConstants.GUI.withSuffix("energy")).append(": ")
                .append(String.valueOf(menu.getBeEnergy().getEnergyStored()))
                .append("/").append(String.valueOf(menu.getBeEnergy().getMaxEnergyStored())));
            if (renderEPT) {
                String prefix = machine.isGenerator() ? "produce": "consume";
                tooltips.add(Component.translatable(ModConstants.GUI.withSuffix(prefix+"_energy_per_tick"), machine.getEnergyPerTick()));
            }
            gui.renderTooltip(font, tooltips, Optional.empty(), mx, my);
        }
    }
    protected void renderFluidTooltip(GuiGraphics gui, int mx, int my) {
        fluidLocs.forEach( (tank, fluidLoc) -> {
            if (!fluidLoc.isEmpty() && isCursorInBounds(fluidLoc.get("x"), fluidLoc.get("y"), fluidLoc.get("width"), fluidLoc.get("height"), mx, my)) {
                var fs = menu.getBeTank().getFluidInTank(tank);
                var tooltips = TooltipHelper.getTooltipFromContainerFluid(fs, minecraft.options.advancedItemTooltips);
                tooltips.add(Component.literal(String.valueOf(fs.getAmount())).append(" / ").append(String.valueOf(menu.getBeTank().getTankCapacity(tank))));
                gui.renderTooltip(font, tooltips, Optional.empty(), mx, my);
            }
        });
    }
    protected void renderProgressTooltip(GuiGraphics gui, int x, int y, int xLen, int yLen, int mx, int my, String key) {
        if(isCursorInBounds(x, y, xLen, yLen, mx, my)) {
            gui.renderTooltip(font,
                    Component.translatable(ModConstants.GUI.withSuffix(key)).append(": ")
                            .append(String.valueOf(menu.data.get(0))).append("/").append(String.valueOf(menu.data.get(1))),
                    mx, my);
        }
    }

    protected void renderDefaultEnergyAndFluidBar(GuiGraphics gui) {
        renderDefaultEnergyBar(gui);
        renderDefaultFluidBar(gui);
    }
    protected void renderDefaultEnergyBar(GuiGraphics gui) {
        renderEnergyBar(gui, leftPos + 5, topPos + 20);
    }

    protected void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(bars, x, y, 0, 0, 18, 56);
        guiGraphics.blit(bars, x + 3, y + 53 - menu.getEnergyProgress(), 18, 53 - menu.getEnergyProgress(), 12, menu.getEnergyProgress());
        energyLoc.put("x", x);
        energyLoc.put("y", y);
        energyLoc.put("width", 18);
        energyLoc.put("height", 56);
    }

    protected void renderDefaultFluidBar(GuiGraphics gui) { renderDefaultFluidBar(gui, 0); }
    protected void renderDefaultFluidBar(GuiGraphics gui, int tankSlot) { renderFluidBar(gui, leftPos + 24, topPos + 20, tankSlot); }
    protected void renderFluidBar(GuiGraphics guiGraphics, int x, int y) { renderFluidBar(guiGraphics,  x,  y, 0); }
    protected void renderFluidBar(GuiGraphics guiGraphics, int x, int y, int tankSlot) {
        Map<String, Integer> fluidLoc = new HashMap<>();
        fluidLoc.put("x", x);
        fluidLoc.put("y", y);
        fluidLoc.put("width", 18);
        fluidLoc.put("height", 56);
        fluidLocs.put(tankSlot, fluidLoc);

        IFluidHandler tank = menu.getBlockEntity().getLevel().getCapability(Capabilities.FluidHandler.BLOCK, menu.getBlockEntity().getBlockPos(), menu.getBlockEntity().getBlockState(), menu.getBlockEntity(), null);
        FluidStack stack = FluidStack.EMPTY;
        if(tank != null) stack = tank.getFluidInTank(tankSlot);
        GuiDrawer.drawFluidWithTank(guiGraphics, x, y, stack, menu.getFluidProgress(tankSlot, 50));
    }

    protected void renderBurn(GuiGraphics guiGraphics, int x, int y, int mx, int my) {
        guiGraphics.blit(bars, x + 1, y + 1, 48, 10, 13, 13);
        if(menu.isWorking()) guiGraphics.blit(bars, x, y + 14 - menu.getScaledProgress(14), 48,  37 - menu.getScaledProgress(14), 14, menu.getScaledProgress(14));
        renderProgressTooltip(guiGraphics, x + 1, y + 1, 13, 13, mx, my, "burn_time");
    }

    protected void renderProgressArrow(GuiGraphics guiGraphics, int x, int y, int mx, int my) {
        guiGraphics.blit(bars, x, y, 0, 56, 22, 15);
        if(menu.isWorking()) guiGraphics.blit(bars, x, y, 22, 56, menu.getScaledProgress(22), 15);
        renderProgressTooltip(guiGraphics, x, y, 22, 15, mx, my, "progress");
    }

    protected void renderLArrow(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(bars, x, y, 53, 0, 10, 9);
    }

}
