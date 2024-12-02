package com.sonamorningstar.eternalartifacts;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder COMMON = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder CLIENT = new ModConfigSpec.Builder();

    static {COMMON.push("Tools");}
    public static final ModConfigSpec.IntValue KNAPSACK_SLOT_IN_ROW =
            COMMON.comment("How many slots should be in one row?")
                    .defineInRange("knapsack_column", 9, 1, 48);
    public static final ModConfigSpec.IntValue TANK_KNAPSACK_SLOT_IN_ROW =
            COMMON.comment("How many slots should be in one row?")
                    .defineInRange("tank_knapsack_column", 9, 1, 48);
    public static final ModConfigSpec.DoubleValue CUTLASS_BEHEAD_CHANCE =
            COMMON.comment("Head drop chance for cutlasses.")
                    .defineInRange("cutlass_behead_chance", 0.2D, 0.1D, 1D);
    public static final ModConfigSpec.IntValue VERSATILITY_COST =
            COMMON.comment("The durability damage amount for pickaxes enchanted with versatility?")
                    .defineInRange("versatility_cost", 3, 1, Integer.MAX_VALUE);
    static{COMMON.pop();}
    static{COMMON.push("World");}
    public static final ModConfigSpec.IntValue GASOLINE_EXPLOSION_THRESHOLD =
            COMMON.comment("The amount of gasoline required to cause an explosion.")
                    .defineInRange("gasoline_explosion_threshold", 1000, 1, Integer.MAX_VALUE);
    static{COMMON.pop();}

    public static final ModConfigSpec.BooleanValue CHARMS_ENABLED =
            CLIENT.comment("Should charms be enabled?")
                    .define("charms_enabled", true);

    static final ModConfigSpec COMMON_SPEC = COMMON.build();
    static final ModConfigSpec CLIENT_SPEC = CLIENT.build();

}
