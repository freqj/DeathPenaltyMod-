package com.soulboundhearts.items;

import com.soulboundhearts.utils.HealthHelper;
import com.soulboundhearts.utils.ModConfigHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Golden Heart item grants additional max health when eaten.
 */
public class GoldenHeartItem extends Item {
    public GoldenHeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            double stored = HealthHelper.getStoredMaxHealth(player);
            if (stored >= ModConfigHolder.getMaxHealth()) {
                player.displayClientMessage(Component.translatable("message.soulboundhearts.max_health_reached"), true);
                return InteractionResultHolder.fail(stack);
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            double stored = HealthHelper.getStoredMaxHealth(player);
            double increased = Math.min(ModConfigHolder.getMaxHealth(), stored + 2.0D);
            HealthHelper.setAndApplyMaxHealth(player, increased);
            player.setHealth((float) increased);
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.soulboundhearts.golden_heart.tooltip"));
        tooltip.add(Component.translatable(
                "item.soulboundhearts.golden_heart.tooltip.cap",
                HealthHelper.formatHealth(ModConfigHolder.getMaxHealth())));
    }
}
