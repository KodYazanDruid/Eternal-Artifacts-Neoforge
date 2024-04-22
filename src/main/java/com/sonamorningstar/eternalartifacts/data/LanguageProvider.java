package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.core.ModItems;
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

                //Blocks
                //add(ModBlocks.LUTFI.get(), "Lutfi");
                add(ModBlocks.ANVILINATOR.get(), "Anvilinator");
                add(ModBlocks.BIOFURNACE.get(), "BioFurnace");
                add(ModBlocks.RESONATOR.get(), "Resonator");
                add(ModBlocks.GARDENING_POT.get(), "Gardening Pot");

                //Entities
                add(ModEntities.DEMON_EYE.get(), "Demon Eye");

                //Misc.
                add("key." + MODID + ".anvilinator.enabled_naming", "Naming enabled.");
                add("key." + MODID + ".anvilinator.disabled_naming", "Naming disabled.");
                add("itemGroup." + MODID, "Eternal Artifacts");
                add(MODID + ".subtitle.holy_dagger_activate", "Holy dagger activated!");
                add("key." + MODID + ".tooltip." + ModItems.ENCUMBATOR.getId().getPath() + ".active", "Item pickup disabled!");
                add("key." + MODID + ".tooltip." + ModItems.ENCUMBATOR.getId().getPath() + ".passive", "Item pickup is normal.");
                add("key." + MODID + ".tooltip." + ModItems.ENCUMBATOR.getId().getPath() + ".switch", "Right click in inventory to switch.");
                add("key." + MODID + ".gardening_pot_item.desc", "Grows crops faster and automatically harvests. If there is inventory under it, puts the items there, otherwise puts them on top of it in the world. Able to grow cacti and sugar cane.");
                add("key."+MODID+".axe_of_regrowth_ench_text", "Always has %s");

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

                //Bloklar
                //add(ModBlocks.LUTFI.get(), "Lütfi");
                add(ModBlocks.ANVILINATOR.get(), "Örsinatör");
                add(ModBlocks.BIOFURNACE.get(), "BiyoFırın");
                add(ModBlocks.RESONATOR.get(), "Yankılayıcı");
                add(ModBlocks.GARDENING_POT.get(), "Bahçe Saksısı");

                //Varlıklar
                add(ModEntities.DEMON_EYE.get(), "İblis Gözü");

                //Ivır Zıvır.
                add("key." + MODID + ".anvilinator.enabled_naming", "Adlandırma açık.");
                add("key." + MODID + ".anvilinator.disabled_naming", "Adlandırma kapalı.");
                add("itemGroup." + MODID, "Eternal Artifacts");
                add(MODID + ".subtitle.holy_dagger_activate", "Kutsal hançer aktifleştirildi!");
                add("key." + MODID + ".tooltip." + ModItems.ENCUMBATOR.getId().getPath() + ".active", "Yerden eşya almak engellendi!");
                add("key." + MODID + ".tooltip." + ModItems.ENCUMBATOR.getId().getPath() + ".passive", "Yerden eşya alma faaliyeti normal.");
                add("key." + MODID + ".tooltip." + ModItems.ENCUMBATOR.getId().getPath()+ ".switch", "Değiştirmek için envanterinde sağ tıkla.");
                add("key." + MODID + ".gardening_pot_item.desc", "Bitkileri hızlı büyütür ve otomatik olarak hasat eder. Eşyaları eğer altında envanter varsa oraya yoksa dünyada üstüne koyar. Kaktüs ve şeker kamışı büyütebilir.");
                add("key."+MODID+".axe_of_regrowth_ench_text", "Her zaman %s'a sahip");

            }
        }
    }
}
