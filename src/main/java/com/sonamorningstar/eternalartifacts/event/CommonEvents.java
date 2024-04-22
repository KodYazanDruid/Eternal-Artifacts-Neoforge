package com.sonamorningstar.eternalartifacts.event;

import com.sonamorningstar.eternalartifacts.content.item.AxeOfRegrowthItem;
import com.sonamorningstar.eternalartifacts.content.item.EncumbatorItem;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModSounds;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.ItemActivationToClient;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import com.sonamorningstar.eternalartifacts.util.PlantHelper;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.entity.player.EntityItemPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class CommonEvents {
    @SubscribeEvent
    public static void anvilUpdateEvent(AnvilUpdateEvent event) {
        if (event.getLeft().is(Items.APPLE)  && event.getRight().is(Items.ORANGE_DYE)) {
            event.setCost(5);
            event.setOutput(new ItemStack(ModItems.ORANGE.get()));
        }
    }

    @SubscribeEvent
    public static void livingHurtEvent(LivingHurtEvent event) {
        LivingEntity living = event.getEntity();
        if(living instanceof Player player) {
            ItemCooldowns cooldowns = player.getCooldowns();
            cooldowns.addCooldown(ModItems.MEDKIT.get(), 160);

            Item dagger = ModItems.HOLY_DAGGER.get();
            if(PlayerHelper.findInStack(player, dagger)) {
                if(!cooldowns.isOnCooldown(dagger)){
                    float damage = event.getAmount();
                    float health = player.getHealth();
                    float absorption = player.getAbsorptionAmount();
                    float maxHealth = player.getMaxHealth();
                    if (health + absorption <= damage || health + absorption / maxHealth <= 0.2F) {
                        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 4));
                        player.getCooldowns().addCooldown(dagger, 6000);
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.HOLY_DAGGER_ACTIVATE.get(), player.getSoundSource());
                        Channel.sendToPlayer(new ItemActivationToClient(dagger.getDefaultInstance()), (ServerPlayer) player);
                    }
                }
            }
        }

    }

    @SubscribeEvent
    public static void pickupEvent(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(EncumbatorItem.ACTIVE, true);
        if(PlayerHelper.findInStackWithTag(player, ModItems.ENCUMBATOR.get(), tag)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void jumpEvent(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity instanceof Player player && PlayerHelper.findInStack(player, ModItems.FROG_LEGS.get()) && !player.isCrouching()) {
            player.hurtMarked = true;
            player.setDeltaMovement(player.getDeltaMovement().add(0.0D, 0.2F, 0.0D));
        }

    }

    @SubscribeEvent
    public static void fallEvent(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity instanceof Player player && PlayerHelper.findInStack(player, ModItems.FROG_LEGS.get())) {
            event.setDistance(Math.max(event.getDistance() - 3, 0));
            event.setDamageMultiplier(0.5F);
        }
    }

    @SubscribeEvent
    public static void mineEvent(BlockEvent.BreakEvent event) {
        if(event.getLevel().isClientSide()) return;
        Player player = event.getPlayer();
        Level level = player.level();
        BlockPos pos = event.getPos();
        BlockState soil = level.getBlockState(pos.below());
        ItemStack stack = player.getMainHandItem();
        if(BlockHelper.isLog(level, pos) && stack.getItem() instanceof AxeOfRegrowthItem){
            event.setCanceled(true);
            List<ItemStack> drops = PlantHelper.doTreeHarvest(level, pos, stack, null);
            ItemStack sapling = ItemStack.EMPTY;
            boolean saplingSetted = false;

            for (ItemStack is : drops) {
                if(is.getItem() instanceof BlockItem bi && bi.getBlock() instanceof SaplingBlock && !saplingSetted) {
                    sapling = is;
                    drops.remove(is);
                    saplingSetted = true;
                }
                ItemHandlerHelper.giveItemToPlayer(player, is);
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            }
            if (sapling.getItem() instanceof BlockItem bi && bi.getBlock() instanceof SaplingBlock saplingBlock && saplingBlock.canSurvive(soil, level, pos)) {
                level.setBlockAndUpdate(pos, saplingBlock.defaultBlockState());
            }
        }
    }

}
