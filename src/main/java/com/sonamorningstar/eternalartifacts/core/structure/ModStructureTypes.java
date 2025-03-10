package com.sonamorningstar.eternalartifacts.core.structure;

import com.sonamorningstar.eternalartifacts.world.structure.PlainsHouseStructure;
import com.sonamorningstar.eternalartifacts.world.structure.SurvivalistsIglooStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<SurvivalistsIglooStructure>> SURVIVALISTS_IGLOO = register("survivalists_igloo", ()-> ()-> SurvivalistsIglooStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<PlainsHouseStructure>> PLAINS_HOUSE = register("plains_house", ()-> ()-> PlainsHouseStructure.CODEC);


    private static <T extends Structure> DeferredHolder<StructureType<?>, StructureType<T>> register(String name, Supplier<StructureType<T>> supp) {
        return STRUCTURE_TYPES.register(name, supp);
    }
}