package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.content.item.base.EnergyRendererItem;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigurationDriveItem extends EnergyRendererItem {

    public ConfigurationDriveItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Player player = ctx.getPlayer();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ModBlockEntity mbe && player != null) {
            MachineConfiguration configs = mbe.getConfiguration();
            if (configs == null) return InteractionResult.PASS;
            IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energy == null) return InteractionResult.PASS;
            if (player.isShiftKeyDown()) {
                int extracted = energy.extractEnergy(250, true);
                if (extracted == 250) {
                    energy.extractEnergy(250, false);
                    saveConfiguration(stack, mbe);
                    player.displayClientMessage(
                            ModConstants.OVERLAY.withSuffixTranslatable("configuration_device_saved")
                                    .withStyle(ChatFormatting.YELLOW),
                            true
                    );
                }
            } else {
                int extracted = energy.extractEnergy(250, true);
                if (extracted == 250) {
                    energy.extractEnergy(250, false);
                    mbe.loadConfiguration(stack);
                    player.displayClientMessage(
                            ModConstants.OVERLAY.withSuffixTranslatable("configuration_device_loaded")
                                    .withStyle(ChatFormatting.YELLOW),
                            true
                    );
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult ray = RayTraceHelper.retrace(player);
        if (player.isShiftKeyDown() && ray.getType() == HitResult.Type.MISS) {
            stack.getOrCreateTag().remove("SidedTransferConfigs");
            player.displayClientMessage(
                    ModConstants.OVERLAY.withSuffixTranslatable("configuration_device_cleared")
                    .withStyle(ChatFormatting.YELLOW),
                    true
            );
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        tooltipComponents.add(CommonComponents.EMPTY);
        if (stack.hasTag()) {
            //CompoundTag configTag = stack.getTag().getCompound(ModBlockEntity.CONFIG_TAG_KEY);
            /*CompoundTag stackTag = stack.getTag();
            CompoundTag nbt = stackTag.getCompound("SidedTransferConfigs");
            boolean containsSide = nbt.contains("SideConfigs");
            boolean containsAuto = nbt.contains("AutoConfigs");
            boolean containsRedstone = nbt.contains("RedstoneConfigs");
            if (containsSide || containsAuto || containsRedstone) {
                tooltipComponents.add(ModConstants.TOOLTIP.withSuffixTranslatable("configuration_device_tooltip").append(":"));
            }
            if (containsSide) {
                ListTag sideConfigs = nbt.getList("SideConfigs", 10);
                sideConfigs.forEach(tag -> {
                    CompoundTag entry = (CompoundTag) tag;
                    int index = entry.getInt("Index");
                    String direction = "";
                    switch (index) {
                        case 0 -> direction = "up";
                        case 1 -> direction = "left";
                        case 2 -> direction = "front";
                        case 3 -> direction = "right";
                        case 4 -> direction = "down";
                        case 5 -> direction = "back";
                    }
                    int color = ChatFormatting.WHITE.getColor();
                    switch (SidedTransferMachine.TransferType.valueOf(entry.getString("Type"))) {
                        case NONE -> color = ChatFormatting.RED.getColor();
                        case PULL -> color = ChatFormatting.BLUE.getColor();
                        case PUSH -> color = ChatFormatting.GOLD.getColor();
                        case DEFAULT -> color = ChatFormatting.GREEN.getColor();
                    }
                    Component side = Component.literal(" ")
                            .append(ModConstants.GUI.withSuffixTranslatable(direction))
                            .append(": ")
                            .append(ModConstants.GUI.withSuffixTranslatable(entry.getString("Type").toLowerCase()))
                            .withColor(color);
                    tooltipComponents.add(side);
                });
            }
            if (containsAuto) {
                ListTag autoConfigs = nbt.getList("AutoConfigs", 10);
                autoConfigs.forEach(tag -> {
                    CompoundTag entry = (CompoundTag) tag;
                    int index = entry.getInt("Index");
                    boolean isAuto = index == 0 || index == 1;
                    String type = "";
                    switch (index) {
                        case 0 -> type = "auto_input";
                        case 1 -> type = "auto_output";
                        case 2 -> type = "item_transportation";
                        case 3 -> type = "fluid_transportation";
                    }
                    Component auto = Component.literal(" ")
                            .append(ModConstants.GUI.withSuffixTranslatable(type))
                            .append(": ")
                            .append(ModConstants.GUI.withSuffixTranslatable(isAuto ? entry.getBoolean("Enabled") ? "enabled" : "disabled" : entry.getBoolean("Enabled")  ? "disabled" : "enabled"))
                            .withColor(isAuto ? entry.getBoolean("Enabled") ? ChatFormatting.GREEN.getColor() : ChatFormatting.RED.getColor() : entry.getBoolean("Enabled") ? ChatFormatting.RED.getColor() : ChatFormatting.GREEN.getColor());
                    tooltipComponents.add(auto);
                });
            }
            if (containsRedstone) {
                ListTag redstoneConfigs = nbt.getList("RedstoneConfigs", 10);
                redstoneConfigs.forEach(tag -> {
                    CompoundTag entry = (CompoundTag) tag;
                    Component redstone = Component.literal(" ")
                            .append(ModConstants.GUI.withSuffixTranslatable("redstone"))
                            .append(": ")
                            .append(ModConstants.GUI.withSuffixTranslatable(entry.getString("Type").toLowerCase()))
                            .withStyle(ChatFormatting.YELLOW);
                    tooltipComponents.add(redstone);
                });
            }*/
        }
    }

    private void saveConfiguration(ItemStack drive, ModBlockEntity be) {
        CompoundTag nbt = drive.getOrCreateTag();
        CompoundTag tag = new CompoundTag();
        MachineConfiguration configuration = be.getConfiguration();
        if (configuration == null) return;
        configuration.save(tag);
        nbt.put(ModBlockEntity.CONFIG_TAG_KEY, tag);
        
    }
    
    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 18;
    }
    
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return getMaxStackSize(stack) == 1;
    }
}
