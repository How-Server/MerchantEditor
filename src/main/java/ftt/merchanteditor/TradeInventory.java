package ftt.merchanteditor;

import ftt.merchanteditor.Helper.TradeHelper;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.ItemCost;

import java.util.Arrays;
import java.util.Optional;

public class TradeInventory {
    final int maxTradeSlotCount = 99;  // TODO: dynamic size?
    SimpleContainer inv = new SimpleContainer(maxTradeSlotCount * 3);
    final int[] offerMaxUses = new int[maxTradeSlotCount];

    public TradeInventory() {
        Arrays.fill(offerMaxUses, Integer.MAX_VALUE);
    }

    public void addListener(ContainerListener listener) {
        inv.addListener(listener);
    }

    public int getMaxUse(int slot) {
        return offerMaxUses[slot];
    }

    public void applyMaxUse(int slot, int value) {
        offerMaxUses[slot] = value;
    }

    public void setOffer(int slot, ItemStack buyA, ItemStack buyB, ItemStack sell, int maxUses) {
        inv.setItem(slot * 3, buyA);
        inv.setItem(slot * 3 + 1, buyB);
        inv.setItem(slot * 3 + 2, sell);
        offerMaxUses[slot] = maxUses;
    }

    public ItemStack getStack(int slot) {
        return inv.getItem(slot);
    }

    public Slot getSlot(int index) {
        return new Slot(inv, index, 0, 0);
    }

    public int getMaxTradeSlotCount() {
        return maxTradeSlotCount;
    }

    public void loadOffers(AbstractVillager merchant) {
        MerchantOffers offers = merchant.getOffers();
        int index = 0;
        for (MerchantOffer offer : offers) {
            if (index + 1 >= maxTradeSlotCount) {
                break;
            }
            ItemStack buyA = offer.getItemCostA().itemStack();
            ItemStack buyB = offer.getItemCostB().isPresent() ? offer.getItemCostB().get().itemStack() : new ItemStack(Items.AIR);
            ItemStack sell = offer.getResult();

            setOffer(index, buyA, buyB, sell, offer.getMaxUses());

            index++;
        }
    }

    public MerchantOffers getOffers() {
        MerchantOffers offers = new MerchantOffers();

        for (int i = 0; i < maxTradeSlotCount; i++) {
            ItemStack stackA = inv.getItem(i * 3);
            ItemStack stackB = inv.getItem(i * 3 + 1);
            ItemStack sell = inv.getItem((i * 3) + 2);

            if (TradeHelper.isTradeEmpty(stackA, stackB, sell) || !TradeHelper.isTradeValid(stackA, stackB, sell)) {
                continue;
            }
            ItemCost buyA = TradeHelper.getFixedTradedItem(stackA);
            ItemCost buyB = TradeHelper.getFixedTradedItem(stackB);
            Optional<ItemCost> maybeTradeB = Optional.of(buyB).filter(t -> t.itemStack().getItem() != Items.AIR);

            offers.add(new MerchantOffer(buyA, maybeTradeB, sell, offerMaxUses[i], 0, 0));
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
