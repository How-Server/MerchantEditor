package ftt.merchanteditor;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;

public class MerchantInfo<T> {

    public final RegistryKey<T> key;
    public final Item display;
    public final String name;

    public MerchantInfo(RegistryKey<T> key, Item display, String name) {
        this.key = key;
        this.display = display;
        this.name = name;
    }

}
