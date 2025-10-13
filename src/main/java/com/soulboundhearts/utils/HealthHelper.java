package com.soulboundhearts.utils;

import com.soulboundhearts.SoulBoundHearts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.Locale;

/**
 * Utility helpers related to health management.
 */
public final class HealthHelper {
    private static final String PERSISTED_TAG = Player.PERSISTED_NBT_TAG;
    private static final String HEALTH_KEY = "SoulBoundHeartsMaxHealth";

    private HealthHelper() {}

    /**
     * Retrieves the player's stored max health from persistent data, initializing it when missing.
     */
    public static double getStoredMaxHealth(Player player) {
        CompoundTag modTag = getOrCreateModTag(player);
        if (!modTag.contains(HEALTH_KEY)) {
            double attributeValue = getAttributeValue(player);
            modTag.putDouble(HEALTH_KEY, applyCaps(attributeValue));
            saveModTag(player, modTag);
        }
        return modTag.getDouble(HEALTH_KEY);
    }

    /**
     * Updates the stored max health value in persistent data and syncs it to the attribute.
     */
    public static void setAndApplyMaxHealth(Player player, double newMaxHealth) {
        double clamped = applyCaps(newMaxHealth);
        CompoundTag modTag = getOrCreateModTag(player);
        modTag.putDouble(HEALTH_KEY, clamped);
        saveModTag(player, modTag);
        applyAttribute(player, clamped);
    }

    /**
     * Ensures that the player's attribute matches the stored value.
     */
    public static void syncAttributeWithStorage(Player player) {
        double stored = getStoredMaxHealth(player);
        applyAttribute(player, stored);
    }

    /**
     * Applies the configured reduction multiplier and rounding rules to the given max health.
     */
    public static double calculateReducedHealth(double currentMaxHealth) {
        double multiplier = ModConfigHolder.getReductionMultiplier();
        double minHealth = ModConfigHolder.getMinHealth();

        // Convert HP to hearts to enforce rounding on whole hearts, then convert back to HP.
        double hearts = currentMaxHealth / 2.0D;
        double reducedHearts = Math.floor(hearts * multiplier);
        double reducedHealth = reducedHearts * 2.0D;

        return Math.max(minHealth, reducedHealth);
    }

    /**
     * Clamps the supplied health value inside the configured caps.
     */
    public static double applyCaps(double health) {
        return Math.max(ModConfigHolder.getMinHealth(), Math.min(ModConfigHolder.getMaxHealth(), health));
    }

    private static CompoundTag getOrCreateModTag(Player player) {
        CompoundTag persistent = player.getPersistentData();
        CompoundTag stored = persistent.getCompound(PERSISTED_TAG);
        persistent.put(PERSISTED_TAG, stored);

        CompoundTag modTag = stored.getCompound(SoulBoundHearts.MOD_ID);
        stored.put(SoulBoundHearts.MOD_ID, modTag);
        return modTag;
    }

    private static void saveModTag(Player player, CompoundTag modTag) {
        CompoundTag persistent = player.getPersistentData();
        CompoundTag stored = persistent.getCompound(PERSISTED_TAG);
        stored.put(SoulBoundHearts.MOD_ID, modTag);
        persistent.put(PERSISTED_TAG, stored);
    }

    private static double getAttributeValue(Player player) {
        AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
        return attribute != null ? attribute.getBaseValue() : 20.0D;
    }

    private static void applyAttribute(Player player, double value) {
        AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (attribute != null) {
            attribute.setBaseValue(value);
            if (player.getHealth() > value) {
                player.setHealth((float) value);
            }
        }
    }

    /**
     * Formats a health value for user-facing text without unnecessary trailing zeros.
     */
    public static String formatHealth(double health) {
        double rounded = Math.round(health * 100.0D) / 100.0D;
        if (Math.abs(rounded - Math.round(rounded)) < 1.0E-6D) {
            return Integer.toString((int) Math.round(rounded));
        }

        String formatted = String.format(Locale.ROOT, "%.2f", rounded);
        while (formatted.contains(".") && formatted.endsWith("0")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }
        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }
        return formatted;
    }
}
