package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.item.*;
import com.sonamorningstar.eternalartifacts.content.item.block.GardeningPotBlockItem;
import com.sonamorningstar.eternalartifacts.util.BlockEntityHelper;
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

    public static final DeferredItem<Item> ORANGE = register("orange", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(1f).build()));
    public static final DeferredItem<Item> ANCIENT_FRUIT = register("ancient_fruit", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(1.5f).build()));
    public static final DeferredItem<Item> BATTERY = register("battery", Item::new);
    public static final DeferredItem<Item> CAPACITOR = register("capacitor", Item::new);

    public static final DeferredItem<Item> LENS = register("lens", Item::new);
    public static final DeferredItem<Item> PLANT_MATTER = register("plant_matter", Item::new);

    public static final DeferredItem<Item> DEMON_EYE_SPAWN_EGG = register("demon_eye_spawn_egg",
            ()-> new DeferredSpawnEggItem(ModEntities.DEMON_EYE, 0xDDA4A4, 0x721212, new Item.Properties()));

    //Actual artifacts.
    public static final DeferredItem<Item> HOLY_DAGGER = registerArtifact("holy_dagger", HolyDaggerItem::new);
    public static final DeferredItem<Item> MEDKIT = registerArtifact("medkit", MedkitItem::new);
    public static final DeferredItem<Item> FROG_LEGS = registerArtifact("frog_legs", Item::new);
    public static final DeferredItem<Item> MAGIC_FEATHER = registerArtifact("magic_feather", Item::new);
    public static final DeferredItem<Item> GOLD_RING = registerArtifact("gold_ring", Item::new);
    public static final DeferredItem<Item> ENCUMBATOR = registerArtifact("encumbator", EncumbatorItem::new);

    //Tools.
    public static final DeferredItem<Item> AXE_OF_REGROWTH = register("axe_of_regrowth", AxeOfRegrowthItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredHolder<Item, BucketItem> NOUS_BUCKET = register("nous_bucket",
            p -> new BucketItem(ModFluids.NOUS_SOURCE::value, p.stacksTo(1).craftRemainder(Items.BUCKET)));

    public static final DeferredItem<RetexturedBlockItem> GARDENING_POT = register("gardening_pot", ()-> new GardeningPotBlockItem(ModTags.Items.GARDENING_POT_SUITABLE, new Item.Properties()));

    public static final DeferredItem<Item> ANCIENT_SEED = register("ancient_seed",
            () -> new ItemNameBlockItem(ModBlocks.ANCIENT_CROP.get(), new Item.Properties()));

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
