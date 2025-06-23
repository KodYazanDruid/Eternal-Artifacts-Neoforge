package com.sonamorningstar.eternalartifacts;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder COMMON = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder CLIENT = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder SERVER = new ModConfigSpec.Builder();

    static {COMMON.push("Tools");}
    public static final ModConfigSpec.IntValue KNAPSACK_SLOT_IN_ROW =
            COMMON.comment("How many slots should be in one row?")
                    .defineInRange("knapsack_column", 9, 1, 48);
    public static final ModConfigSpec.IntValue TANK_KNAPSACK_SLOT_IN_ROW =
            COMMON.comment("How many slots should be in one row?")
                    .defineInRange("tank_knapsack_column", 9, 1, 48);
    public static final ModConfigSpec.DoubleValue CUTLASS_DROP_CHANCE =
            COMMON.comment("Drop chance for extra items on cutlass.")
                    .defineInRange("cutlass_drop_chance", 0.2D, 0.1D, 1D);
    public static final ModConfigSpec.IntValue VERSATILITY_COST =
            COMMON.comment("The durability damage amount for pickaxes enchanted with versatility?")
                    .defineInRange("versatility_cost", 3, 1, Integer.MAX_VALUE);
    static{COMMON.pop();}
    
    static{COMMON.push("World");}
    public static final ModConfigSpec.IntValue GASOLINE_EXPLOSION_THRESHOLD =
            COMMON.comment("The amount of gasoline required to cause an explosion.")
                    .defineInRange("gasoline_explosion_threshold", 1000, 1, Integer.MAX_VALUE);
    static{COMMON.pop();}

    static{COMMON.push("Charms");}
    public static final ModConfigSpec.BooleanValue CHARMS_ENABLED =
            COMMON.comment("Should charms be enabled?")
                    .define("charms_enabled", true);
    static{COMMON.pop();}
    
    static{SERVER.push("Artifacts");}
    public static final ModConfigSpec.DoubleValue FINAL_CUT_EXECUTE_THRESHOLD =
            SERVER.comment("Percentage of health that the Final Cut artifact will execute at.")
                    .defineInRange("final_cut_execute_threshold", 0.20D, 0.01D, 1D);
    static {SERVER.pop();}
    
    static final ModConfigSpec COMMON_SPEC = COMMON.build();
    static final ModConfigSpec CLIENT_SPEC = CLIENT.build();
    static final ModConfigSpec SERVER_SPEC = SERVER.build();

}
