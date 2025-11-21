package com.sonamorningstar.eternalartifacts.event.common;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.api.forceload.ForceLoadManager;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.api.morph.PlayerMorphUtil;
import com.sonamorningstar.eternalartifacts.client.gui.tooltip.ItemTooltipManager;
import com.sonamorningstar.eternalartifacts.container.BlueprintMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.ShockAbsorber;
import com.sonamorningstar.eternalartifacts.content.enchantment.base.AttributeEnchantment;
import com.sonamorningstar.eternalartifacts.event.ModResourceReloadListener;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.content.enchantment.SoulboundEnchantment;
import com.sonamorningstar.eternalartifacts.content.enchantment.VersatilityEnchantment;
import com.sonamorningstar.eternalartifacts.content.entity.ChargedSheepEntity;
import com.sonamorningstar.eternalartifacts.content.item.*;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.event.custom.DrumInteractEvent;
import com.sonamorningstar.eternalartifacts.event.custom.JarDrinkEvent;
import com.sonamorningstar.eternalartifacts.event.custom.charms.CharmTickEvent;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.ILivingDasher;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.ILivingJumper;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.ItemActivationToClient;
import com.sonamorningstar.eternalartifacts.network.SavePlayerDataToClient;
import com.sonamorningstar.eternalartifacts.util.LootTableHelper;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
        //TODO: Yippie ki-yay mother
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
        LivingEntity hurtEntity = event.getEntity();
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();
        Level level = hurtEntity.level();
        float damage = event.getAmount();
        float health = hurtEntity.getHealth();
        float absorption = hurtEntity.getAbsorptionAmount();
        float maxHealth = hurtEntity.getMaxHealth();
        float healthPercent = health / maxHealth;
        float totalHealthPercent = (health + absorption) / maxHealth;
        if(hurtEntity instanceof Player player) {
            ItemCooldowns cooldowns = player.getCooldowns();
            cooldowns.addCooldown(ModItems.MEDKIT.get(), 160);
            
            if (health + absorption <= damage || totalHealthPercent <= 0.5) {
                ItemStack charm = CharmManager.findCharm(player, ModItems.HOLY_DAGGER.get());
                if (!charm.isEmpty() && !cooldowns.isOnCooldown(charm.getItem())) {
                    player.addEffect(new MobEffectInstance(ModEffects.DIVINE_PROTECTION.get(), 600, 0));
                    cooldowns.addCooldown(charm.getItem(), 6000);
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.HOLY_DAGGER_ACTIVATE.get(), player.getSoundSource());
                    Channel.sendToPlayer(new ItemActivationToClient(charm), (ServerPlayer) player);
                }
            }
            
            if (healthPercent <= 0.3) {
                ItemStack charm = CharmManager.findCharm(player, ModItems.HEART_NECKLACE.get());
                if (!charm.isEmpty() && !cooldowns.isOnCooldown(charm.getItem())) {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
                    cooldowns.addCooldown(charm.getItem(), 1200);
                }
            }
            
            ItemStack oddlyShapedOpal = CharmManager.findCharm(player, ModItems.ODDLY_SHAPED_OPAL.get());
            if (!oddlyShapedOpal.isEmpty() &&
                !cooldowns.isOnCooldown(oddlyShapedOpal.getItem()) &&
                !(source.is(DamageTypeTags.BYPASSES_ARMOR))) {
               event.setAmount(damage * 0.5F);
               cooldowns.addCooldown(ModItems.ODDLY_SHAPED_OPAL.get(), 200);
            }
            
        }
        
        if (attacker instanceof LivingEntity lAttacker && !hurtEntity.isDeadOrDying()) {
            for (int i = 0; i < CharmStorage.get(lAttacker).getSlots(); i++) {
                ItemStack charm = CharmStorage.get(lAttacker).getStackInSlot(i);
                if (charm.is(ModItems.MAGIC_BANE) &&
                    !source.is(Tags.DamageTypes.IS_MAGIC) &&
                    !source.is(Tags.DamageTypes.IS_TECHNICAL) &&
                    !source.is(ModDamageTypes.EXECUTE.get())) {
                    DamageSource magicDamage = ModDamageSources.INSTANCES.get(attacker.level()).magicBypassIFrame(lAttacker);
                    float rawDamage = damage * Config.MAGIC_BANE_DAMAGE_CONVERT_MULTIPLIER.get().floatValue();
                    float spellDamage = (float) lAttacker.getAttributeValue(ModAttributes.SPELL_POWER);
                    float amplified = rawDamage * spellDamage / 100.0F;
                    hurtEntity.hurt(magicDamage, amplified);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.ENCHANT,
                            hurtEntity.getX(), hurtEntity.getY() + hurtEntity.getBbHeight() / 2.0F, hurtEntity.getZ(),
                            50, 0.5D, 0.5D, 0.5D, 0.1D);
                    }
                }
                if (charm.is(ModItems.MOONGLASS_PENDANT) && source.is(Tags.DamageTypes.IS_MAGIC)) {
                    float heal = damage * Config.MOONGLASS_PENDANT_HEAL_MULTIPLIER.get().floatValue();
                    lAttacker.heal(heal);
                }
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void livingDamageEvent(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();
        LivingEntity target = event.getEntity();
        float remainingHealth = target.getHealth() - event.getAmount();
        
        if (attacker instanceof LivingEntity leAttacker &&
            !target.isDeadOrDying() &&
            !target.getType().is(ModTags.Entities.EXECUTE_BLACKLISTED) &&
            !source.is(ModDamageTypes.EXECUTE.get())) {
            
            ItemStack finalCut = CharmManager.findCharm(leAttacker, ModItems.FINAL_CUT.get());
            if (!finalCut.isEmpty()) {
                float threshold = Config.FINAL_CUT_EXECUTE_THRESHOLD.get().floatValue();
                if (remainingHealth / target.getMaxHealth() <= threshold) {
                    event.setCanceled(true);
                    Level level = target.level();
                    target.hurt(ModDamageSources.INSTANCES.get(level).execute(leAttacker), Float.MAX_VALUE);
                    double pPosY = target.getY() + target.getBbHeight() / 2.0F;
                    level.playSound(null, target.getX(), pPosY, target.getZ(),
                        ModSounds.FINAL_CUT_EFFECT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    if (level instanceof ServerLevel tsLevel) {
                        tsLevel.sendParticles(ParticleTypes.CRIT,
                            target.getX(), pPosY, target.getZ(),
                            50, 0.5D, 0.5D, 0.5D, 0.1D);
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
        if(!CharmManager.findInPlayerWithTag(player, ModItems.ENCUMBATOR.get(), tag).isEmpty())
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void jumpEvent(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        if (!CharmManager.findCharm(entity, ModItems.FROG_LEGS.get()).isEmpty() &&
                !entity.isCrouching()) {
            entity.hurtMarked = true;
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.2F, 0.0D));
        }
        entity.getPersistentData().putInt(ILivingDasher.KEY, 1);
    }
    @SubscribeEvent
    public static void fallEvent(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        if(!CharmManager.findCharm(entity, ModItems.FROG_LEGS.get()).isEmpty()) {
            event.setDistance(Math.max(event.getDistance() - 3, 0));
            event.setDamageMultiplier(0.5F);
        }
    }
    
    @SubscribeEvent
    public static void effectAddedEvent(MobEffectEvent.Added event) {
        MobEffectInstance oldEffect = event.getOldEffectInstance();
        MobEffectInstance newEffect = event.getEffectInstance();
        LivingEntity entity = event.getEntity();
        if (oldEffect == null && newEffect != null && newEffect.getEffect().getCategory() == MobEffectCategory.HARMFUL &&
                !newEffect.getEffect().isInstantenous() && entity instanceof Player player &&
                !player.getCooldowns().isOnCooldown(ModItems.RAINCOAT.get()) &&
                !CharmManager.findCharm(entity, ModItems.RAINCOAT.get()).isEmpty()) {
            newEffect.duration = 0;
            player.getCooldowns().addCooldown(ModItems.RAINCOAT.get(), 300);
        }
    }
    
    @SubscribeEvent
    public static void getItemAttributesEvent(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        EquipmentSlot slot = event.getSlotType();
        Map<Enchantment, Integer> allEnchantments = stack.getAllEnchantments();
        for (Map.Entry<Enchantment, Integer> entry : allEnchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();
            if (enchantment instanceof AttributeEnchantment attrEnchant && level > 0) {
                Set<Attribute> attributeSet = attrEnchant.getAttributeSet();
                for (Attribute attribute : attributeSet) {
                    AttributeModifier mod = attrEnchant.getModifier(attribute, slot, stack, level);
                    if (mod != null) event.addModifier(attribute, mod);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void livingTickEvent(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        
        var charms = CharmStorage.get(living);
        for (int i = 0; i < charms.getSlots(); i++) {
            var stack = charms.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (living instanceof ServerPlayer sp && stack.getItem().isComplex()) {
                Packet<?> packet = ((ComplexItem)stack.getItem()).getUpdatePacket(stack, sp.level(), sp);
                if (packet != null) sp.connection.send(packet);
            }
            if (i != 12 || CharmStorage.canHaveWildcard(living)) {
                CharmTickEvent charmEvent = new CharmTickEvent(living, stack, i);
                NeoForge.EVENT_BUS.post(charmEvent);
            }
        }
        
        if(living.onGround() && !(living instanceof AbstractClientPlayer)) {
            if (living instanceof ServerPlayer sp) {
                living.getPersistentData().putInt(ILivingJumper.KEY, 1);
                Channel.sendToPlayer(new SavePlayerDataToClient(ILivingJumper.KEY, 1), sp);
            } else {
                living.getPersistentData().putInt(ILivingJumper.KEY, 1);
            }
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
        ItemStack dispenser = CharmManager.findCharm(player, Items.DISPENSER);
        if (!dispenser.isEmpty() && !player.isCrouching() && level instanceof ServerLevel sl && !cd.isOnCooldown(Items.DISPENSER)) {
            ItemEntity itemEntity = event.getEntity();
            ItemStack thrown = itemEntity.getItem();
            if (thrown.getCount() > 1) return;
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
    public static void onLevelTickPre(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (event.level.isClientSide()) return;
            ForceLoadManager.onServerWorldTick(event.level.getServer());
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
        Entity tracked = event.getTarget();
        Player player = event.getEntity();
        
        if (tracked instanceof LivingEntity living && !player.level().isClientSide()) {
            CharmStorage.get(living).synchTracking();
        }
    }
    @SubscribeEvent
    public static void playerCloneEvent(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();
        Inventory oldInventory = oldPlayer.getInventory();
        Inventory newInventory = newPlayer.getInventory();
        var oldCharms = CharmStorage.get(oldPlayer);
        var newCharms = CharmStorage.get(newPlayer);
        boolean wasDeath = event.isWasDeath();
        Level level = oldPlayer.level();
        boolean doKeep = level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);

        if (wasDeath) {
            boolean oldWildcard = CharmStorage.canHaveWildcard(oldPlayer);
            newCharms.setWildcardNbt(oldWildcard);

            if (!doKeep && !oldPlayer.isSpectator()) {
                for (int i = 0; i < oldCharms.getSlots(); i++) {
                    ItemStack oldStack = oldCharms.getStackInSlot(i);
                    if (SoulboundEnchantment.has(oldStack)) newCharms.setStackInSlot(i, oldStack);
                }
                
                for (int i = 0; i < oldInventory.getContainerSize(); i++) {
                    ItemStack stack = oldInventory.getItem(i);
                    if (SoulboundEnchantment.has(stack)) newInventory.setItem(i, oldInventory.getItem(i));
                }
            } else {
                for (int i = 0; i < oldCharms.getSlots(); i++) {
                    ItemStack oldStack = oldCharms.getStackInSlot(i);
                    newCharms.setStackInSlot(i, oldStack.copyAndClear());
                }
            }
        }
    }
    
    public static final String TAG_KEY = "EtarMagicQuiver";
    @SubscribeEvent
    public static void entityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        
        if (!entity.level().isClientSide() && entity instanceof Player player) {
            CharmStorage.get(player).syncSelf();
        }
       
        if (entity instanceof AbstractArrow arrow) {
            Entity owner = arrow.getOwner();
            if (owner instanceof ServerPlayer sp) {
                EntityType<?> morph = PlayerMorphUtil.MORPH_MAP.get(sp);
                if (morph == EntityType.WITHER_SKELETON) {
                    arrow.setSecondsOnFire(100);
                } else if (morph == EntityType.STRAY && arrow instanceof Arrow arr) {
                    arr.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600));
                    arr.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
            }
            ItemStack pickup = arrow.getPickupItemStackOrigin();
            if (pickup.hasTag() && pickup.getTag().getBoolean(TAG_KEY)) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
        }
        
        if (entity instanceof FishingHook hook) {
            Entity owner = hook.getOwner();
            if (owner instanceof LivingEntity living) {
                if (living.hasEffect(ModEffects.ANGLERS_LUCK.get())) {
                    hook.luck += living.getEffect(ModEffects.ANGLERS_LUCK.get()).getAmplifier() + 1;
                }
                if (living.hasEffect(ModEffects.LURING.get())) {
                    hook.lureSpeed += living.getEffect(ModEffects.LURING.get()).getAmplifier() + 1;
                }
            }
        }
        
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem();
            if (stack.getEnchantmentLevel(ModEnchantments.EVERLASTING.get()) > 0) {
                itemEntity.setUnlimitedLifetime();
            }
        }
        
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
        AbstractContainerMenu menu = event.getContainer();
        if (player.level().isClientSide) return;
        CharmStorage.get(player).syncSelf();
        
        if (menu instanceof BlueprintMenu bpMenu && player instanceof ServerPlayer sp) {
            bpMenu.synchIngredients(sp);
        }
    }
    
    @SubscribeEvent
    public static void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        ItemTooltipManager.setReload();
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel sl)) return;
        var networks = TesseractNetworks.get(sl).getTesseractNetworks();
        for (TesseractNetwork<?> network : networks) {
            if (network.getPendingWhitelistPlayers().contains(player.getGameProfile().getName())) {
                network.getPendingWhitelistPlayers().remove(player.getGameProfile().getName());
                network.getWhitelistedPlayers().add(player.getGameProfile());
                EternalArtifacts.LOGGER.info("Player {} has been whitelisted to tesseract network {}.", player.getGameProfile().getName(), network.getUuid());
            }
        }
    }
    
    @SubscribeEvent
    public static void levelLoadEvent(LevelEvent.Load event) {
        LevelAccessor level = event.getLevel();
        ModDamageSources.INSTANCES.put(level, new ModDamageSources(level.registryAccess()));
    }
    @SubscribeEvent
    public static void levelUnloadEvent(LevelEvent.Unload event) {
        LevelAccessor level = event.getLevel();
        ModDamageSources.INSTANCES.remove(level);
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
        int expDrop = event.getExpToDrop();
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
        if (expDrop > 0) {
            ItemStack talisman = CharmManager.findCharm(player, ModItems.SAGES_TALISMAN.get());
            if (!talisman.isEmpty()) {
                event.setExpToDrop(Mth.ceil(expDrop * 1.2));
            }
        }
    }
    
    @SubscribeEvent
    public static void blockPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        Entity entity = event.getEntity();
        LevelAccessor levelAcc = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState placedState = event.getPlacedBlock();
        
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
            if (blockEntity instanceof ShockAbsorber absorber) {
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
    public static void enderManAngerEvent(EnderManAngerEvent event) {
        Player player = event.getPlayer();
        ItemStack carved = CharmManager.findCharm(player, Items.CARVED_PUMPKIN);
        if (!carved.isEmpty()) event.setCanceled(true);
    }
    
    @SubscribeEvent
    public static void livingAttackEvent(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        Entity attacker = source.getDirectEntity();
        ItemStack tool = attacker instanceof LivingEntity living ? living.getMainHandItem() : ItemStack.EMPTY;
        int lvl = tool.getEnchantmentLevel(ModEnchantments.MELTING_TOUCH.get());
        if (lvl > 0) {
            entity.setSecondsOnFire(lvl * 4);
        }
        if (attacker instanceof ServerPlayer sp && PlayerMorphUtil.MORPH_MAP.get(sp) == EntityType.WITHER_SKELETON) {
            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 200), attacker);
        }
    }
    
    @SubscribeEvent
    public static void experienceDropEvent(LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        if (player != null) {
            ItemStack talisman = CharmManager.findCharm(player, ModItems.SAGES_TALISMAN.get());
            if (!talisman.isEmpty()) {
                event.setDroppedExperience(Mth.ceil(event.getDroppedExperience() * 1.2));
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
        if (event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK && VersatilityEnchantment.has(itemStack)){
            BlockState axeModifiedState = calculateStateForAxe(level, blockPos, player, blockState, context);
            if (axeModifiedState != null) {
                level.setBlock(blockPos, axeModifiedState, 11);
                level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, axeModifiedState));

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

    @Nullable
    private static BlockState calculateStateForAxe(Level level, BlockPos pos, @Nullable Player player, BlockState state, UseOnContext context) {
        Optional<BlockState> stripState = Optional.ofNullable(state.getToolModifiedState(context, ToolActions.AXE_STRIP, false));
        if (stripState.isPresent()) {
            level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            return stripState.get();
        } else {
            Optional<BlockState> scrapeState = Optional.ofNullable(state.getToolModifiedState(context, ToolActions.AXE_SCRAPE, false));
            if (scrapeState.isPresent()) {
                level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3005, pos, 0);
                return scrapeState.get();
            } else {
                Optional<BlockState> wasOffState = Optional.ofNullable(state.getToolModifiedState(context, ToolActions.AXE_WAX_OFF, false));
                if (wasOffState.isPresent()) {
                    level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.levelEvent(player, 3004, pos, 0);
                    return wasOffState.get();
                } else {
                    return null;
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerResourceReloadEvent(AddReloadListenerEvent event) {
        event.addListener(new ModResourceReloadListener());
    }
    
    @SubscribeEvent
    public static void serverStartingEvent(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        rollHammeringTables(server.overworld());
        server.getRecipeManager().hadErrorsLoading();
    }
    
    private static void rollHammeringTables(ServerLevel level) {
        for (TagKey<Block> tag : HammerItem.gatheredTags) {
            ResourceLocation tableId = HammerItem.getTableForTag(tag);
            Map<Item, Pair<Float, Float>> results = LootTableHelper.getItemsWithCounts(level, tableId);
            results.forEach((item, pair) -> HammerItem.tagDropRates.put(tag, Pair.of(item, pair)));
            
        }
        for (Block block : HammerItem.gatheredBlocks) {
            ResourceLocation tableId = HammerItem.getTableForBlock(block);
            Map<Item, Pair<Float, Float>> results = LootTableHelper.getItemsWithCounts(level, tableId);
            results.forEach((item, pair) -> HammerItem.blockDropRates.put(block, Pair.of(item, pair)));
        }
        
    }
    
    @SubscribeEvent
    public static void interactEntityEvent(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        Entity target = event.getTarget();
        Player player = event.getEntity();
        if (stack.getItem() instanceof DyeItem dye && target instanceof Shulker shulker && shulker.isAlive() &&
                shulker.getColor() != dye.getDyeColor()) {
            DyeColor color = dye.getDyeColor();
            shulker.level().playSound(player, shulker, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!player.level().isClientSide()) {
                shulker.setVariant(Optional.of(color));
                stack.shrink(1);
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            }
            player.swing(event.getHand());
        }
    }
}