package ftt.merchanteditor;

import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MerchantNamingGUI extends AnvilInputGui {
    private final MerchantEditorGUI parentEditor;
    private String pendingName;

    public MerchantNamingGUI(ServerPlayerEntity player, MerchantEntity villager, MerchantEditorGUI parentEditor) {
        super(player, false);
        this.parentEditor = parentEditor;

        this.setTitle(Text.literal("為商人命名"));
        String current = villager.getName() != null ? villager.getName().getString() : "";
        this.setDefaultInputValue(current);
        this.pendingName = current;

        this.setSlot(1, new GuiElementBuilder(Items.NAME_TAG).setName(Text.literal("上方輸入新名稱，左側還原舊名稱")));
        this.setSlot(2, new GuiElementBuilder(Items.LIME_WOOL)
                .setName(Text.literal("儲存"))
                .setCallback((index, type, action, gui) -> {
                    String name = (this.pendingName == null ? "" : this.pendingName.trim());
                    if (!name.isEmpty()) {
                        villager.setCustomName(Text.literal(name));
                        if (this.parentEditor != null) this.parentEditor.setTitle(Text.literal("編輯交易：" + name));
                        player.sendMessage(Text.literal("已命名為：" + name), true);
                    } else {
                        villager.setCustomName(null);
                        if (this.parentEditor != null) {
                            String display = villager.getName() != null ? villager.getName().getString() : "";
                            this.parentEditor.setTitle(Text.literal("編輯交易：" + display));
                        }
                        player.sendMessage(Text.literal("已清除名稱"), true);
                    }
                    this.close();
                }));
    }

    @Override
    public void onInput(String input) {
        this.pendingName = (input == null) ? "" : input.trim();
    }

    @Override
    public void onClose() {
        if (this.parentEditor != null) {
            this.parentEditor.open();
        }
        super.onClose();
    }
}
