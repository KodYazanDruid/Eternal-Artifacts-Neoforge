package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class LanguageProvider extends net.neoforged.neoforge.common.data.LanguageProvider {
    String locale;
    public LanguageProvider(PackOutput output, String locale) {
        super(output, MODID, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        switch (locale) {
            case "en_us" -> {
                //Items
                add(ModItems.ORANGE.get(), "Orange");
                add(ModItems.NOUS_BUCKET.get(), "Nous Bucket");
                add(ModItems.BATTERY.get(), "Battery");
                add(ModItems.CAPACITOR.get(), "Capacitor");
                add(ModItems.LENS.get(), "Lens");
                add(ModItems.PLANT_MATTER.get(), "Plant Matter");
                add(ModItems.HOLY_DAGGER.get(), "Holy Dagger");
                add(ModItems.MEDKIT.get(), "Medkit");
                add(ModItems.FROG_LEGS.get(), "Frog Legs");
                add(ModItems.MAGIC_FEATHER.get(), "Magic Feather");
                add(ModItems.GOLD_RING.get(), "Gold Ring");
                add(ModItems.ENCUMBATOR.get(), "Encumbator");
                add(ModItems.ANCIENT_SEED.get(), "Ancient Seed");
                add(ModItems.ANCIENT_FRUIT.get(), "Ancient Fruit");
                add(ModItems.DEMON_EYE_SPAWN_EGG.get(), "Demon Eye Spawn Egg");
                add(ModItems.AXE_OF_REGROWTH.get(), "Axe of Regrowth");
                add(ModItems.RAW_MEAT_INGOT.get(), "Raw Meat Ingot");
                add(ModItems.MEAT_INGOT.get(), "Meat Ingot");
                add(ModItems.PINK_SLIME.get(), "Pink Slime");
                add(ModItems.PINKY_SPAWN_EGG.get(), "Pinky Spawn Egg");
                add(ModItems.ENDER_POUCH.get(), "Ender Pouch");
                add(ModItems.PORTABLE_CRAFTER.get(), "Portable Crafter");
                add(ModItems.GOLDEN_ANCIENT_FRUIT.get(), "Golden Ancient Fruit");
                add(ModItems.ENCHANTED_GOLDEN_ANCIENT_FRUIT.get(), "Enchanted Golden Ancient Fruit");
                add(ModItems.COMFY_SHOES.get(), "Comfy Shoes");
                add(ModItems.SUGAR_CHARCOAL.get(), "Sugar Charcoal");
                add(ModItems.BANANA.get(), "Banana");
                
                //Blocks
                //add(ModBlocks.LUTFI.get(), "Lutfi");
                add(ModBlocks.ANVILINATOR.get(), "Anvilinator");
                add(ModFluidTypes.NOUS.get().getDescriptionId(), "Nous");
                add(ModBlocks.BIOFURNACE.get(), "BioFurnace");
                add(ModBlocks.RESONATOR.get(), "Resonator");
                add(ModBlocks.GARDENING_POT.get(), "Gardening Pot");
                add(ModBlocks.FANCY_CHEST.get(), "Fancy Chest");
                add(ModBlocks.PINK_SLIME_BLOCK.get(), "Pink Slime Block");
                add(ModBlocks.ROSY_FROGLIGHT.get(), "Rosy Froglight");
                add(ModBlocks.FORSYTHIA.get(), "Forsythia");
                add(ModBlocks.MACHINE_BLOCK.get(), "Machine Block");
                add(ModBlocks.BOOK_DUPLICATOR.get(), "Book Duplicator");
                add(ModBlocks.SUGAR_CHARCOAL_BLOCK.get(), "Sugar Charcoal Block");
                add(ModBlocks.FOUR_LEAF_CLOVER.get(), "Four Leaf Clover");
                add(ModBlocks.GRAVEL_COAL_ORE.get(), "Gravel Coal Ore");
                add(ModBlocks.GRAVEL_COPPER_ORE.get(), "Gravel Copper Ore");
                add(ModBlocks.GRAVEL_IRON_ORE.get(), "Gravel Iron Ore");
                add(ModBlocks.GRAVEL_GOLD_ORE.get(), "Gravel Gold Ore");

                //Entities
                add(ModEntities.DEMON_EYE.get(), "Demon Eye");
                add(ModEntities.PINKY.get(), "Pinky");

                //Effects
                add(ModEffects.FLIGHT.get(), "Flight");
                add(ModEffects.DIVINE_PROTECTION.get(), "Divine Protection");

                //Misc.
                add("key." + MODID + ".anvilinator.enabled_naming", "Naming enabled.");
                add("key." + MODID + ".anvilinator.disabled_naming", "Naming disabled.");
                add("itemGroup." + MODID, "Eternal Artifacts");
                add(MODID + ".subtitle.holy_dagger_activate", "Holy dagger activated!");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("encumbator.active"), "Item pickup disabled!");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("encumbator.passive"), "Item pickup is normal.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("encumbator.switch"), "Right click in inventory to switch.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("inventory_right_click"), "You can right click in inventory to open.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.ofItem(ModItems.GARDENING_POT), "Grows crops faster and automatically harvests. If there is inventory under it, puts the items there, otherwise puts them on top of it in the world. Able to grow cacti and sugar cane.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("axe_of_regrowth_ench_text"), "Always has %s");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("magic_feather_active"), "Magic feather is active!");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("magic_feather_not_active"), "Magic feather is not active.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("golden_ancient_fruit"), "Gives two minutes of speed and haste.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("enchanted_golden_ancient_fruit"), "Gives two minues of flight and enhanced speed, haste and absorption.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("comfy_shoes"), "Gives step height when worn.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.ofItem(ModItems.FROG_LEGS), "Increases jumping height and decreases fall damage.");


            }
            case "tr_tr" -> {
                //Eşyalar
                add(ModItems.ORANGE.get(), "Portakal");
                add(ModItems.NOUS_BUCKET.get(), "İdrak Kovası");
                add(ModItems.BATTERY.get(), "Pil");
                add(ModItems.CAPACITOR.get(), "Kondansatör");
                add(ModItems.LENS.get(), "Lens");
                add(ModItems.PLANT_MATTER.get(), "Bitkisel Madde");
                add(ModItems.HOLY_DAGGER.get(), "Kutsal Hançer");
                add(ModItems.MEDKIT.get(), "Medkit");
                add(ModItems.FROG_LEGS.get(), "Kurbağa Bacakları");
                add(ModItems.MAGIC_FEATHER.get(), "Büyülü Tüy");
                add(ModItems.GOLD_RING.get(), "Altın Yüzük");
                add(ModItems.ENCUMBATOR.get(), "Yük Engelleyici");
                add(ModItems.ANCIENT_SEED.get(), "Antik Tohum");
                add(ModItems.ANCIENT_FRUIT.get(), "Antik Meyve");
                add(ModItems.DEMON_EYE_SPAWN_EGG.get(), "İblis Gözü Canlandırma Yumurtası");
                add(ModItems.AXE_OF_REGROWTH.get(), "Yeşertme Baltası");
                add(ModItems.RAW_MEAT_INGOT.get(), "Çiğ Et Külçesi");
                add(ModItems.MEAT_INGOT.get(), "Et Külçesi");
                add(ModItems.PINK_SLIME.get(), "Pembe Balçık");
                add(ModItems.PINKY_SPAWN_EGG.get(), "Pinky Çağırma Yumurtası");
                add(ModItems.ENDER_POUCH.get(), "Ender Çantası");
                add(ModItems.PORTABLE_CRAFTER.get(), "Taşınabilir Üretim Masası");
                add(ModItems.GOLDEN_ANCIENT_FRUIT.get(), "Altın Antik Meyve");
                add(ModItems.ENCHANTED_GOLDEN_ANCIENT_FRUIT.get(), "Büyülü Altın Antik Meyve");
                add(ModItems.COMFY_SHOES.get(), "Rahat Ayakkabılar");
                add(ModItems.SUGAR_CHARCOAL.get(), "Şeker Odun Kömürü");
                add(ModItems.BANANA.get(), "Muz");

                //Bloklar
                //add(ModBlocks.LUTFI.get(), "Lütfi");
                add(ModBlocks.ANVILINATOR.get(), "Örsinatör");
                add(ModFluidTypes.NOUS.get().getDescriptionId(), "İdrak");
                add(ModBlocks.BIOFURNACE.get(), "BiyoFırın");
                add(ModBlocks.RESONATOR.get(), "Yankılayıcı");
                add(ModBlocks.GARDENING_POT.get(), "Bahçe Saksısı");
                add(ModBlocks.FANCY_CHEST.get(), "Süslü Sandık");
                add(ModBlocks.PINK_SLIME_BLOCK.get(), "Pembe Balçık Bloğu");
                add(ModBlocks.ROSY_FROGLIGHT.get(), "Gülcük Kurbağa Işığı");
                add(ModBlocks.FORSYTHIA.get(), "Hor Çiçeği");
                add(ModBlocks.MACHINE_BLOCK.get(), "Makine Bloğu");
                add(ModBlocks.BOOK_DUPLICATOR.get(), "Kitap Çoğaltıcı");
                add(ModBlocks.SUGAR_CHARCOAL_BLOCK.get(), "Şeker Odun Kömürü Bloğu");
                add(ModBlocks.FOUR_LEAF_CLOVER.get(), "Dört Yapraklı Yonca");
                add(ModBlocks.GRAVEL_COAL_ORE.get(), "Çakılda Kömür Cevheri");
                add(ModBlocks.GRAVEL_COPPER_ORE.get(), "Çakılda Bakır Cevheri");
                add(ModBlocks.GRAVEL_IRON_ORE.get(), "Çakılda Demir Cevheri");
                add(ModBlocks.GRAVEL_GOLD_ORE.get(), "Çakılda Altın Cevheri");

                //Varlıklar
                add(ModEntities.DEMON_EYE.get(), "İblis Gözü");
                add(ModEntities.PINKY.get(), "Pembiş");

                //Efektler
                add(ModEffects.FLIGHT.get(), "Uçuş");
                add(ModEffects.DIVINE_PROTECTION.get(), "Kutsal Koruma");

                //Ivır Zıvır.
                add("key." + MODID + ".anvilinator.enabled_naming", "Adlandırma açık.");
                add("key." + MODID + ".anvilinator.disabled_naming", "Adlandırma kapalı.");
                add("itemGroup." + MODID, "Eternal Artifacts");
                add(MODID + ".subtitle.holy_dagger_activate", "Kutsal hançer aktifleştirildi!");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("encumbator.active"), "Yerden eşya almak engellendi!");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("encumbator.passive"), "Yerden eşya alma faaliyeti normal.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("encumbator.switch"), "Değiştirmek için envanterinde sağ tıkla.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("inventory_right_click"), "Envanterde sağ tıklayarak açabilirsin.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.ofItem(ModItems.GARDENING_POT), "Bitkileri hızlı büyütür ve otomatik olarak hasat eder. Eşyaları eğer altında envanter varsa oraya yoksa dünyada üstüne koyar. Kaktüs ve şeker kamışı büyütebilir.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("axe_of_regrowth_ench_text"), "Her zaman %s'a sahip");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("magic_feather_active"), "Büyülü tüy aktif!");
                add(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("magic_feather_not_active"), "Büyülü tüy aktif değil.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.ofItem(ModItems.GOLDEN_ANCIENT_FRUIT), "İki dakikalığına hız ve acele verir.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.ofItem(ModItems.ENCHANTED_GOLDEN_ANCIENT_FRUIT), "İki dakikalığına uçuş ve gelişmiş hız, acele ve emiş verir.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.ofItem(ModItems.COMFY_SHOES), "Giyildiğinde adım yüksekliği verir.");
                add(ModConstants.TRANSLATE_KEY_PREFIX.ofItem(ModItems.FROG_LEGS), "Zıplama yüksekliğini arttırır ve düşüş hasarını azaltır.");
            }
        }
    }
}
