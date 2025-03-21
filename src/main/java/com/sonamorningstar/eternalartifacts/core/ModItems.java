package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.block.BewlrStandingAndWallBlockItem;
import com.sonamorningstar.eternalartifacts.content.item.*;
import com.sonamorningstar.eternalartifacts.content.item.base.AnimatedSpellTomeItem;
import com.sonamorningstar.eternalartifacts.content.item.base.SpellTomeItem;
import com.sonamorningstar.eternalartifacts.content.item.block.FancyChestBlockItem;
import com.sonamorningstar.eternalartifacts.content.item.block.GardeningPotBlockItem;
import com.sonamorningstar.eternalartifacts.content.item.block.JarBlockItem;
import com.sonamorningstar.eternalartifacts.content.item.block.TesseractItem;
import com.sonamorningstar.eternalartifacts.content.item.block.base.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.content.spell.*;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<Item> ORANGE = register("orange", p -> new Item(p.food(ModFoods.ORANGE)));
    public static final DeferredItem<Item> BANANA = register("banana", p -> new Item(p.food(ModFoods.BANANA)));
    public static final DeferredItem<Item> ANCIENT_FRUIT = register("ancient_fruit", p -> new Item(p.food(ModFoods.ANCIENT_FRUIT)));
    public static final DeferredItem<Item> RAW_MEAT_INGOT = register("meat_ingot", p -> new Item(p.food(ModFoods.MEAT_INGOT)));
    public static final DeferredItem<Item> COOKED_MEAT_INGOT = register("cooked_meat_ingot", p -> new Item(p.food(ModFoods.COOKED_MEAT_INGOT)));
    public static final DeferredItem<Item> APPLE_PIE = register("apple_pie", p -> new Item(p.food(ModFoods.APPLE_PIE)));
    public static final DeferredItem<Item> BANANA_CREAM_PIE = register("banana_cream_pie", p -> new Item(p.food(ModFoods.BANANA_CREAM_PIE)));
    public static final DeferredItem<Item> DUCK_MEAT = register("duck_meat", p -> new Item(p.food(ModFoods.DUCK_MEAT)));
    public static final DeferredItem<Item> COOKED_DUCK_MEAT = register("cooked_duck_meat", p -> new Item(p.food(ModFoods.COOKED_DUCK_MEAT)));
    public static final DeferredItem<Item> GOLDEN_ANCIENT_FRUIT = register("golden_ancient_fruit", p -> new GoldenAncientFruitItem(p.rarity(Rarity.RARE).food(ModFoods.GOLDEN_ANCIENT_FRUIT)));
    public static final DeferredItem<Item> ENCHANTED_GOLDEN_ANCIENT_FRUIT = register("enchanted_golden_ancient_fruit", p -> new EnchantedGoldenAncientFruitItem(p.rarity(Rarity.EPIC).food(ModFoods.ENCHANTED_GOLDEN_ANCIENT_FRUIT)));
    public static final DeferredItem<Item> BATTERY = registerStacksToOne("battery", BatteryItem::new);
    public static final DeferredItem<Item> CAPACITOR = register("capacitor");
    public static final DeferredItem<Item> LENS = register("lens");
    public static final DeferredItem<Item> PLANT_MATTER = register("plant_matter");
    public static final DeferredItem<Item> PINK_SLIME = register("pink_slime");
    public static final DeferredItem<Item> GOLD_RING = registerStacksToOne("gold_ring", GoldRingItem::new);
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
                    List.of(new ResourceLocation("item/empty_slot_sword"),
                            new ResourceLocation("item/empty_slot_pickaxe"),
                            new ResourceLocation("item/empty_slot_axe"),
                            new ResourceLocation("item/empty_slot_shovel"),
                            new ResourceLocation("item/empty_slot_hoe")),
                    List.of(new ResourceLocation("item/empty_slot_ingot"))));
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
    public static final DeferredItem<Item> MARIN_INGOT = register("marin_ingot");
    public static final DeferredItem<Item> RAW_MARIN = register("raw_marin");
    public static final DeferredItem<Item> TAR_BALL = register("tar_ball");
    public static final DeferredItem<Item> BITUMEN = register("bitumen");
    public static final DeferredItem<Item> PINK_SLIME_INGOT = register("pink_slime_ingot");
    public static final DeferredItem<Item> DEMON_INGOT = register("demon_ingot", p -> new Item(p.fireResistant()));
    public static final DeferredItem<Item> DEMONIC_TABLET = register("demonic_tablet", p -> new Item(p.fireResistant()));
    public static final DeferredItem<Item> SLOT_LOCK = registerStacksToOne("slot_lock");
    public static final DeferredItem<Item> FLOUR = register("flour");
    public static final DeferredItem<Item> DOUGH = register("dough");
    public static final DeferredItem<Item> BANANA_BREAD = register("banana_bread", p -> new Item(p.food(ModFoods.BANANA_BREAD)));
    public static final DeferredItem<Item> BLUEPRINT = register("blueprint", BlueprintItem::new);
    public static final DeferredItem<Item> WHITE_SHULKER_SHELL = register("white_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.WHITE));
    public static final DeferredItem<Item> ORANGE_SHULKER_SHELL = register("orange_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.ORANGE));
    public static final DeferredItem<Item> MAGENTA_SHULKER_SHELL = register("magenta_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.MAGENTA));
    public static final DeferredItem<Item> LIGHT_BLUE_SHULKER_SHELL = register("light_blue_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.LIGHT_BLUE));
    public static final DeferredItem<Item> YELLOW_SHULKER_SHELL = register("yellow_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.YELLOW));
    public static final DeferredItem<Item> LIME_SHULKER_SHELL = register("lime_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.LIME));
    public static final DeferredItem<Item> PINK_SHULKER_SHELL = register("pink_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.PINK));
    public static final DeferredItem<Item> GRAY_SHULKER_SHELL = register("gray_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.GRAY));
    public static final DeferredItem<Item> LIGHT_GRAY_SHULKER_SHELL = register("light_gray_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.LIGHT_GRAY));
    public static final DeferredItem<Item> CYAN_SHULKER_SHELL = register("cyan_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.CYAN));
    public static final DeferredItem<Item> PURPLE_SHULKER_SHELL = register("purple_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.PURPLE));
    public static final DeferredItem<Item> BLUE_SHULKER_SHELL = register("blue_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.BLUE));
    public static final DeferredItem<Item> BROWN_SHULKER_SHELL = register("brown_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.BROWN));
    public static final DeferredItem<Item> GREEN_SHULKER_SHELL = register("green_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.GREEN));
    public static final DeferredItem<Item> RED_SHULKER_SHELL = register("red_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.RED));
    public static final DeferredItem<Item> BLACK_SHULKER_SHELL = register("black_shulker_shell", p -> new ColoredShulkerShellItem(p, DyeColor.BLACK));
    public static final DeferredItem<Item> ANGELIC_HEART = register("angelic_heart", p -> new AngelicHeartItem(p.food(ModFoods.ANGELIC_HEART)));
    public static final DeferredItem<Item> GREEN_APPLE = register("green_apple", p -> new Item(p.food(ModFoods.GREEN_APPLE)));
    public static final DeferredItem<Item> YELLOW_APPLE = register("yellow_apple", p -> new Item(p.food(ModFoods.YELLOW_APPLE)));
    
    public static final DeferredItem<DeferredSpawnEggItem> DEMON_EYE_SPAWN_EGG = registerSpawnEgg("demon_eye_spawn_egg", ModEntities.DEMON_EYE, 0xDDA4A4, 0x721212);
    public static final DeferredItem<DeferredSpawnEggItem> PINKY_SPAWN_EGG = registerSpawnEgg("pinky_spawn_egg", ModEntities.PINKY, 0xE8B3E2, 0xC062B3);
    public static final DeferredItem<DeferredSpawnEggItem> MAGICAL_BOOK_SPAWN_EGG = registerSpawnEgg("magical_book_spawn_egg", ModEntities.MAGICAL_BOOK, 0xe9b115, 0x752802);
    public static final DeferredItem<DeferredSpawnEggItem> DUCK_SPAWN_EGG = registerSpawnEgg("duck_spawn_egg", ModEntities.DUCK, 0x126700, 0xF2691B);
    public static final DeferredItem<DeferredSpawnEggItem> CHARGED_SHEEP_SPAWN_EGG = registerSpawnEgg("charged_sheep_spawn_egg", ModEntities.CHARGED_SHEEP, 0xF8D0FF, 0xFFA1D7);

    //Actual artifacts.
    public static final DeferredItem<Item> HOLY_DAGGER = registerStacksToOne("holy_dagger", HolyDaggerItem::new);
    public static final DeferredItem<Item> MEDKIT = registerStacksToOne("medkit", MedkitItem::new);
    public static final DeferredItem<Item> FROG_LEGS = registerStacksToOne("frog_legs", FrogLegsItem::new);
    public static final DeferredItem<Item> MAGIC_FEATHER = registerStacksToOne("magic_feather", MagicFeatherItem::new);
    public static final DeferredItem<Item> ENCUMBATOR = registerStacksToOne("encumbator", EncumbatorItem::new);
    public static final DeferredItem<Item> ENDER_KNAPSACK = registerStacksToOne("ender_knapsack", EnderKnapsackItem::new);
    public static final DeferredItem<Item> PORTABLE_CRAFTER = registerStacksToOne("portable_crafter", PortableCrafterItem::new);
    public static final DeferredItem<Item> COMFY_SHOES = registerStacksToOne("comfy_shoes", ComfyShoesItem::new);
    public static final DeferredItem<Item> ENDER_NOTEBOOK = registerStacksToOne("ender_notebook", EnderNotebookItem::new);
    public static final DeferredItem<SpellTomeItem<EvokerFangsSpell>> EVOKERS_TOME = registerTome("evokers_tome", ModSpells.EVOKER_FANGS);
    public static final DeferredItem<AnimatedSpellTomeItem<FireballSpell>> FIREBALL_TOME = registerAnimatedTome("fireball_tome", ModSpells.FIREBALL);
    public static final DeferredItem<SpellTomeItem<TornadoSpell>> TORNADO_TOME = registerTome("tornado_tome", ModSpells.TORNADO);
    public static final DeferredItem<SpellTomeItem<ShulkerBulletsSpell>> SHULKER_BULLETS_TOME = registerTome("shulker_bullets_tome", ModSpells.SHULKER_BULLETS);
    public static final DeferredItem<SpellTomeItem<MeteoriteSpell>> METEORITE_TOME = registerTome("meteorite_tome", ModSpells.METEORITE);
    public static final DeferredItem<Item> POWER_GAUNTLET = registerStacksToOne("power_gauntlet");
    public static final DeferredItem<Item> HEART_NECKLACE = registerStacksToOne("heart_necklace");
    public static final DeferredItem<Item> SAGES_TALISMAN = registerStacksToOne("sages_talisman");
    public static final DeferredItem<Item> BAND_OF_ARCANE = registerStacksToOne("band_of_arcane");
    public static final DeferredItem<Item> EMERALD_SIGNET = registerStacksToOne("emerald_signet");
    public static final DeferredItem<Item> SKYBOUND_TREADS = registerStacksToOne("skybound_treads");
    public static final DeferredItem<Item> GALE_SASH = registerStacksToOne("gale_sash");

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
    public static final DeferredItem<Item> WRENCH = registerStacksToOne("wrench", p -> new WrenchItem(Tiers.IRON, p));
    public static final DeferredItem<Item> KNAPSACK = registerStacksToOne("knapsack", KnapsackItem::new);
    public static final DeferredItem<Item> WOODEN_HAMMER = registerStacksToOne("wooden_hammer", p -> new HammerItem(Tiers.WOOD, p));
    public static final DeferredItem<Item> STONE_HAMMER = registerStacksToOne("stone_hammer", p -> new HammerItem(Tiers.STONE, p));
    public static final DeferredItem<Item> COPPER_HAMMER = registerStacksToOne("copper_hammer", p -> new HammerItem(ModTiers.COPPER, p));
    public static final DeferredItem<Item> IRON_HAMMER = registerStacksToOne("iron_hammer", p -> new HammerItem(Tiers.IRON, p));
    public static final DeferredItem<Item> GOLDEN_HAMMER = registerStacksToOne("golden_hammer", p -> new HammerItem(Tiers.GOLD, p));
    public static final DeferredItem<Item> DIAMOND_HAMMER = registerStacksToOne("diamond_hammer", p -> new HammerItem(Tiers.DIAMOND, p));
    public static final DeferredItem<Item> NETHERITE_HAMMER = registerStacksToOne("netherite_hammer", p -> new HammerItem(Tiers.NETHERITE, p.fireResistant()));
    public static final DeferredItem<Item> HAMMAXE = registerStacksToOne("hammaxe", p -> new HammaxeItem(ModTiers.CHLOROPHYTE, p));
    public static final DeferredItem<Item> GLASSCUTTER = registerStacksToOne("glasscutter", p -> new GlasscutterItem(Tiers.IRON, p));
    public static final DeferredItem<Item> WITHERING_SWORD = register("withering_sword", WitheringSword::new);
    public static final DeferredItem<Item> FEEDING_CANISTER = registerStacksToOne("feeding_canister", FeedingCanister::new);
    public static final DeferredItem<Item> GRAFTER = registerStacksToOne("grafter", p -> new GrafterItem(ModTiers.COPPER, p));
    public static final DeferredItem<Item> TANK_KNAPSACK = registerStacksToOne("tank_knapsack", TankKnapsackItem::new);
    public static final DeferredItem<Item> WOODEN_CUTLASS = registerStacksToOne("wooden_cutlass", p -> new CutlassItem(Tiers.WOOD, p));
    public static final DeferredItem<Item> STONE_CUTLASS = registerStacksToOne("stone_cutlass", p -> new CutlassItem(Tiers.STONE, p));
    public static final DeferredItem<Item> COPPER_CUTLASS = registerStacksToOne("copper_cutlass", p -> new CutlassItem(ModTiers.COPPER, p));
    public static final DeferredItem<Item> IRON_CUTLASS = registerStacksToOne("iron_cutlass", p -> new CutlassItem(Tiers.IRON, p));
    public static final DeferredItem<Item> GOLDEN_CUTLASS = registerStacksToOne("golden_cutlass", p -> new CutlassItem(Tiers.GOLD, p));
    public static final DeferredItem<Item> DIAMOND_CUTLASS = registerStacksToOne("diamond_cutlass", p -> new CutlassItem(Tiers.DIAMOND, p));
    public static final DeferredItem<Item> NETHERITE_CUTLASS = registerStacksToOne("netherite_cutlass", p -> new CutlassItem(Tiers.NETHERITE, p.fireResistant()));
    public static final DeferredItem<Item> CHLOROPHYTE_CUTLASS = registerStacksToOne("chlorophyte_cutlass", p -> new CutlassItem(ModTiers.CHLOROPHYTE, p));
    public static final DeferredItem<Item> LIGHTSABER = register("lightsaber", () -> new LightSaberItem(ModTiers.CHLOROPHYTE));
    public static final DeferredItem<Item> WOODEN_SICKLE = registerStacksToOne("wooden_sickle", p -> new SickleItem(Tiers.WOOD, p));
    public static final DeferredItem<Item> STONE_SICKLE = registerStacksToOne("stone_sickle", p -> new SickleItem(Tiers.STONE, p));
    public static final DeferredItem<Item> COPPER_SICKLE = registerStacksToOne("copper_sickle", p -> new SickleItem(ModTiers.COPPER, p));
    public static final DeferredItem<Item> IRON_SICKLE = registerStacksToOne("iron_sickle", p -> new SickleItem(Tiers.IRON, p));
    public static final DeferredItem<Item> GOLDEN_SICKLE = registerStacksToOne("golden_sickle", p -> new SickleItem(Tiers.GOLD, p));
    public static final DeferredItem<Item> DIAMOND_SICKLE = registerStacksToOne("diamond_sickle", p -> new SickleItem(Tiers.DIAMOND, p));
    public static final DeferredItem<Item> NETHERITE_SICKLE = registerStacksToOne("netherite_sickle", p -> new SickleItem(Tiers.NETHERITE, p.fireResistant()));
    public static final DeferredItem<Item> CHLOROPHYTE_SICKLE = registerStacksToOne("chlorophyte_sickle", p -> new SickleItem(ModTiers.CHLOROPHYTE, p));
    public static final DeferredItem<Item> CHISEL = registerStacksToOne("chisel", p -> new ChiselItem(Tiers.DIAMOND, p));
    public static final DeferredItem<Item> SHULKER_HELMET = registerStacksToOne("shulker_helmet", p -> new ShulkerArmorItem(ArmorItem.Type.HELMET, p));
    public static final DeferredItem<Item> SHULKER_CHESTPLATE = registerStacksToOne("shulker_chestplate", p -> new ShulkerArmorItem(ArmorItem.Type.CHESTPLATE, p));
    public static final DeferredItem<Item> SHULKER_LEGGINGS = registerStacksToOne("shulker_leggings", p -> new ShulkerArmorItem(ArmorItem.Type.LEGGINGS, p));
    public static final DeferredItem<Item> SHULKER_BOOTS = registerStacksToOne("shulker_boots", p -> new ShulkerArmorItem(ArmorItem.Type.BOOTS, p));
    public static final DeferredItem<Item> PORTABLE_BATTERY = registerStacksToOne("portable_battery", PortableBatteryItem::new);
    public static final DeferredItem<Item> CONFIGURATION_DRIVE = registerStacksToOne("configuration_drive", ConfigurationDriveItem::new);
    public static final DeferredItem<Item> STEEL_SWORD = registerStacksToOne("steel_sword", p -> new SwordItem(ModTiers.STEEL, 3, -2.4f, p));
    public static final DeferredItem<Item> STEEL_PICKAXE = registerStacksToOne("steel_pickaxe", p -> new PickaxeItem(ModTiers.STEEL, 1, -2.8f, p));
    public static final DeferredItem<Item> STEEL_AXE = registerStacksToOne("steel_axe", p -> new AxeItem(ModTiers.STEEL, 7, -3.2f, p));
    public static final DeferredItem<Item> STEEL_SHOVEL = registerStacksToOne("steel_shovel", p -> new ShovelItem(ModTiers.STEEL, 1.5F, -3.0f, p));
    public static final DeferredItem<Item> STEEL_HOE = registerStacksToOne("steel_hoe", p -> new HoeItem(ModTiers.STEEL, -1, -2.0f, p));
    public static final DeferredItem<Item> STEEL_HAMMER = registerStacksToOne("steel_hammer", p -> new HammerItem(ModTiers.STEEL, p));
    public static final DeferredItem<Item> STEEL_SICKLE = registerStacksToOne("steel_sickle", p -> new SickleItem(ModTiers.STEEL, p));
    public static final DeferredItem<Item> STEEL_CUTLASS = registerStacksToOne("steel_cutlass", p -> new CutlassItem(ModTiers.STEEL, p));
    public static final DeferredItem<Item> STEEL_HELMET = registerStacksToOne("steel_helmet", p -> new ArmorItem(ModArmorMaterials.STEEL, ArmorItem.Type.HELMET, p));
    public static final DeferredItem<Item> STEEL_CHESTPLATE = registerStacksToOne("steel_chestplate", p -> new ArmorItem(ModArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE, p));
    public static final DeferredItem<Item> STEEL_LEGGINGS = registerStacksToOne("steel_leggings", p -> new ArmorItem(ModArmorMaterials.STEEL, ArmorItem.Type.LEGGINGS, p));
    public static final DeferredItem<Item> STEEL_BOOTS = registerStacksToOne("steel_boots", p -> new ArmorItem(ModArmorMaterials.STEEL, ArmorItem.Type.BOOTS, p));
    public static final DeferredItem<Item> SPAWNER_EXTRACTOR = registerStacksToOne("spawner_extractor", SpawnerExtractorItem::new);
    public static final DeferredItem<Item> BONE_SWORD = registerStacksToOne("bone_sword", p -> new SwordItem(ModTiers.BONE, 3, -2.4f, p));
    //public static final DeferredItem<Item> BONE_PICKAXE = registerStacksToOne("bone_pickaxe", p -> new PickaxeItem(ModTiers.BONE, 1, -2.8f, p));
    //public static final DeferredItem<Item> BONE_AXE = registerStacksToOne("bone_axe", p -> new AxeItem(ModTiers.BONE, 7, -3.2f, p));
    //public static final DeferredItem<Item> BONE_SHOVEL = registerStacksToOne("bone_shovel", p -> new ShovelItem(ModTiers.BONE, 1.5F, -3.0f, p));
    //public static final DeferredItem<Item> BONE_HOE = registerStacksToOne("bone_hoe", p -> new HoeItem(ModTiers.BONE, -1, -2.0f, p));
    //public static final DeferredItem<Item> BONE_HAMMER = registerStacksToOne("bone_hammer", p -> new HammerItem(ModTiers.BONE, p));
    //public static final DeferredItem<Item> BONE_SICKLE = registerStacksToOne("bone_sickle", p -> new SickleItem(ModTiers.BONE, p));
    //public static final DeferredItem<Item> BONE_CUTLASS = registerStacksToOne("bone_cutlass", p -> new CutlassItem(ModTiers.BONE, p));
    
    public static final DeferredItem<RetexturedBlockItem> GARDENING_POT = register("gardening_pot", ()-> new GardeningPotBlockItem(ModTags.Items.GARDENING_POT_SUITABLE, new Item.Properties()));
    public static final DeferredItem<RetexturedBlockItem> FANCY_CHEST = register("fancy_chest", ()-> new FancyChestBlockItem(ModTags.Items.GARDENING_POT_SUITABLE, new Item.Properties()));
    public static final DeferredItem<JarBlockItem> JAR = register("jar", JarBlockItem::new);
    public static final DeferredItem<TesseractItem> TESSERACT = register("tesseract", TesseractItem::new);

    public static final DeferredItem<Item> ANCIENT_SEED = register("ancient_seed",
            () -> new ItemNameBlockItem(ModBlocks.ANCIENT_CROP.get(), new Item.Properties()));
    
    public static final DeferredItem<Item> DROWNED_HEAD = register("drowned_head", () ->
        new BewlrStandingAndWallBlockItem(ModBlocks.DROWNED_HEAD.get(), ModBlocks.DROWNED_WALL_HEAD.get(), new Item.Properties().rarity(Rarity.UNCOMMON), Direction.DOWN)
    );
    public static final DeferredItem<Item> HUSK_HEAD = register("husk_head", () ->
        new BewlrStandingAndWallBlockItem(ModBlocks.HUSK_HEAD.get(), ModBlocks.HUSK_WALL_HEAD.get(), new Item.Properties().rarity(Rarity.UNCOMMON), Direction.DOWN)
    );
    public static final DeferredItem<Item> STRAY_SKULL = register("stray_skull", () ->
        new BewlrStandingAndWallBlockItem(ModBlocks.STRAY_SKULL.get(), ModBlocks.STRAY_WALL_SKULL.get(), new Item.Properties().rarity(Rarity.UNCOMMON), Direction.DOWN)
    );
    public static final DeferredItem<Item> BLAZE_HEAD = register("blaze_head", () ->
        new BewlrStandingAndWallBlockItem(ModBlocks.BLAZE_HEAD.get(), ModBlocks.BLAZE_WALL_HEAD.get(), new Item.Properties().rarity(Rarity.UNCOMMON), Direction.DOWN)
    );
    
    //region Register methods.
    private static <T extends Item> DeferredItem<T> register(String name, Supplier<T> supplier) {
        return ITEMS.register(name, supplier);
    }
    private static DeferredItem<Item> register(String name) {
        return register(name, ()-> new Item(new Item.Properties()));
    }
    private static <T extends Item> DeferredItem<T> register(String name, Function<Item.Properties, T> func) {
        return register(name, ()-> func.apply(new Item.Properties()));
    }

    private static DeferredItem<Item> registerStacksToOne(String name) {
        return registerStacksToOne(name, Item::new);
    }
    private static <T extends Item> DeferredItem<T> registerStacksToOne(String name, Function<Item.Properties, T> func) {
        return register(name, ()-> func.apply(new Item.Properties().stacksTo(1)));
    }

    private static DeferredItem<DeferredSpawnEggItem> registerSpawnEgg(String name, Supplier<? extends EntityType<? extends Mob>> type, int background, int highlight) {
        return register(name, ()-> new DeferredSpawnEggItem(type, background, highlight, new Item.Properties()));
    }

    private static <S extends Spell> DeferredItem<SpellTomeItem<S>> registerTome(String name, DeferredHolder<Spell, S> spellHolder) {
        return register(name, () -> new SpellTomeItem<>(spellHolder, new Item.Properties().stacksTo(1)));
    }
    private static <S extends Spell> DeferredItem<AnimatedSpellTomeItem<S>> registerAnimatedTome(String name, DeferredHolder<Spell, S> spellHolder) {
        return register(name, () -> new AnimatedSpellTomeItem<>(spellHolder, new Item.Properties().stacksTo(1)));
    }
    //endregion
}
