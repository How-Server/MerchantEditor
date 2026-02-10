package ftt.merchanteditor;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import ftt.merchanteditor.Helper.TradeHelper;
import ftt.merchanteditor.Helper.VillagerHelper;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;

public class MerchantEditorGUI extends SimpleGui {
    TradeInventory inv;
    int pageOffset = 0;
    final long interactCD = 2;
    long lastInteractTick = 0;
    final int itemPerPage = 6;

    // merchant data
    AbstractVillager merchant;
    Vec3 villagerOriginPos;

    // buttons related to gui ( 9 * row + col )
    final int scrollPageUpIndex = 4;
    final int scrollPageDownIndex = 9 * 5 + 4;
    final int renameIndex = 9 + 6;
    final int changeTypeIndex = 9 * 3 + 6;
    final int changeProfessionIndex = 9 * 3 + 7;
    final int changeLevelIndex = 9 * 3 + 8;
    final int cancelChangeIndex = 9 * 5 + 6;
    final int saveChangeIndex = 9 * 5 + 8;

    // helper class
    public MerchantEditorGUI(ServerPlayer player, AbstractVillager merchant) {
        super(MenuType.GENERIC_9x6, player, false);
        this.inv = new TradeInventory();
        this.merchant = merchant;


        villagerOriginPos = this.merchant.position();
        this.merchant.snapTo(TradeHelper.getPreviewPos(player));

        inv.loadOffers(this.merchant);
        TradeHelper.useMerchant(this.merchant);
        initMenu();
        registerListener();
    }

