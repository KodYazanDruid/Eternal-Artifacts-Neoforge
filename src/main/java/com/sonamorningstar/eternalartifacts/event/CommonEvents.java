package com.sonamorningstar.eternalartifacts.event;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.Tornado;
import com.sonamorningstar.eternalartifacts.content.item.*;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.event.custom.DrumInteractEvent;
import com.sonamorningstar.eternalartifacts.event.custom.JarDrinkEvent;
import com.sonamorningstar.eternalartifacts.event.hooks.ModHooks;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.ItemActivationToClient;
import com.sonamorningstar.eternalartifacts.util.AutomationHelper;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.entity.player.EntityItemPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class CommonEvents {
    @SubscribeEvent
    public static void anvilUpdateEvent(AnvilUpdateEvent event) {
        /*if (event.getLeft().is(Items.APPLE)  && event.getRight().is(Items.ORANGE_DYE)) {
            event.setCost(5);
            event.setOutput(new ItemStack(ModItems.ORANGE.get()));
        }*/
    }

    //TODO: Do the data pack thing.
    @SubscribeEvent
    public static void jarDrinkEvent(JarDrinkEvent event) {
        FluidStack fluidStack = event.getFluidStack();
        if(fluidStack.is(Tags.Fluids.MILK)) {
            event.setDrinkingAmount(250);
            event.setDefaultUseTime();
            event.setAfterDrink((player, stack) -> player.removeEffectsCuredBy(EffectCures.MILK));
        }
        if(fluidStack.is(Fluids.LAVA)) {
            event.setUseTime(80);
            event.setAfterDrink((player, stack) -> player.setSecondsOnFire(10));
        }
        if(fluidStack.is(ModTags.Fluids.EXPERIENCE)) {
            event.setUseTime(20);
            int fluidAmount = fluidStack.getAmount();
            int drankAmount = fluidAmount - (fluidAmount % 20);
            if(drankAmount < 20) event.setCanceled(true);
            event.setDrinkingAmount(drankAmount);
            event.setAfterDrink((player, itemStack) -> player.giveExperiencePoints(drankAmount / 20));
            event.setAfterDrinkSound(SoundEvents.EXPERIENCE_ORB_PICKUP);
        }
        if(fluidStack.is(FluidTags.WATER)) {
            event.setDefaultUseTime();
            event.setAfterDrink((player, itemStack) -> {
                if(player.isOnFire()) event.setAfterDrinkSound(SoundEvents.FIRE_EXTINGUISH);
                player.setRemainingFireTicks(0);
            });
        }
        if(fluidStack.is(ModFluids.BEER.getFluid())) {
            event.setDefaultUseTime();
            event.setAfterDrink(((player, itemStack) -> player.heal(4.0F)));
        }

    }

    @SubscribeEvent
    public static void drumInteractEvent(DrumInteractEvent event) {
        FluidStack stack = event.getContent();
        if (stack.is(ModFluids.GASOLINE.getFluid()) && stack.getAmount() >= 1000) {
            event.setFuseTime(40);
            event.setRadius(20.0F);
        }
    }

    @SubscribeEvent
    public static void livingHurtEvent(LivingHurtEvent event) {
        LivingEntity living = event.getEntity();
        if(living instanceof Player player) {
            ItemCooldowns cooldowns = player.getCooldowns();
            cooldowns.addCooldown(ModItems.MEDKIT.get(), 160);

            Item dagger = ModItems.HOLY_DAGGER.get();
            if(PlayerHelper.findItem(player, dagger)) {
                if(!cooldowns.isOnCooldown(dagger)){
                    float damage = event.getAmount();
                    float health = player.getHealth();
                    float absorption = player.getAbsorptionAmount();
                    float maxHealth = player.getMaxHealth();
                    if (health + absorption <= damage || health + absorption / maxHealth <= 0.2F) {
                        player.addEffect(new MobEffectInstance(ModEffects.DIVINE_PROTECTION.get(), 600, 0));
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
        if(entity instanceof Player player && PlayerHelper.findItem(player, ModItems.FROG_LEGS.get()) && !player.isCrouching()) {
            player.hurtMarked = true;
            player.setDeltaMovement(player.getDeltaMovement().add(0.0D, 0.2F, 0.0D));
        }
    }

    @SubscribeEvent
    public static void fallEvent(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity instanceof Player player && PlayerHelper.findItem(player, ModItems.FROG_LEGS.get())) {
            event.setDistance(Math.max(event.getDistance() - 3, 0));
            event.setDamageMultiplier(0.5F);
        }
    }

    private static final Set<BlockPos> TREE_CHOP = new HashSet<>();
    private static final Set<BlockPos> ORE_BREAK = new HashSet<>();

    @SubscribeEvent
    public static void mineEvent(BlockEvent.BreakEvent event) {
        if(event.getLevel().isClientSide()) return;
        Player player = event.getPlayer();
        Level level = player.level();
        BlockPos pos = event.getPos();
        BlockState soil = level.getBlockState(pos.below());
        ItemStack stack = player.getMainHandItem();

        //Tree chopping.
        if(BlockHelper.isLog(level, pos) && stack.getItem() instanceof AxeOfRegrowthItem &&
                player instanceof ServerPlayer serverPlayer &&
                !TREE_CHOP.contains(pos)){

            event.setCanceled(true);
            //TREE_CHOP.add(pos);
            List<ItemStack> drops = AutomationHelper.doTreeHarvest(level, pos, stack, null, serverPlayer);
            //TREE_CHOP.remove(pos);
            ItemStack sapling = ItemStack.EMPTY;
            boolean saplingSetted = false;

            for (ItemStack is : drops) {
                if(is.getItem() instanceof BlockItem bi && bi.getBlock() instanceof SaplingBlock && !saplingSetted) {
                    sapling = is;
                    drops.remove(is);
                    saplingSetted = true;
                }
                ItemHandlerHelper.giveItemToPlayer(serverPlayer, is);
            }
            if (sapling.getItem() instanceof BlockItem bi &&
                    bi.getBlock() instanceof SaplingBlock saplingBlock &&
                    soil.canSustainPlant(level, pos, Direction.UP, saplingBlock)) {
                level.setBlockAndUpdate(pos, saplingBlock.defaultBlockState());
            }
        }
        //Ore breaking.
        if(BlockHelper.isOre(level, pos) && stack.getItem() instanceof ChloroveinPickaxeItem &&
                player instanceof ServerPlayer serverPlayer &&
                !ORE_BREAK.contains(pos)) {

            event.setCanceled(true);
            ORE_BREAK.add(pos);
            List<ItemStack> drops = AutomationHelper.doOreVeinMine(level, pos, stack, null, serverPlayer);
            ORE_BREAK.remove(pos);
            for (ItemStack is : drops) {
                ItemHandlerHelper.giveItemToPlayer(serverPlayer, is);
            }
        }


    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        Pair<Boolean, Integer> activeTicks = MagicFeatherItem.activeTicks;
        if(activeTicks != null){
            boolean bool = activeTicks.getFirst();
            int ticks = activeTicks.getSecond();
            if (ticks > 0) ticks--;
            else bool = false;
            MagicFeatherItem.activeTicks = Pair.of(bool, ticks);
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        AttributeInstance stepHeight = player.getAttribute(NeoForgeMod.STEP_HEIGHT.value());
        AttributeInstance spellDamage = player.getAttribute(ModAttributes.SPELL_DAMAGE.get());

        if(!(player.hasItemInSlot(EquipmentSlot.FEET) && player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.COMFY_SHOES.get()))) {
            if(stepHeight != null && stepHeight.hasModifier(ComfyShoesItem.getStepHeight())) {
                stepHeight.removeModifier(ComfyShoesItem.getStepHeight().getId());
            }
        }

        /*if (!PlayerHelper.findItem(player, ModItems.ORANGE.get())) {
            if (spellDamage != null && spellDamage.hasModifier(Spell.spellDamage)) {
                spellDamage.removeModifier(Spell.spellDamage.getId());
            }
        }else if (spellDamage != null && !spellDamage.hasModifier(Spell.spellDamage)) {
            spellDamage.addTransientModifier(Spell.spellDamage);
        }*/
    }

    @SubscribeEvent
    public static void healEvent(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity.hasEffect(ModEffects.MALADY.get())) event.setCanceled(true);
    }

}
