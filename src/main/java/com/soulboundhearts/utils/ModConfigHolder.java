package com.soulboundhearts.utils;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Holds the Forge configuration specification for the mod.
 */
public final class ModConfigHolder {
    private ModConfigHolder() {}

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue HEALTH_REDUCTION_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MIN_HEALTH_CAP;
    public static final ForgeConfigSpec.DoubleValue MAX_HEALTH_CAP;

    static {
        BUILDER.comment("General settings for SoulBoundHearts").push("general");

        HEALTH_REDUCTION_MULTIPLIER = BUILDER
                .comment("Multiplier applied to max health after death. Values < 1 reduce health.")
                .defineInRange("healthReductionMultiplier", 0.5D, 0.0D, 1.0D);

        MIN_HEALTH_CAP = BUILDER
                .comment("Minimum amount of health (in HP) a player can have.")
                .defineInRange("minHealthCap", 2.0D, 2.0D, 100.0D);

        MAX_HEALTH_CAP = BUILDER
                .comment("Maximum amount of health (in HP) a player can have.")
                .defineInRange("maxHealthCap", 50.0D, 2.0D, 200.0D);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static double getReductionMultiplier() {
        return HEALTH_REDUCTION_MULTIPLIER.get();
    }

    public static double getMinHealth() {
        return MIN_HEALTH_CAP.get();
    }

    public static double getMaxHealth() {
        return MAX_HEALTH_CAP.get();
    }
}
