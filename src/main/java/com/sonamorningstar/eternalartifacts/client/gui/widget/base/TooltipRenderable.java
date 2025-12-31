package com.sonamorningstar.eternalartifacts.client.gui.widget.base;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Interface for widgets that can render tooltips at a specific z-index.
 * This allows for proper tooltip layering in panel hierarchies.
 */
public interface TooltipRenderable {
    /**
     * Renders the tooltip for this widget at the specified z-index.
     *
     * @param gui The graphics context
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param tooltipZ The z-index at which to render the tooltip
     */
    void renderTooltip(GuiGraphics gui, int mouseX, int mouseY, int tooltipZ);
}

