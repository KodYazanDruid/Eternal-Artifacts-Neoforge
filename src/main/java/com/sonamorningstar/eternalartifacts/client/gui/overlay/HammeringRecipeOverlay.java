package com.sonamorningstar.eternalartifacts.client.gui.overlay;

import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.HammeringModifier;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;

import java.util.HashSet;
import java.util.Set;

public class HammeringRecipeOverlay extends ModGuiOverlay {
    public static final Set<Block> gatheredBlocks = new HashSet<>();
    
    public HammeringRecipeOverlay() {
        super(4, 18);
    }
    
    @Override
    public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        Player player = mc.player;
        if (shouldReturn(gui.getMinecraft())) return;
        BlockHitResult bhr = RayTraceHelper.retrace(player, ClipContext.Fluid.NONE);
        BlockState state = player.level().getBlockState(bhr.getBlockPos());
        if (bhr.getType() != HitResult.Type.MISS && checkBlock(state) && player.getMainHandItem().is(ModTags.Items.TOOLS_HAMMER)) {
            int x = mc.getWindow().getGuiScaledWidth() / 2;
            int y = mc.getWindow().getGuiScaledHeight() / 2;
            Item bi = state.getBlock().asItem();
            boolean hasItem = bi != Items.AIR;
            Component text = ModConstants.OVERLAY.withSuffixTranslatable("hammering_recipe").append(" ");
            int strWidth = getComponentWidth(text);
            int contentWidth = hasItem ? strWidth + 61 : strWidth + 4;
            setWidth(contentWidth);
            renderBlankBlack(guiGraphics, x, y, contentWidth, 18, 0.5F);
            guiGraphics.drawString(mc.font, text, x + 3, y + 5, 0xFFFFFF);
            if (hasItem) {
                int spriteX = x + 3 + strWidth;
                guiGraphics.renderItem(bi.getDefaultInstance(), spriteX, y + 1);
                GuiDrawer.drawEmptyArrow(guiGraphics, spriteX + 18, y + 2);
                guiGraphics.renderItem(Items.BEEF.getDefaultInstance(), spriteX + 42, y + 1);
            }
        }
    }

    private boolean checkBlock(BlockState state) {
        return gatheredBlocks.contains(state.getBlock()) || HammeringModifier.gatheredTags.stream().anyMatch(state::is);
    }
}
