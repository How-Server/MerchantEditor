package ftt.merchanteditor;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import ftt.merchanteditor.Helper.TradeHelper;
import ftt.merchanteditor.Helper.VillagerHelper;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

public class MerchantEditorGUI extends SimpleGui {
    TradeInventory inv;
    int pageOffset = 0;

    // merchant data
    MerchantEntity merchant;
    Vec3d villagerOriginPos;

    // buttons related to gui
    final int scrollPageIndex = 45;
    final int renameIndex = 46;
    final int changeTypeIndex = 47;
    final int changeProfessionIndex = 48;
    final int changeLevelIndex = 49;
    final int cancelChangeIndex = 51;
    final int saveChangeIndex = 53;

    // helper class
    public MerchantEditorGUI(ServerPlayerEntity player, MerchantEntity merchant) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);
        this.inv = new TradeInventory();
        this.merchant = merchant;


        villagerOriginPos = this.merchant.getPos();
        this.merchant.refreshPositionAfterTeleport(TradeHelper.getPreviewPos(player));

        inv.loadOffers(this.merchant);
        TradeHelper.useMerchant(this.merchant);
        initMenu();
        registerListener();
    }

    @Override
    public boolean onClick(int index, ClickType type, SlotActionType action, GuiElementInterface element) {
        if (type.isMiddle || (!type.isLeft && !type.isRight)) {
            return super.onClick(index, type, action, element);
        }

        boolean goNext = type.isLeft;
        if (index == scrollPageIndex) {
            scrollMenu(type.shift ? 9 : 1, !goNext);
        } else if (index == changeProfessionIndex) {
            VillagerHelper.changeProfession(merchant, goNext);
        } else if (index == changeTypeIndex) {
            VillagerHelper.changeType(merchant, goNext);
        } else if (index == changeLevelIndex) {
            VillagerHelper.changeLevel(merchant, goNext);
        }

        updateMenu();
        return super.onClick(index, type, action, element);
    }


    private void initMenu() {
        setTitle(Text.literal("編輯交易：" + merchant.getName().getString()));
        for (int i = 0; i < 54; i++) {
            setSlot(i, new GuiElementBuilder(Items.BLACK_STAINED_GLASS_PANE).hideTooltip());
        }

        setSlot(scrollPageIndex, new GuiElementBuilder(Items.CLOCK).setName(Text.literal("滾動頁面")).addLoreLine(Text.literal("滑鼠左右鍵")).addLoreLine(Text.literal("按住shift可跳一頁")));
        setSlot(renameIndex, new GuiElementBuilder(Items.NAME_TAG).setName(Text.literal("修改名稱")).setCallback(this::callRenameGUI));
        setSlot(changeTypeIndex, new GuiElementBuilder(Items.CARTOGRAPHY_TABLE).setName(Text.literal("生態")));
        setSlot(changeProfessionIndex, new GuiElementBuilder(Items.IRON_AXE).setName(Text.literal("工作")));
        setSlot(changeLevelIndex, new GuiElementBuilder(Items.EXPERIENCE_BOTTLE).setName(Text.literal("階級")));
        setSlot(cancelChangeIndex, new GuiElementBuilder(Items.RED_WOOL).setName(Text.literal("取消")).setCallback(this::cancelChanges));
        setSlot(saveChangeIndex, new GuiElementBuilder(Items.LIME_WOOL).setName(Text.literal("儲存")).setCallback(this::saveChanges));

        updateMenu();
    }


    private void updateMenu() {
        for (int i = 0; i < 9; i++) {
            int invSlot = pageOffset + i;
            // Link the trade inventory to menu
            setSlotRedirect(9 + i, inv.getSlot(invSlot * 3));
            setSlotRedirect(18 + i, inv.getSlot(invSlot * 3 + 1));
            setSlotRedirect(27 + i, inv.getSlot(invSlot * 3 + 2));

            // check if trade is valid and display it
            ItemStack stackA = inv.getStack(invSlot * 3);
            ItemStack stackB = inv.getStack(invSlot * 3 + 1);
            ItemStack sell = inv.getStack(invSlot * 3 + 2);

            boolean isEmpty = TradeHelper.isTradeEmpty(stackA, stackB, sell);
            boolean isValid = TradeHelper.isTradeValid(stackA, stackB, sell);

            // show is trade valid
            Item checkItem = Items.LIME_STAINED_GLASS;
            if (!isValid) {
                checkItem = Items.RED_STAINED_GLASS;
            }
            if (isEmpty) {
                checkItem = Items.BLACK_STAINED_GLASS;
            }
            setSlot(i, new GuiElementBuilder(checkItem).setCount(invSlot + 1).setMaxCount(99).hideTooltip());

            // show trade max use
            int maxUse = inv.getMaxUse(invSlot);
            Item maxUseItem = Items.YELLOW_STAINED_GLASS_PANE;
            Text maxUseName = Text.literal("次數：" + maxUse).styled(s -> s.withColor(Formatting.YELLOW));
            if (isEmpty) {
                maxUseItem = Items.BLACK_STAINED_GLASS_PANE;
                maxUseName = Text.literal("無");
                maxUse = 1;
            } else if (maxUse == 0) {
                maxUseName = Text.literal("停用").styled(s -> s.withColor(Formatting.RED));
                maxUseItem = Items.RED_STAINED_GLASS_PANE;
                maxUse = 1;
            } else if (maxUse == Integer.MAX_VALUE) {
                maxUseName = Text.literal("無限制").styled(s -> s.withColor(Formatting.GREEN));
                maxUseItem = Items.GREEN_STAINED_GLASS_PANE;
                maxUse = 1;
            }
            setSlot(36 + i, new GuiElementBuilder()
                    .setItem(maxUseItem)
                    .setName(maxUseName)
                    .setCount(Math.min(maxUse, 99))
                    .addLoreLine(Text.literal("點擊即可編輯"))
                    .setMaxCount(99).setCallback(() -> callMaxUseGUI(invSlot)));
        }

        // Control panel
        setSlot(changeProfessionIndex, VillagerHelper.getCurrentProfessionElement(merchant));
        setSlot(changeTypeIndex, VillagerHelper.getCurrentTypeElement(merchant));
        setSlot(changeLevelIndex, VillagerHelper.getCurrentLevelElement(merchant));

    }


    public void applyMaxUse(int slot, int maxUses) {
        inv.applyMaxUse(slot, maxUses);
    }

    private void saveToMerchant() {
        merchant.getOffers().clear();
        merchant.getOffers().addAll(inv.getOffers());
    }

    private void cancelChanges() {
        player.sendMessage(Text.literal("取消修改").styled(style -> style.withColor(Formatting.RED)));
        TradeHelper.unuseMerchant(merchant);
        merchant.refreshPositionAfterTeleport(villagerOriginPos);
        close();
    }

    private void saveChanges() {
        int invalid = inv.findFirstInvalidTrade();
        if (invalid >= 0) {
            setTitle(Text.literal("第 " + (invalid + 1) + " 個交易有問題").styled(style -> style.withColor(Formatting.DARK_RED)));
            scrollToSlot(invalid);
            return;
        }

        saveToMerchant();
        player.sendMessage(Text.literal("修改成功").styled(style -> style.withColor(Formatting.GREEN)));
        TradeHelper.unuseMerchant(merchant);
        merchant.refreshPositionAfterTeleport(villagerOriginPos);
        close();
    }

    public void scrollMenu(int distance, boolean goBack) {
        if (distance <= 0) {
            return;
        }
        for (int i = 0; i < distance; i++) {
            if (!goBack && !isLastPage()) {
                pageOffset++;
            } else if (goBack && !isFirstPage()) {
                pageOffset--;
            }
        }
    }

    private void scrollToSlot(int slot) {
        int left = pageOffset;
        int right = pageOffset + 8;
        if (slot > right) {
            scrollMenu(slot - right + 4, false);
        } else if (slot < left) {
            scrollMenu(left - slot + 4, true);
        }
    }

    private void callMaxUseGUI(int invSlot) {
        MerchantMaxUseGUI maxUseGui = new MerchantMaxUseGUI(player, invSlot, inv.getMaxUse(invSlot), this);
        close();
        maxUseGui.open();
    }


    private void callRenameGUI() {
        MerchantNamingGUI namingGui = new MerchantNamingGUI(player, merchant, this);
        this.close();
        namingGui.open();
    }

    private boolean isFirstPage() {
        return pageOffset <= 0;
    }

    private boolean isLastPage() {
        return ((pageOffset + 9) * 3 + 2) >= (inv.getMaxTradeSlotCount() * 3);
    }


    private void registerListener() {
        inv.addListener(sender -> updateMenu());
    }

    @Override
    public boolean open() {
        updateMenu();
        return super.open();
    }

    @Override
    public boolean canPlayerClose() {
        return false;
    }

}
