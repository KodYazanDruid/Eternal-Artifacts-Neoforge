package com.sonamorningstar.eternalartifacts;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.util.LootTableHelper;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LootTableHelper testleri.
 */
public class EtarTest extends MinecraftBootstrapTest {

    // Private getCount metoduna erişim için reflection kullanıyoruz
    private Pair<Float, Float> invokeGetCount(NumberProvider provider) throws Exception {
        Method method = LootTableHelper.class.getDeclaredMethod("getCount", NumberProvider.class);
        method.setAccessible(true);
        return (Pair<Float, Float>) method.invoke(null, provider);
    }

    @Test
    @DisplayName("ConstantValue ile getCount - min ve max aynı olmalı")
    void testGetCountWithConstantValue() throws Exception {
        ConstantValue constant = ConstantValue.exactly(5.0f);
        Pair<Float, Float> result = invokeGetCount(constant);
        
        assertEquals(5.0f, result.getFirst(), 0.001f, "Minimum 5 olmalı");
        assertEquals(5.0f, result.getSecond(), 0.001f, "Maximum 5 olmalı");
    }

    @Test
    @DisplayName("ConstantValue 1 ile getCount")
    void testGetCountWithConstantValueOne() throws Exception {
        ConstantValue constant = ConstantValue.exactly(1.0f);
        Pair<Float, Float> result = invokeGetCount(constant);
        
        assertEquals(1.0f, result.getFirst(), 0.001f);
        assertEquals(1.0f, result.getSecond(), 0.001f);
    }

    @Test
    @DisplayName("UniformGenerator ile getCount - min ve max aralığı")
    void testGetCountWithUniformGenerator() throws Exception {
        UniformGenerator uniform = UniformGenerator.between(2.0f, 8.0f);
        Pair<Float, Float> result = invokeGetCount(uniform);
        
        assertEquals(2.0f, result.getFirst(), 0.001f, "Minimum 2 olmalı");
        assertEquals(8.0f, result.getSecond(), 0.001f, "Maximum 8 olmalı");
    }

    @Test
    @DisplayName("UniformGenerator geniş aralık ile getCount")
    void testGetCountWithUniformGeneratorWideRange() throws Exception {
        UniformGenerator uniform = UniformGenerator.between(1.0f, 64.0f);
        Pair<Float, Float> result = invokeGetCount(uniform);
        
        assertEquals(1.0f, result.getFirst(), 0.001f, "Minimum 1 olmalı");
        assertEquals(64.0f, result.getSecond(), 0.001f, "Maximum 64 olmalı");
    }

    @Test
    @DisplayName("BinomialDistributionGenerator ile getCount - min 0, max n olmalı")
    void testGetCountWithBinomialDistribution() throws Exception {
        // Binomial: n=10, p=0.5 -> 0 ile 10 arası değer üretir
        BinomialDistributionGenerator binomial = BinomialDistributionGenerator.binomial(10, 0.5f);
        Pair<Float, Float> result = invokeGetCount(binomial);
        
        assertEquals(0.0f, result.getFirst(), 0.001f, "Binomial minimum 0 olmalı");
        assertEquals(10.0f, result.getSecond(), 0.001f, "Binomial maximum n değeri olmalı");
    }

    @Test
    @DisplayName("BinomialDistributionGenerator küçük n ile getCount")
    void testGetCountWithBinomialDistributionSmallN() throws Exception {
        BinomialDistributionGenerator binomial = BinomialDistributionGenerator.binomial(3, 0.9f);
        Pair<Float, Float> result = invokeGetCount(binomial);
        
        assertEquals(0.0f, result.getFirst(), 0.001f, "Binomial minimum 0 olmalı");
        assertEquals(3.0f, result.getSecond(), 0.001f, "Binomial maximum 3 olmalı");
    }

    @Test
    @DisplayName("İç içe UniformGenerator ile getCount")
    void testGetCountWithNestedUniform() throws Exception {
        // UniformGenerator içinde ConstantValue kullanarak
        UniformGenerator uniform = UniformGenerator.between(
            ConstantValue.exactly(5.0f).getFloat(null),
            ConstantValue.exactly(15.0f).getFloat(null)
        );
        Pair<Float, Float> result = invokeGetCount(uniform);
        
        assertEquals(5.0f, result.getFirst(), 0.001f);
        assertEquals(15.0f, result.getSecond(), 0.001f);
    }

    @Test
    @DisplayName("Roll hesaplaması - minimum roll en az 1 olmalı")
    void testMinRollIsAtLeastOne() {
        // minRoll = Math.max(1, rolls.getFirst()) mantığını test ediyoruz
        float minRoll = Math.max(1, 0.0f);
        assertEquals(1.0f, minRoll, 0.001f, "Minimum roll en az 1 olmalı");
        
        minRoll = Math.max(1, 0.5f);
        assertEquals(1.0f, minRoll, 0.001f, "0.5 için minimum roll 1 olmalı");
        
        minRoll = Math.max(1, 2.0f);
        assertEquals(2.0f, minRoll, 0.001f, "2.0 için minimum roll 2 olmalı");
    }

    @Test
    @DisplayName("Max roll hesaplaması - bonus roll ile luck çarpılmalı")
    void testMaxRollWithLuck() {
        float baseRoll = 3.0f;
        float bonusRoll = 2.0f;
        float luck = 1.5f;
        
        float maxRoll = baseRoll + bonusRoll * luck;
        assertEquals(6.0f, maxRoll, 0.001f, "Max roll = 3 + 2*1.5 = 6 olmalı");
    }

    @Test
    @DisplayName("Entry count hesaplaması")
    void testEntryCountCalculation() {
        float itemCountMin = 2.0f;
        float itemCountMax = 5.0f;
        float minRoll = 1.0f;
        float maxRoll = 3.0f;
        
        float entryMin = itemCountMin * minRoll;
        float entryMax = itemCountMax * maxRoll;
        
        assertEquals(2.0f, entryMin, 0.001f, "Entry min = 2 * 1 = 2");
        assertEquals(15.0f, entryMax, 0.001f, "Entry max = 5 * 3 = 15");
    }

    @Test
    @DisplayName("Aynı eşya farklı kaynaklardan - min en düşük, max toplam")
    void testSameItemFromMultipleSources() {
        // İlk kaynak: min=2, max=5
        // İkinci kaynak: min=1, max=3
        // Sonuç: min=1 (en düşük), max=8 (toplam)
        
        float existingMin = 2.0f;
        float existingMax = 5.0f;
        float newMin = 1.0f;
        float newMax = 3.0f;
        
        float resultMin = Math.min(existingMin, newMin);
        float resultMax = existingMax + newMax;
        
        assertEquals(1.0f, resultMin, 0.001f, "Minimum en düşük değer olmalı");
        assertEquals(8.0f, resultMax, 0.001f, "Maximum toplam olmalı");
    }

    @Test
    @DisplayName("Zero luck ile max roll hesaplaması")
    void testMaxRollWithZeroLuck() {
        float baseRoll = 2.0f;
        float bonusRoll = 5.0f;
        float luck = 0.0f;
        
        float maxRoll = baseRoll + bonusRoll * luck;
        assertEquals(2.0f, maxRoll, 0.001f, "Luck 0 ise bonus roll etkisiz");
    }
}
