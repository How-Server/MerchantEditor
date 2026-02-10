package ftt.merchanteditor.Helper;

import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.trading.ItemCost;

import java.util.Map;
import java.util.Optional;

public class TradeHelper {

    public static Vec3 getPreviewPos(ServerPlayer player) {
        Vec3 playerPos = player.position();

        // Move villager to left of your screen
        Vec3 forward = player.getViewVector(1.0F);
        Vec3 up = new Vec3(0, 0.5, 0);
        Vec3 left = up.cross(forward).normalize();
        Vec3 offset = left.scale(1.5).add(up.scale(0.5)).add(forward.scale(1.5));
        return playerPos.add(offset);
    }

    public static void useMerchant(AbstractVillager merchant) {
        markMerchantEdited(merchant);

        merchant.addTag("editing");
        merchant.addEffect(new MobEffectInstance(MobEffects.GLOWING, MobEffectInstance.INFINITE_DURATION, 0, false, false));

    }

    public static void markMerchantEdited(AbstractVillager merchant) {
        merchant.addTag("official"); // will stay here so we know it's edited by plugin
        merchant.setNoAi(true);
        merchant.setInvulnerable(true);
        merchant.setSilent(true);
        if (merchant instanceof Villager v) {
            v.getGossips().clear();
        }
    }

    public static void unuseMerchant(AbstractVillager merchant) {
        merchant.removeTag("editing");
        merchant.removeEffect(MobEffects.GLOWING);
    }

    // This should be a temperate fix to prevent items have their base components stored.
    public static ItemCost getFixedTradedItem(ItemStack sourceItem) {
        return new ItemCost(sourceItem.getItem(), sourceItem.getCount()).withComponents((builder -> {
            DataComponentPatch changes = sourceItem.getComponentsPatch();
            for (Map.Entry<DataComponentType<?>, Optional<?>> item : changes.entrySet()) {
                if (item.getValue().isEmpty()) {
                    continue;
                }
                TypedDataComponent<?> component = TypedDataComponent.createUnchecked(item.getKey(), item.getValue().get());
                builder.expect(component);
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
