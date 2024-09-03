package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.item.*;
import com.sonamorningstar.eternalartifacts.content.item.block.FancyChestBlockItem;
import com.sonamorningstar.eternalartifacts.content.item.block.GardeningPotBlockItem;
import com.sonamorningstar.eternalartifacts.content.item.block.JarBlockItem;
import com.sonamorningstar.eternalartifacts.content.item.block.base.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
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
    public static final DeferredItem<Item> DUCK_MEAT = register("duck_meat", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3f).meat().build()));
    public static final DeferredItem<Item> COOKED_DUCK_MEAT = register("cooked_duck_meat", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6f).meat().build()));
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
    public static final DeferredItem<Item> BATTERY = registerStacksToOne("battery", BatteryItem::new);
    public static final DeferredItem<Item> CAPACITOR = register("capacitor");
    public static final DeferredItem<Item> LENS = register("lens");
    public static final DeferredItem<Item> PLANT_MATTER = register("plant_matter");
    public static final DeferredItem<Item> PINK_SLIME = register("pink_slime");
    public static final DeferredItem<Item> GOLD_RING = registerStacksToOne("gold_ring");
    public static final DeferredItem<Item> SUGAR_CHARCOAL = register("sugar_charcoal");
    public static final DeferredItem<Item> ENDER_TABLET = register("ender_tablet");
    public static final DeferredItem<Item> STONE_TABLET = register("stone_tablet");
    public static final DeferredItem<Item> CHLOROPHYTE_TABLET = register("chlorophyte_tablet");
    public static final DeferredItem<Item> CHLOROPHYTE_INGOT = register("chlorophyte_ingot");
    public static final DeferredItem<Item> CHLOROPHYTE_UPGRADE_SMITHING_TEMPLATE = register("chlorophyte_upgrade_smithing_template",
            ()-> new SmithingTemplateItem(
                    ModConstants.CHLOROPHYTE_UPGRADE_APPLIES_TO.translatable().withStyle(ChatFormatting.BLUE),
                    ModConstants.CHLOROPHYTE_UPGRADE_INGREDIENTS.translatable().withStyle(ChatFormatting.BLUE),
                    Component.translatable(Util.makeDescriptionId("upgrade", new ResourceLocation(MODID,"chlorophyte_upgrade"))).withStyle(ChatFormatting.GRAY),
                    ModConstants.CHLOROPHYTE_UPGRADE_BASE_SLOT_DESCRIPTION.translatable(),
                    ModConstants.CHLOROPHYTE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION.translatable(),
                    List.of(
                            new ResourceLocation("item/empty_slot_sword"),
                            new ResourceLocation("item/empty_slot_pickaxe"),
                            new ResourceLocation("item/empty_slot_axe"),
                            new ResourceLocation("item/empty_slot_shovel"),
                            new ResourceLocation("item/empty_slot_hoe")
                    ),
                    List.of(new ResourceLocation("item/empty_slot_ingot"))
            ));
    public static final DeferredItem<Item> DUCK_FEATHER = register("duck_feather");
    public static final DeferredItem<Item> COPPER_TABLET = register("copper_tablet");
    public static final DeferredItem<Item> COPPER_NUGGET = register("copper_nugget");
    public static final DeferredItem<Item> EXPERIENCE_BERRY = register("experience_berry", ExperienceBerryItem::new);
    public static final DeferredItem<Item> MANGANESE_INGOT = register("manganese_ingot");
    public static final DeferredItem<Item> MANGANESE_NUGGET = register("manganese_nugget");
    public static final DeferredItem<Item> STEEL_INGOT = register("steel_ingot");
    public static final DeferredItem<Item> STEEL_NUGGET = register("steel_nugget");
    public static final DeferredItem<Item> PLASTIC_SHEET = register("plastic_sheet");
    public static final DeferredItem<Item> ENDER_PAPER = register("ender_paper", EnderPaper::new);
    public static final DeferredItem<Item> RAW_MANGANESE = register("raw_manganese");
    public static final DeferredItem<Item> COAL_DUST = register("coal_dust");
    public static final DeferredItem<Item> CHARCOAL_DUST = register("charcoal_dust");
    public static final DeferredItem<Item> SUGAR_CHARCOAL_DUST = register("sugar_charcoal_dust");
    public static final DeferredItem<Item> CLAY_DUST = register("clay_dust");
    public static final DeferredItem<Item> ARDITE_INGOT = register("ardite_ingot");
    public static final DeferredItem<Item> RAW_ARDITE = register("raw_ardite");
    public static final DeferredItem<Item> TAR_BALL = register("tar_ball");
    public static final DeferredItem<Item> BITUMEN = register("bitumen");
    public static final DeferredItem<Item> PINK_SLIME_INGOT = register("pink_slime_ingot");
    public static final DeferredItem<Item> DEMON_INGOT = register("demon_ingot", p -> new Item(p.fireResistant()));
    public static final DeferredItem<Item> DEMONIC_TABLET = register("demonic_tablet");

    public static final DeferredItem<Item> DEMON_EYE_SPAWN_EGG = register("demon_eye_spawn_egg",
            ()-> new DeferredSpawnEggItem(ModEntities.DEMON_EYE, 0xDDA4A4, 0x721212, new Item.Properties()));
    public static final DeferredItem<Item> PINKY_SPAWN_EGG = register("pinky_spawn_egg",
            ()-> new DeferredSpawnEggItem(ModEntities.PINKY, 0xE8B3E2, 0xC062B3, new Item.Properties()));
    public static final DeferredItem<Item> MAGICAL_BOOK_SPAWN_EGG = register("magical_book_spawn_egg",
            ()-> new DeferredSpawnEggItem(ModEntities.MAGICAL_BOOK, 0xe9b115, 0x752802, new Item.Properties()));
    public static final DeferredItem<Item> DUCK_SPAWN_EGG = register("duck_spawn_egg",
            ()-> new DeferredSpawnEggItem(ModEntities.DUCK, 0x126700, 0xF2691B, new Item.Properties()));

    //Actual artifacts.
    public static final DeferredItem<Item> HOLY_DAGGER = registerStacksToOne("holy_dagger", HolyDaggerItem::new);
    public static final DeferredItem<Item> MEDKIT = registerStacksToOne("medkit", MedkitItem::new);
    public static final DeferredItem<Item> FROG_LEGS = registerStacksToOne("frog_legs", FrogLegsItem::new);
    public static final DeferredItem<Item> MAGIC_FEATHER = registerStacksToOne("magic_feather", MagicFeatherItem::new);
    public static final DeferredItem<Item> ENCUMBATOR = registerStacksToOne("encumbator", EncumbatorItem::new);
    public static final DeferredItem<Item> ENDER_POUCH = registerStacksToOne("ender_pouch", EnderPouchItem::new);
    public static final DeferredItem<Item> PORTABLE_CRAFTER = registerStacksToOne("portable_crafter", PortableCrafterItem::new);
    public static final DeferredItem<Item> COMFY_SHOES = registerStacksToOne("comfy_shoes", ComfyShoesItem::new);
    public static final DeferredItem<Item> ENDER_NOTEBOOK = registerStacksToOne("ender_notebook", EnderNotebookItem::new);

    //Tools.
    public static final DeferredItem<Item> COPPER_SWORD = registerStacksToOne("copper_sword", p -> new SwordItem(ModTiers.COPPER, 3, -2.4f, p));
    public static final DeferredItem<Item> COPPER_PICKAXE = registerStacksToOne("copper_pickaxe", p -> new PickaxeItem(ModTiers.COPPER, 1, -2.8f, p));
    public static final DeferredItem<Item> COPPER_AXE = registerStacksToOne("copper_axe", p -> new AxeItem(ModTiers.COPPER, 7, -3.2f, p));
    public static final DeferredItem<Item> COPPER_SHOVEL = registerStacksToOne("copper_shovel", p -> new ShovelItem(ModTiers.COPPER, 1.5F, -3.0f, p));
    public static final DeferredItem<Item> COPPER_HOE = registerStacksToOne("copper_hoe", p -> new HoeItem(ModTiers.COPPER, -1, -2.0f, p));
    public static final DeferredItem<Item> SWORD_OF_THE_GREEN_EARTH = registerStacksToOne("sword_of_the_green_earth", SwordOfTheGreenEarthItem::new);
    public static final DeferredItem<Item> CHLOROVEIN_PICKAXE = registerStacksToOne("chlorovein_pickaxe", ChloroveinPickaxeItem::new);
    public static final DeferredItem<Item> AXE_OF_REGROWTH = registerStacksToOne("axe_of_regrowth", AxeOfRegrowthItem::new);
    public static final DeferredItem<Item> NATURAL_SPADE = registerStacksToOne("natural_spade", NaturalSpadeItem::new);
    public static final DeferredItem<Item> LUSH_GRUBBER = registerStacksToOne("lush_grubber", LushGrubberItem::new);
    public static final DeferredItem<Item> WRENCH = registerStacksToOne("wrench", WrenchItem::new);
    public static final DeferredItem<Item> KNAPSACK = registerStacksToOne("knapsack", KnapsackItem::new);
    public static final DeferredItem<Item> WOODEN_HAMMER = registerStacksToOne("wooden_hammer", p -> new HammerItem(Tiers.WOOD, p));
    public static final DeferredItem<Item> STONE_HAMMER = registerStacksToOne("stone_hammer", p -> new HammerItem(Tiers.STONE, p));
    public static final DeferredItem<Item> COPPER_HAMMER = registerStacksToOne("copper_hammer", p -> new HammerItem(ModTiers.COPPER, p));
    public static final DeferredItem<Item> IRON_HAMMER = registerStacksToOne("iron_hammer", p -> new HammerItem(Tiers.IRON, p));
    public static final DeferredItem<Item> GOLDEN_HAMMER = registerStacksToOne("golden_hammer", p -> new HammerItem(Tiers.GOLD, p));
    public static final DeferredItem<Item> DIAMOND_HAMMER = registerStacksToOne("diamond_hammer", p -> new HammerItem(Tiers.DIAMOND, p));
    public static final DeferredItem<Item> NETHERITE_HAMMER = registerStacksToOne("netherite_hammer", p -> new HammerItem(Tiers.NETHERITE, p.fireResistant()));
    public static final DeferredItem<Item> HAMMAXE = registerStacksToOne("hammaxe", HammaxeItem::new);
    public static final DeferredItem<Item> GLASSCUTTER = registerStacksToOne("glasscutter", GlasscutterItem::new);
    public static final DeferredItem<Item> WITHERING_SWORD = register("withering_sword", WitheringSword::new);
    public static final DeferredItem<Item> FEEDING_CANISTER = registerStacksToOne("feeding_canister", FeedingCanister::new);

    public static final DeferredItem<RetexturedBlockItem> GARDENING_POT = register("gardening_pot", ()-> new GardeningPotBlockItem(ModTags.Items.GARDENING_POT_SUITABLE, new Item.Properties()));
    public static final DeferredItem<RetexturedBlockItem> FANCY_CHEST = register("fancy_chest", ()-> new FancyChestBlockItem(ModTags.Items.GARDENING_POT_SUITABLE, new Item.Properties()));
    public static final DeferredItem<JarBlockItem> JAR = register("jar", JarBlockItem::new);

    public static final DeferredItem<Item> ANCIENT_SEED = register("ancient_seed",
            () -> new ItemNameBlockItem(ModBlocks.ANCIENT_CROP.get(), new Item.Properties()));
    public static final DeferredItem<DoubleHighBlockItem> FORSYTHIA = register("forsythia", ()-> new DoubleHighBlockItem(ModBlocks.FORSYTHIA.get(), new Item.Properties()));

    private static <T extends Item> DeferredItem<T> register(String name, Supplier<T> supplier) {
        return ITEMS.register(name, supplier);
    }

    private static DeferredItem<Item> register(String name) {
        return register(name, ()-> new Item(new Item.Properties()));
    }

    private static <T extends Item> DeferredItem<T> register(String name, Function<Item.Properties, T> func) {
        return register(name, ()-> func.apply(new Item.Properties()));
    }

    private static <T extends Item> DeferredItem<T> register(String name, Function<Item.Properties, T> func, Item.Properties props) {
        return props == null ? register(name, func) : ITEMS.register(name, ()-> func.apply(props));
    }

    private static DeferredItem<Item> registerStacksToOne(String name) {
        return registerStacksToOne(name, Item::new);
    }

    private static <T extends Item> DeferredItem<T> registerStacksToOne(String name, Function<Item.Properties, T> func) {
        return register(name, ()-> func.apply(new Item.Properties().stacksTo(1)));
    }
}
