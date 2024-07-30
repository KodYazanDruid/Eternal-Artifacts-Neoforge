package com.sonamorningstar.eternalartifacts;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue KNAPSACK_SLOT_IN_ROW =
            BUILDER.comment("How many slots should be in one row?")
                    .defineInRange("knapsack_column", 9, 1, 256);

    static final ModConfigSpec SPEC = BUILDER.build();

}
