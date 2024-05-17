package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.item.*;
import com.sonamorningstar.eternalartifacts.content.item.block.FancyChestBlockItem;
import com.sonamorningstar.eternalartifacts.content.item.block.GardeningPotBlockItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<Item> ORANGE = register("orange", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build()));
    public static final DeferredItem<Item> BANANA = register("banana", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build()));
    public static final DeferredItem<Item> ANCIENT_FRUIT = register("ancient_fruit", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build()));
    public static final DeferredItem<Item> RAW_MEAT_INGOT = register("raw_meat_ingot", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).meat().build()));
    public static final DeferredItem<Item> MEAT_INGOT = register("meat_ingot", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.8f).meat().build()));
    public static final DeferredItem<Item> APPLE_PIE = register("apple_pie", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.3f).build()));
    public static final DeferredItem<Item> BANANA_CREAM_PIE = register("banana_cream_pie", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.3f).build()));
    public static final DeferredItem<Item> GOLDEN_ANCIENT_FRUIT = register("golden_ancient_fruit", GoldenAncientFruitItem::new, new Item.Properties().rarity(Rarity.RARE).food(
            new FoodProperties.Builder()
                .nutrition(4)
                .saturationMod(1.2F)
                .effect(()->new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2400, 1), 1.0F)
                .effect(()->new MobEffectInstance(MobEffects.DIG_SPEED, 2400, 1), 1.0F)
                .alwaysEat()
                .build()));
    public static final DeferredItem<Item> ENCHANTED_GOLDEN_ANCIENT_FRUIT = register("enchanted_golden_ancient_fruit", EnchantedGoldenAncientFruitItem::new, new Item.Properties().rarity(Rarity.EPIC).food(
            new FoodProperties.Builder()
                .nutrition(4)
                .saturationMod(1.2F)
                .effect(()->new MobEffectInstance(ModEffects.FLIGHT.get(), 2400, 0), 1.0F)
                .effect(()->new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2400, 2), 1.0F)
                .effect(()->new MobEffectInstance(MobEffects.DIG_SPEED, 2400, 2), 1.0F)
                .effect(()->new MobEffectInstance(MobEffects.ABSORPTION, 2400, 4), 1.0F)
                .alwaysEat()
                .build()));
    public static final DeferredItem<Item> BATTERY = register("battery", Item::new);
    public static final DeferredItem<Item> CAPACITOR = register("capacitor", Item::new);
    public static final DeferredItem<Item> LENS = register("lens", Item::new);
    public static final DeferredItem<Item> PLANT_MATTER = register("plant_matter", Item::new);
    public static final DeferredItem<Item> PINK_SLIME = register("pink_slime", Item::new);
    public static final DeferredItem<Item> GOLD_RING = registerArtifact("gold_ring", Item::new);
    public static final DeferredItem<Item> SUGAR_CHARCOAL = register("sugar_charcoal", Item::new);

    public static final DeferredItem<Item> DEMON_EYE_SPAWN_EGG = register("demon_eye_spawn_egg",
            ()-> new DeferredSpawnEggItem(ModEntities.DEMON_EYE, 0xDDA4A4, 0x721212, new Item.Properties()));
    public static final DeferredItem<Item> PINKY_SPAWN_EGG = register("pinky_spawn_egg",
            ()-> new DeferredSpawnEggItem(ModEntities.PINKY, 0xE8B3E2, 0xC062B3, new Item.Properties()));

    //Actual artifacts.
    public static final DeferredItem<Item> HOLY_DAGGER = registerArtifact("holy_dagger", HolyDaggerItem::new);
    public static final DeferredItem<Item> MEDKIT = registerArtifact("medkit", MedkitItem::new);
    public static final DeferredItem<Item> FROG_LEGS = registerArtifact("frog_legs", FrogLegsItem::new);
    public static final DeferredItem<Item> MAGIC_FEATHER = registerArtifact("magic_feather", MagicFeatherItem::new);
    public static final DeferredItem<Item> ENCUMBATOR = registerArtifact("encumbator", EncumbatorItem::new);
    public static final DeferredItem<Item> ENDER_POUCH = registerArtifact("ender_pouch", EnderPouchItem::new);
    public static final DeferredItem<Item> PORTABLE_CRAFTER = registerArtifact("portable_crafter", PortableCrafterItem::new);
    public static final DeferredItem<Item> COMFY_SHOES = registerArtifact("comfy_shoes", ComfyShoesItem::new);
    public static final DeferredItem<Item> ENDER_NOTEBOOK = registerArtifact("ender_notebook", EnderNotebookItem::new);

    //Tools.
    public static final DeferredItem<Item> AXE_OF_REGROWTH = register("axe_of_regrowth", AxeOfRegrowthItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredHolder<Item, BucketItem> NOUS_BUCKET = register("nous_bucket",
            p -> new BucketItem(ModFluids.NOUS_SOURCE::value, p.stacksTo(1).craftRemainder(Items.BUCKET)));
    public static final DeferredHolder<Item, BucketItem> LIQUID_MEAT_BUCKET = register("liquid_meat_bucket",
            p -> new BucketItem(ModFluids.LIQUID_MEAT_SOURCE::value, p.stacksTo(1).craftRemainder(Items.BUCKET)));
    public static final DeferredHolder<Item, BucketItem> PINK_SLIME_BUCKET = register("pink_slime_bucket",
            p -> new BucketItem(ModFluids.PINK_SLIME_SOURCE::value, p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final DeferredItem<RetexturedBlockItem> GARDENING_POT = register("gardening_pot", ()-> new GardeningPotBlockItem(ModTags.Items.GARDENING_POT_SUITABLE, new Item.Properties()));
    public static final DeferredItem<RetexturedBlockItem> FANCY_CHEST = register("fancy_chest", ()-> new FancyChestBlockItem(ModTags.Items.GARDENING_POT_SUITABLE, new Item.Properties()));

    public static final DeferredItem<Item> ANCIENT_SEED = register("ancient_seed",
            () -> new ItemNameBlockItem(ModBlocks.ANCIENT_CROP.get(), new Item.Properties()));
    public static final DeferredItem<DoubleHighBlockItem> FORSYTHIA = register("forsythia", ()-> new DoubleHighBlockItem(ModBlocks.FORSYTHIA.get(), new Item.Properties()));

    private static <T extends Item> DeferredItem<T> register(String name, Supplier<T> supplier) {
        return ITEMS.register(name, supplier);
    }

    private static <T extends Item> DeferredItem<T> register(String name, Function<Item.Properties, T> func) {
        return register(name, ()-> func.apply(new Item.Properties()));
    }

    private static <T extends Item> DeferredItem<T> registerArtifact(String name, Function<Item.Properties, T> func) {
        return register(name, ()-> func.apply(new Item.Properties().stacksTo(1)));
    }

    private static <T extends Item> DeferredItem<T> register(String name, Function<Item.Properties, T> func, Item.Properties props) {
        return props == null ? register(name, func) : ITEMS.register(name, ()-> func.apply(props));
    }
}
