package com.soulboundhearts;

import com.soulboundhearts.items.ModItems;
import com.soulboundhearts.utils.ModConfigHolder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point for the SoulBoundHearts mod.
 */
@Mod(SoulBoundHearts.MOD_ID)
public class SoulBoundHearts {
    public static final String MOD_ID = "soulboundhearts";
    public static final Logger LOGGER = LogManager.getLogger();

    public SoulBoundHearts() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigHolder.SPEC);
    }
}
