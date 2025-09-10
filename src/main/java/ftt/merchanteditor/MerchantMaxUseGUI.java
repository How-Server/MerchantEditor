package ftt.merchanteditor;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MerchantMaxUseGUI extends AnvilInputGui {
    private final MerchantEditorGUI parentEditor;
    private final int tradeIndex;
    private String pending;

    public MerchantMaxUseGUI(ServerPlayerEntity player, int tradeIndex, int current, MerchantEditorGUI parentEditor) {
        super(player, false);
        this.parentEditor = parentEditor;
        this.tradeIndex = tradeIndex;

        this.setTitle(Text.literal("設定最大次數 (-1=無限, 0=禁用)"));
        this.pending = defaultInputForCurrent(current);
        this.setDefaultInputValue(this.pending);

        this.setSlot(1, new GuiElementBuilder(Items.PAPER)
                .setName(Text.literal("上方輸入新次數，左側還原舊次數")));
        this.setSlot(2, new GuiElementBuilder(Items.LIME_WOOL)
                .setName(Text.literal("儲存"))
                .setCallback((index, type, action, gui) -> {
                    int parsed = parseOrDefault(this.pending, current);
                    int normalized = normalizeInput(parsed);
                    if (this.parentEditor != null) {
                        this.parentEditor.applyMaxUse(this.tradeIndex, normalized);
                        this.parentEditor.open();
                    }
                    this.close();
                }));
    }

    @Override
    public void onInput(String input) {
        this.pending = (input == null) ? "" : input.trim();
    }

    @Override
    public void onClose() {
        if (this.parentEditor != null) {
            this.parentEditor.open();
        }
        super.onClose();
    }

    private static int parseOrDefault(String s, int def) {
        if (s == null || s.isEmpty()) return def;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static int normalizeInput(int v) {
        if (v < 0) return Integer.MAX_VALUE;
        return v;
    }

    private static String defaultInputForCurrent(int current) {
        if (current == Integer.MAX_VALUE) return "-1";
        return Integer.toString(current);
    }
    
}
