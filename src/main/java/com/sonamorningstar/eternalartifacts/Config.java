package com.sonamorningstar.eternalartifacts;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.push("Tools");
    }

    public static final ModConfigSpec.IntValue KNAPSACK_SLOT_IN_ROW =
            BUILDER.comment("How many slots should be in one row?")
                    .defineInRange("knapsack_column", 9, 1, 48);
    public static final ModConfigSpec.IntValue TANK_KNAPSACK_SLOT_IN_ROW =
            BUILDER.comment("How many slots should be in one row?")
                    .defineInRange("tank_knapsack_column", 9, 1, 48);
    public static final ModConfigSpec.DoubleValue CUTLASS_BEHEAD_CHANCE =
            BUILDER.comment("Head drop chance for cutlasses.")
                    .defineInRange("cutlass_behead_chance", 0.2D, 0.1D, 1D);

    static {
        BUILDER.pop();
    }

    static final ModConfigSpec SPEC = BUILDER.build();

}
