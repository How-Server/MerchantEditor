package ftt.merchanteditor;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class Entry implements ModInitializer {
    public static final String MOD_ID = "merchanteditor";

    @Override
    public void onInitialize() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.isShiftKeyDown() && player.permissions().hasPermission(Permissions.COMMANDS_OWNER) && entity instanceof AbstractVillager villager) {
                handleVillagerInteraction((ServerPlayer) player, villager);
                return InteractionResult.SUCCESS;
            }
            return net.minecraft.world.InteractionResult.PASS;
        });
    }

    public static void handleVillagerInteraction(ServerPlayer player, AbstractVillager villager) {
        if (villager.getTags().contains("editing")) {
            player.displayClientMessage(Component.literal("有人正在修改該商人"), true);
            return;
        }

        SimpleGui gui = new MerchantEditorGUI(player, villager);
        gui.open();
    }


}