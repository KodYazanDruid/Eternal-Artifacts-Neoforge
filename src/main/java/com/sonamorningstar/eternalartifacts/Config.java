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
    
    static{COMMON.push("Artifacts");}
    public static final ModConfigSpec.DoubleValue FINAL_CUT_EXECUTE_THRESHOLD =
        COMMON.comment("Percentage of health that the Final Cut artifact will execute at.")
                    .defineInRange("final_cut_execute_threshold", 0.2D, 0.01D, 1D);
    public static final ModConfigSpec.DoubleValue MAGIC_BANE_DAMAGE_CONVERT_MULTIPLIER =
        COMMON.comment("Damage multiplier for the Magic Bane artifact.")
                    .defineInRange("magic_bane_damage_convert_multiplier", 0.2D, 0.1D, 1.0D);
    public static final ModConfigSpec.DoubleValue MOONGLASS_PENDANT_HEAL_MULTIPLIER =
        COMMON.comment("Healing multiplier for the Moonglass Pendant artifact.")
                    .defineInRange("moonglass_pendant_heal_multiplier", 0.2D, 0.1D, 1.0D);
    public static final ModConfigSpec.IntValue EYES_OF_DESTRUCTION_CRIT_BONUS =
        COMMON.comment("Crit damage bonus for the Eyes of Destruction artifact.")
                    .defineInRange("eyes_of_destruction_crit_bonus", 50, 5, 100);
    public static final ModConfigSpec.DoubleValue SANGUINE_AMULET_MAX_HEALTH =
        COMMON.comment("Max health bonus that sangunine amulet can give.")
                    .defineInRange("sanguine_amulet_max_health", 20.0D, 0.0D, 100.0D);
    public static final ModConfigSpec.IntValue SANGUINE_AMULET_MAX_SOULS =
        COMMON.comment("Soul amount required to fully max sanguine amulets health bonus. It is evenly distributed for each health point.")
                    .defineInRange("sanguine_amulet_max_souls", 2000, 0, 1_000_000);
    public static final ModConfigSpec.DoubleValue FLINT_TOOLS_FIRE_CHANCE =
        COMMON.comment("Chance for flint tools to set fire to mobs when hitting them.")
                    .defineInRange("flint_tools_fire_chance", 0.35D, 0.0D, 1.0D);
    public static final ModConfigSpec.IntValue FLINT_TOOLS_FIRE_DURATION =
        COMMON.comment("Duration in seconds for flint tools to set fire to mobs when hitting them.")
                    .defineInRange("flint_tools_fire_duration", 2, 0, 1024);
    public static final ModConfigSpec.DoubleValue BONE_TOOLS_REPAIR_PERCENTAGE =
        COMMON.comment("Percentage of durability that bone tools will repair when a milk fluid is consumed.")
                    .defineInRange("flint_tools_fire_chance", 0.2D, 0.05D, 1.0D);
    static {COMMON.pop();}
    
    static{SERVER.push("Machines");}
    public static final ModConfigSpec.IntValue BREW_AMOUNT =
        SERVER.comment("The mb amount of Alchemical Brewer should brew.")
            .defineInRange("brew_amount", 1000, 1, Integer.MAX_VALUE);
    static {SERVER.pop();}
    
    /*static{SERVER.push("Capabilities");}
    public static final ModConfigSpec.BooleanValue BOTTLE_CAP_ENABLED = SERVER.comment("Should bottle fluid capability be enabled?")
            .define("bottle_cap_enabled", true);
    static {SERVER.pop();}*/
    
    static final ModConfigSpec COMMON_SPEC = COMMON.build();
    static final ModConfigSpec CLIENT_SPEC = CLIENT.build();
    static final ModConfigSpec SERVER_SPEC = SERVER.build();

}
