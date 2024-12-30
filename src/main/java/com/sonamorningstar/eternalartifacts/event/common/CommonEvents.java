package com.sonamorningstar.eternalartifacts.event.common;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import com.sonamorningstar.eternalartifacts.api.charm.TagReloadListener;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.ShockAbsorberBlockEntity;
import com.sonamorningstar.eternalartifacts.content.entity.ChargedSheepEntity;
import com.sonamorningstar.eternalartifacts.content.item.*;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.event.custom.DrumInteractEvent;
import com.sonamorningstar.eternalartifacts.event.custom.JarDrinkEvent;
import com.sonamorningstar.eternalartifacts.event.custom.charms.CharmTickEvent;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.ItemActivationToClient;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.EntityItemPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Optional;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MODID)
public class CommonEvents {
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
        int gasolineExplosionThreshold = Config.GASOLINE_EXPLOSION_THRESHOLD.get();
        if (stack.is(ModTags.Fluids.GASOLINE) && stack.getAmount() >= gasolineExplosionThreshold) {
            event.setFuseTime(40);
            event.setRadius(20.0F * ((float) stack.getAmount() / gasolineExplosionThreshold));
        }
    }

    @SubscribeEvent
    public static void livingHurtEvent(LivingHurtEvent event) {
        LivingEntity living = event.getEntity();
        if(living instanceof Player player) {
            ItemCooldowns cooldowns = player.getCooldowns();
            cooldowns.addCooldown(ModItems.MEDKIT.get(), 160);

            Item dagger = ModItems.HOLY_DAGGER.get();
            if(!PlayerCharmManager.findInPlayer(player, dagger).isEmpty()) {
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
        if(!PlayerCharmManager.findInPlayerWithTag(player, ModItems.ENCUMBATOR.get(), tag).isEmpty())
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void jumpEvent(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity instanceof Player player &&
                !PlayerCharmManager.findInPlayer(player, ModItems.FROG_LEGS.get()).isEmpty() &&
                !player.isCrouching()) {
            player.hurtMarked = true;
            player.setDeltaMovement(player.getDeltaMovement().add(0.0D, 0.2F, 0.0D));
        }
    }

    @SubscribeEvent
    public static void fallEvent(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity instanceof Player player && !PlayerCharmManager.findInPlayer(player, ModItems.FROG_LEGS.get()).isEmpty()) {
            event.setDistance(Math.max(event.getDistance() - 3, 0));
            event.setDamageMultiplier(0.5F);
        }
    }

    @SubscribeEvent
    public static void lightningOnEntity(EntityStruckByLightningEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Sheep sheep &&
                sheep.level() instanceof ServerLevel serverLevel &&
                net.neoforged.neoforge.event.EventHooks.canLivingConvert(sheep, ModEntities.CHARGED_SHEEP.get(), (timer) -> {})) {
            ChargedSheepEntity chargedSheep = ModEntities.CHARGED_SHEEP.get().create(serverLevel);
            if (chargedSheep == null) return;
            chargedSheep.copyPosition(sheep);
            chargedSheep.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(chargedSheep.blockPosition()), MobSpawnType.CONVERSION, null, null);
            chargedSheep.setNoAi(sheep.isNoAi());
            chargedSheep.setBaby(sheep.isBaby());
            chargedSheep.setColor(sheep.getColor());
            chargedSheep.setAge(sheep.getAge());
            if (sheep.hasCustomName()) {
                chargedSheep.setCustomName(sheep.getCustomName());
                chargedSheep.setCustomNameVisible(sheep.isCustomNameVisible());
            }
            chargedSheep.setPersistenceRequired();
            net.neoforged.neoforge.event.EventHooks.onLivingConvert(sheep, chargedSheep);
            serverLevel.addFreshEntityWithPassengers(chargedSheep);
            sheep.discard();
        }
    }

    @SubscribeEvent
    public static void itemTossEvent(ItemTossEvent event) {
        Player player = event.getPlayer();
        Level level = event.getPlayer().level();
        ItemCooldowns cd = player.getCooldowns();
        ItemStack dispenser = PlayerCharmManager.findCharm(player, Items.DISPENSER);
        if (!dispenser.isEmpty() && !player.isCrouching() && level instanceof ServerLevel sl && !cd.isOnCooldown(Items.DISPENSER)) {
            ItemEntity itemEntity = event.getEntity();
            ItemStack thrown = itemEntity.getItem();
            DispenseItemBehavior dispenseMethod = DispenserBlock.DISPENSER_REGISTRY.get(thrown.getItem());
            if (dispenseMethod != DispenseItemBehavior.NOOP) {
                Direction playerFacing = PlayerHelper.getFacingDirection(player);
                BlockPos bPos = BlockPos.containing(player.getEyePosition());
                if (playerFacing == Direction.DOWN) bPos = bPos.below();
                BlockState state = Blocks.DISPENSER.getStateDefinition().any().setValue(DispenserBlock.FACING, playerFacing);
                DispenserBlockEntity dummyEntity = new DispenserBlockEntity(bPos, state);
                BlockSource dummy = new BlockSource(
                        (ServerLevel) player.level(), bPos,
                        state, dummyEntity);
                itemEntity.setItem(dispenseMethod.dispense(dummy, thrown));
                cd.addCooldown(Items.DISPENSER, 4);
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
    public static void startTrackingEvent(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player player && !player.level().isClientSide) {
            CharmStorage.get(player).syncSelf();
        }
    }
    @SubscribeEvent
    public static void playerCloneEvent(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();
        var oldCharms = CharmStorage.get(oldPlayer);
        NonNullList<ItemStack> oldItems = oldCharms.getStacks();
        var newCharms = CharmStorage.get(newPlayer);
        for (int i = 0; i < oldItems.size(); i++)
            newCharms.setStackInSlot(i, oldItems.get(i));

    }
    @SubscribeEvent
    public static void entityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player && !player.level().isClientSide)
            CharmStorage.get(player).syncSelf();
    }
    @SubscribeEvent
    public static void playerChangeDimensionsEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        CharmStorage.get(player).syncSelf();
    }
    @SubscribeEvent
    public static void playerOpenContainerEvent(PlayerContainerEvent.Open event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        CharmStorage.get(player).syncSelf();
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
        if (event.phase == TickEvent.Phase.START) {
            var charms = player.getData(ModDataAttachments.CHARMS);
            for (int i = 0; i < charms.getSlots(); i++) {
                var stack = charms.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                if (player instanceof ServerPlayer sp && stack.getItem().isComplex()) {
                    Packet<?> packet = ((ComplexItem)stack.getItem()).getUpdatePacket(stack, sp.level(), sp);
                    if (packet != null) sp.connection.send(packet);
                }
                CharmTickEvent charmEvent = new CharmTickEvent(player, stack, i);
                NeoForge.EVENT_BUS.post(charmEvent);
            }

        }

    }

    @SubscribeEvent
    public static void charmTick(CharmTickEvent event) {
        Player player = event.getEntity();
        ItemStack charm = event.getCharm();
        int slot = event.getSlot();
        if (charm.getItem() instanceof ComfyShoesItem) {
            AttributeInstance step = player.getAttribute(NeoForgeMod.STEP_HEIGHT.value());
            if(step != null){
                var mod = ComfyShoesItem.getStepHeight();
                if (step.hasModifier(mod) && player.isCrouching()) step.removeModifier(mod.getId());
                if (!step.hasModifier(mod) && !player.isCrouching())
                    step.addTransientModifier(mod);
            }
        }
        if (charm.is(ModItems.MEDKIT)) {
            if (!player.getCooldowns().isOnCooldown(ModItems.MEDKIT.get()) && !player.hasEffect(MobEffects.REGENERATION)) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 25, 1, false, false, false));
            }
        }
        if (charm.getItem() instanceof MapItem mapItem) {
            mapItem.inventoryTick(charm, player.level(), player, -1, true);
        }
        if (charm.getItem() instanceof CompassItem compassItem) {
            compassItem.inventoryTick(charm, player.level(), player, -1, true);
        }

        if (charm.getItem() instanceof PortableBatteryItem battery) {
            battery.chargeSlots(player, charm);
        }
    }

    @SubscribeEvent
    public static void healEvent(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity.hasEffect(ModEffects.MALADY.get())) event.setCanceled(true);
    }

    public static volatile BlockHitResult eternal_Artifacts_Neoforge$cachedRay;
    @SubscribeEvent
    public static void blockBreakEvent(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getMainHandItem();
        LevelAccessor level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState blockState = event.getState();
        if (blockState.is(ModBlocks.POTTED_TIGRIS) && level instanceof Level realLevel) {
            realLevel.invalidateCapabilities(event.getPos());
        }
        if (itemStack.canApplyAtEnchantingTable(ModEnchantments.VERSATILITY.get())) {
            if (itemStack.getEnchantmentLevel(ModEnchantments.VERSATILITY.get()) > 0) {
                int amount = Config.VERSATILITY_COST.get() - 1;
                itemStack.hurt(amount, level.getRandom(), player instanceof ServerPlayer serverPlayer ? serverPlayer : null);
            }
        }
        if (itemStack.getItem() instanceof ChiselItem) {
            eternal_Artifacts_Neoforge$cachedRay = RayTraceHelper.retrace(player, ClipContext.Fluid.NONE);
        }
    }

    @SubscribeEvent
    public static void preExplosionEvent(ExplosionEvent.Start event) {
        Level level = event.getLevel();
        Explosion explosion = event.getExplosion();
        if(explosion.radius() <= 0) return;
        Vec3 center = explosion.center();
        BlockPos pos = BlockPos.containing(center.x, center.y, center.z);
        for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(2, 2, 2), pos.offset(-2, -2, -2))) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof ShockAbsorberBlockEntity absorber) {
                ModEnergyStorage energy = absorber.energy;
                if (energy != null) {
                    int generated = (int) (explosion.radius() * 5000);
                    int received = energy.receiveEnergyForced(generated, false);
                    if (received == generated) event.setCanceled(true);
                    if (received > 0) break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void useEvent(UseItemOnBlockEvent event) {
        ItemStack itemStack = event.getItemStack();
        Level level = event.getLevel();
        BlockPos blockPos = event.getPos();
        Player player = event.getEntity();
        BlockState blockState = level.getBlockState(blockPos);
        InteractionHand hand = event.getHand();
        ItemStack otherHandStack = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        UseOnContext context = event.getUseOnContext();
        if (event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK &&
                itemStack.canApplyAtEnchantingTable(ModEnchantments.VERSATILITY.get()) &&
                itemStack.getEnchantmentLevel(ModEnchantments.VERSATILITY.get()) > 0) {

            Optional<BlockState> axeModifiedState = calculateStateForAxe(level, blockPos, player, blockState, context);
            if (axeModifiedState.isPresent()) {
                level.setBlock(blockPos, axeModifiedState.get(), 11);
                level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, axeModifiedState.get()));

                if (player instanceof ServerPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, blockPos, itemStack);
                }

                itemStack.hurtAndBreak(Config.VERSATILITY_COST.get(), player, p -> p.broadcastBreakEvent(hand));
                if (level.isClientSide) player.swing(hand);
            }
            if (context.getClickedFace() != Direction.DOWN) {
                BlockState shovelModifiedState = blockState.getToolModifiedState(context, ToolActions.SHOVEL_FLATTEN, false);
                BlockState newState = null;

                if (shovelModifiedState != null && level.getBlockState(blockPos.above()).isAir()) {
                    level.playSound(player, blockPos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                    newState = shovelModifiedState;
                } else if (blockState.getBlock() instanceof CampfireBlock && blockState.getValue(CampfireBlock.LIT)) {
                    if (!level.isClientSide()) {
                        level.levelEvent(null, 1009, blockPos, 0);
                    }
                    CampfireBlock.dowse(player, level, blockPos, blockState);
                    newState = blockState.setValue(CampfireBlock.LIT, false);
                }

                if (newState != null) {
                    if (!level.isClientSide) {
                        level.setBlock(blockPos, newState, 11);
                        level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, newState));
                        itemStack.hurtAndBreak(Config.VERSATILITY_COST.get(), player, p -> p.broadcastBreakEvent(context.getHand()));
                    }else player.swing(hand);

                }
            }
        }

        if (event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_BEFORE_BLOCK &&
                blockState.is(Blocks.CHEST) &&
                (itemStack.is(Items.SHULKER_SHELL) || otherHandStack.is(Items.SHULKER_SHELL))) {
            byte result = ColoredShulkerShellItem.handleTransform(level, blockPos, itemStack, otherHandStack, player, blockState);
            if (result == 1) {
                if (player instanceof ServerPlayer sp) sp.awardStat(Stats.ITEM_USED.get(Items.SHULKER_SHELL));
                player.swing(hand, true);
                event.setCanceled(true);
            }
        }
    }

    private static Optional<BlockState> calculateStateForAxe(Level level, BlockPos pos, @Nullable Player player, BlockState state, UseOnContext context) {
        Optional<BlockState> stripState = Optional.ofNullable(state.getToolModifiedState(context, ToolActions.AXE_STRIP, false));
        if (stripState.isPresent()) {
            level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            return stripState;
        } else {
            Optional<BlockState> scrapeState = Optional.ofNullable(state.getToolModifiedState(context, ToolActions.AXE_SCRAPE, false));
            if (scrapeState.isPresent()) {
                level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3005, pos, 0);
                return scrapeState;
            } else {
                Optional<BlockState> wasOffState = Optional.ofNullable(state.getToolModifiedState(context, ToolActions.AXE_WAX_OFF, false));
                if (wasOffState.isPresent()) {
                    level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.levelEvent(player, 3004, pos, 0);
                    return wasOffState;
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerResourceReloadEvent(AddReloadListenerEvent event) {
        event.addListener(new TagReloadListener());
    }
}
