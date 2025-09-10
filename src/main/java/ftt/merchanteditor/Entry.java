package ftt.merchanteditor;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class Entry implements ModInitializer {
    public static final String MOD_ID = "merchanteditor";

    @Override
    public void onInitialize() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.isSneaking() && player.hasPermissionLevel(4) && entity instanceof MerchantEntity villager) {
                handleVillagerInteraction((ServerPlayerEntity) player, villager);
                return net.minecraft.util.ActionResult.SUCCESS;
            }
            return net.minecraft.util.ActionResult.PASS;
        });
    }

    public static void handleVillagerInteraction(ServerPlayerEntity player, MerchantEntity villager) {
        if (villager.getCommandTags().contains("editing")) {
            player.sendMessage(Text.literal("有人正在修改該商人"), true);
            return;
        }

        SimpleGui gui = new MerchantEditorGUI(player, villager);
        gui.open();
    }


}