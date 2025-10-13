package com.soulboundhearts.events;

import com.soulboundhearts.SoulBoundHearts;
import com.soulboundhearts.utils.HealthHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Handles player related Forge events such as login, respawn and world load.
 */
@Mod.EventBusSubscriber(modid = SoulBoundHearts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PlayerEventHandler {
    private PlayerEventHandler() {}

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            enforceKeepInventory(serverLevel);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide) {
            if (player.level() instanceof ServerLevel serverLevel) {
                enforceKeepInventory(serverLevel);
            }
            HealthHelper.syncAttributeWithStorage(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide) {
            HealthHelper.syncAttributeWithStorage(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player newPlayer = event.getEntity();
        Player oldPlayer = event.getOriginal();
        if (!newPlayer.level().isClientSide) {
            double previousMax = HealthHelper.getStoredMaxHealth(oldPlayer);
            double newMax = previousMax;
            if (event.isWasDeath()) {
                newMax = HealthHelper.calculateReducedHealth(previousMax);
            }
            HealthHelper.setAndApplyMaxHealth(newPlayer, newMax);
        }
    }

    private static void enforceKeepInventory(ServerLevel level) {
        GameRules.BooleanValue rule = level.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY);
        if (!rule.get()) {
            rule.set(true, level.getServer());
            SoulBoundHearts.LOGGER.info("Enabled keepInventory gamerule for level {}", level.dimension().location());
        }
    }
}
