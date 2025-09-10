package ftt.merchanteditor.Helper;

import net.minecraft.component.Component;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradedItem;

import java.util.Map;
import java.util.Optional;

public class TradeHelper {

    public static Vec3d getPreviewPos(ServerPlayerEntity player) {
        Vec3d playerPos = player.getPos();

        // Move villager to left of your screen
        Vec3d forward = player.getRotationVec(1.0F);
        Vec3d up = new Vec3d(0, 0.5, 0);
        Vec3d left = up.crossProduct(forward).normalize();
        Vec3d offset = left.multiply(1.5).add(up.multiply(0.5)).add(forward.multiply(1.5));
        return playerPos.add(offset);
    }

    public static void useMerchant(MerchantEntity merchant) {
        markMerchantEdited(merchant);

        merchant.addCommandTag("editing");
        merchant.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, StatusEffectInstance.INFINITE, 0, false, false));

    }

    public static void markMerchantEdited(MerchantEntity merchant) {
        merchant.addCommandTag("official"); // will stay here so we know it's edited by plugin
        merchant.setAiDisabled(true);
        merchant.setInvulnerable(true);
        merchant.setSilent(true);
        if (merchant instanceof VillagerEntity v) {
            v.getGossip().clear();
        }
    }

    public static void unuseMerchant(MerchantEntity merchant) {
        merchant.removeCommandTag("editing");
        merchant.removeStatusEffect(StatusEffects.GLOWING);
    }

    // This should be a temperate fix to prevent items have their base components stored.
    public static TradedItem getFixedTradedItem(ItemStack sourceItem) {
        return new TradedItem(sourceItem.getItem(), sourceItem.getCount()).withComponents((builder -> {
            ComponentChanges changes = sourceItem.getComponentChanges();
            for (Map.Entry<ComponentType<?>, Optional<?>> item : changes.entrySet()) {
                if (item.getValue().isEmpty()) {
                    continue;
                }
                Component<?> component = Component.of(item.getKey(), item.getValue().get());
                builder.add(component);
            }
            return builder;
        }));
    }

    public static boolean isTradeEmpty(ItemStack stackA, ItemStack stackB, ItemStack sell) {
        return stackA.getItem() == Items.AIR && stackB.getItem() == Items.AIR && sell.getItem() == Items.AIR;
    }

    public static boolean isTradeValid(ItemStack stackA, ItemStack stackB, ItemStack sell) {
        return isTradeEmpty(stackA, stackB, sell) || (stackA.getItem() != Items.AIR && sell.getItem() != Items.AIR);
    }
}
