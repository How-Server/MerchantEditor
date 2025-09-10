package ftt.merchanteditor;

import ftt.merchanteditor.Helper.TradeHelper;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;

import java.util.Arrays;
import java.util.Optional;

public class TradeInventory {
    final int maxTradeSlotCount = 99;  // TODO: dynamic size?
    SimpleInventory inv = new SimpleInventory(maxTradeSlotCount * 3);
    final int[] offerMaxUses = new int[maxTradeSlotCount];

    public TradeInventory() {
        Arrays.fill(offerMaxUses, Integer.MAX_VALUE);
    }

    public void addListener(InventoryChangedListener listener) {
        inv.addListener(listener);
    }

    public int getMaxUse(int slot) {
        return offerMaxUses[slot];
    }

    public void applyMaxUse(int slot, int value) {
        offerMaxUses[slot] = value;
    }

    public void setOffer(int slot, ItemStack buyA, ItemStack buyB, ItemStack sell, int maxUses) {
        inv.setStack(slot * 3, buyA);
        inv.setStack(slot * 3 + 1, buyB);
        inv.setStack(slot * 3 + 2, sell);
        offerMaxUses[slot] = maxUses;
    }

    public ItemStack getStack(int slot) {
        return inv.getStack(slot);
    }

    public Slot getSlot(int index) {
        return new Slot(inv, index, 0, 0);
    }

    public int getMaxTradeSlotCount() {
        return maxTradeSlotCount;
    }

    public void loadOffers(MerchantEntity merchant) {
        TradeOfferList offers = merchant.getOffers();
        int index = 0;
        for (TradeOffer offer : offers) {
            if (index + 1 >= maxTradeSlotCount) {
                break;
            }
            ItemStack buyA = offer.getFirstBuyItem().itemStack();
            ItemStack buyB = offer.getSecondBuyItem().isPresent() ? offer.getSecondBuyItem().get().itemStack() : new ItemStack(Items.AIR);
            ItemStack sell = offer.getSellItem();

            setOffer(index, buyA, buyB, sell, offer.getMaxUses());

            index++;
        }
    }

    public TradeOfferList getOffers() {
        TradeOfferList offers = new TradeOfferList();

        for (int i = 0; i < maxTradeSlotCount; i++) {
            ItemStack stackA = inv.getStack(i * 3);
            ItemStack stackB = inv.getStack(i * 3 + 1);
            ItemStack sell = inv.getStack((i * 3) + 2);

            if (TradeHelper.isTradeEmpty(stackA, stackB, sell) || !TradeHelper.isTradeValid(stackA, stackB, sell)) {
                continue;
            }
            TradedItem buyA = TradeHelper.getFixedTradedItem(stackA);
            TradedItem buyB = TradeHelper.getFixedTradedItem(stackB);
            Optional<TradedItem> maybeTradeB = Optional.of(buyB).filter(t -> t.itemStack().getItem() != Items.AIR);

            offers.add(new TradeOffer(buyA, maybeTradeB, sell, offerMaxUses[i], 0, 0));
        }

        return offers;
    }

    public int findFirstInvalidTrade() {
        for (int i = 0; i < maxTradeSlotCount; i++) {
            if (!TradeHelper.isTradeValid(getStack(i * 3), getStack(i * 3 + 1), getStack(i * 3 + 2))) {
                return i;
            }
        }
        return -1;
    }

}