    @Override
    public boolean onClick(int index, ClickType type, net.minecraft.world.inventory.ClickType action, GuiElementInterface element) {
        if (type.isMiddle || (!type.isLeft && !type.isRight) || type == ClickType.MOUSE_DOUBLE_CLICK) {
            return super.onClick(index, type, action, element);
        }
        if (player.level().getGameTime() <= lastInteractTick + interactCD) {
            return super.onClick(index, type, action, element);
        }
        lastInteractTick = player.level().getGameTime();


        boolean isLeft = type.isLeft;
        if (getSlot(index).getItemStack().getItem() != Items.BLACK_STAINED_GLASS_PANE) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.5F, isLeft ? 1F : 0.9F);
        }

        if (index == scrollPageUpIndex) {
            scrollMenu(type.shift ? 9 : 1, true);
        } else if (index == scrollPageDownIndex) {
            scrollMenu(type.shift ? 9 : 1, false);
        } else if (index == changeProfessionIndex) {
            VillagerHelper.changeProfession(merchant, isLeft);
        } else if (index == changeTypeIndex) {
            VillagerHelper.changeType(merchant, isLeft);
        } else if (index == changeLevelIndex) {
            VillagerHelper.changeLevel(merchant, isLeft);
        }

        updateMenu();
        return super.onClick(index, type, action, element);
    }


    private void initMenu() {
        setTitle(Component.literal("編輯交易：" + merchant.getName().getString()));
        for (int i = 0; i < 54; i++) {
            setSlot(i, new GuiElementBuilder(Items.BLACK_STAINED_GLASS_PANE).hideTooltip());
        }

        setSlot(scrollPageUpIndex, new GuiElementBuilder(Items.BLUE_STAINED_GLASS_PANE).setName(Component.literal("往上滾動")).addLoreLine(Component.literal("按住shift可跳一頁")));
        setSlot(scrollPageDownIndex, new GuiElementBuilder(Items.BLUE_STAINED_GLASS_PANE).setName(Component.literal("往下滾動")).addLoreLine(Component.literal("按住shift可跳一頁")));
        setSlot(renameIndex, new GuiElementBuilder(Items.NAME_TAG).setName(Component.literal("修改名稱")).setCallback(this::callRenameGUI));
        setSlot(changeTypeIndex, new GuiElementBuilder(Items.CARTOGRAPHY_TABLE).setName(Component.literal("生態")));
        setSlot(changeProfessionIndex, new GuiElementBuilder(Items.IRON_AXE).setName(Component.literal("工作")));
        setSlot(changeLevelIndex, new GuiElementBuilder(Items.EXPERIENCE_BOTTLE).setName(Component.literal("階級")));
        setSlot(cancelChangeIndex, new GuiElementBuilder(Items.RED_WOOL).setName(Component.literal("取消")).setCallback(this::cancelChanges));
        setSlot(saveChangeIndex, new GuiElementBuilder(Items.LIME_WOOL).setName(Component.literal("儲存")).setCallback(this::saveChanges));

        updateMenu();
    }


    private void updateMenu() {
        for (int i = 0; i < 6; i++) {
            int invSlot = pageOffset + i;

            // check if trade is valid and display it
            ItemStack stackA = inv.getStack(invSlot * 3);
            ItemStack stackB = inv.getStack(invSlot * 3 + 1);
            ItemStack sell = inv.getStack(invSlot * 3 + 2);

            boolean isEmpty = TradeHelper.isTradeEmpty(stackA, stackB, sell);
            boolean isValid = TradeHelper.isTradeValid(stackA, stackB, sell);
            int maxUse = inv.getMaxUse(invSlot);

            // Information about trade
            GuiElementBuilder checkItemElement = new GuiElementBuilder();
            if (!isValid) {
                checkItemElement.setItem(Items.BARRIER);
            } else if (isEmpty) {
                checkItemElement.setItem(Items.BLACK_STAINED_GLASS);
            } else if (maxUse == 0) {
                checkItemElement.setItem(Items.RED_STAINED_GLASS);
            } else if (maxUse == Integer.MAX_VALUE) {
                checkItemElement.setItem(Items.LIME_STAINED_GLASS);
            } else {
                checkItemElement.setItem(Items.YELLOW_STAINED_GLASS);
            }
            String maxUseString = maxUse == 0 ? "停用" : maxUse == Integer.MAX_VALUE ? "無限制" : Integer.toString(maxUse);
            ChatFormatting color = isValid ? ChatFormatting.GREEN : ChatFormatting.RED;
            checkItemElement
                    .setName(Component.literal("第 " + (invSlot + 1) + " 個交易").withStyle((s) -> s.withColor(color)))
                    .setCount(invSlot + 1)
                    .setMaxCount(99)
                    .setCallback(() -> callMaxUseGUI(invSlot))
                    .addLoreLine(Component.literal("數量：" + maxUseString));


            setSlot(i * 9, checkItemElement);
            // Link the trade inventory to menu
            setSlotRedirect(i * 9 + 1, inv.getSlot(invSlot * 3));
            setSlotRedirect(i * 9 + 2, inv.getSlot(invSlot * 3 + 1));
            setSlotRedirect(i * 9 + 3, inv.getSlot(invSlot * 3 + 2));
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
        player.sendSystemMessage(Component.literal("取消修改").withStyle(style -> style.withColor(ChatFormatting.RED)));
        TradeHelper.unuseMerchant(merchant);
        merchant.snapTo(villagerOriginPos);
        close();
    }

    private void saveChanges() {
        int invalid = inv.findFirstInvalidTrade();
        if (invalid >= 0) {
            setTitle(Component.literal("第 " + (invalid + 1) + " 個交易有問題").withStyle(style -> style.withColor(ChatFormatting.DARK_RED)));
            scrollToSlot(invalid);
            return;
        }

        saveToMerchant();
        player.sendSystemMessage(Component.literal("修改成功").withStyle(style -> style.withColor(ChatFormatting.GREEN)));
        TradeHelper.unuseMerchant(merchant);
        merchant.snapTo(villagerOriginPos);
        close();
    }

    public void scrollMenu(int distance, boolean goUp) {
        if (distance <= 0) {
            return;
        }
        for (int i = 0; i < distance; i++) {
            if (!goUp && !isLastPage()) {
                pageOffset++;
            } else if (goUp && !isFirstPage()) {
                pageOffset--;
            }
        }
    }

    private void scrollToSlot(int slot) {
        int left = pageOffset;
        int right = pageOffset + itemPerPage - 1;
        int mid = itemPerPage / 2;
        if (slot > right) {
            scrollMenu(slot - right + mid, false);
        } else if (slot < left) {
            scrollMenu(left - slot + mid, true);
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
        return ((pageOffset + itemPerPage) * 3 + 2) >= (inv.getMaxTradeSlotCount() * 3);
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
