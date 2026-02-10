package ftt.merchanteditor;

import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceKey;

public class MerchantInfo<T> {

    public final ResourceKey<T> key;
    public final Item display;
    public final String name;

    public MerchantInfo(ResourceKey<T> key, Item display, String name) {
        this.key = key;
        this.display = display;
        this.name = name;
    }

}
