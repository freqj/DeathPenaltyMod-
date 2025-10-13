package com.soulboundhearts.items;

import com.soulboundhearts.SoulBoundHearts;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers all custom items for the mod.
 */
public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SoulBoundHearts.MOD_ID);

    public static final RegistryObject<Item> GOLDEN_HEART = ITEMS.register("golden_heart",
            () -> new GoldenHeartItem(new Item.Properties()
                    .stacksTo(16)
                    .food(new FoodProperties.Builder().nutrition(0).saturationMod(0.0F).alwaysEat().build())));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
