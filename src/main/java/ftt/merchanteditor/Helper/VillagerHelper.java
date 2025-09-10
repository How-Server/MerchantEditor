package ftt.merchanteditor.Helper;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import ftt.merchanteditor.MerchantInfo;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VillagerHelper {
    private static final List<MerchantInfo<VillagerProfession>> professions = Arrays.asList(
            new MerchantInfo<>(VillagerProfession.NONE, Items.GLASS, "None"),
            new MerchantInfo<>(VillagerProfession.ARMORER, Items.BLAST_FURNACE, "Armorer"),
            new MerchantInfo<>(VillagerProfession.BUTCHER, Items.SMOKER, "Butcher"),
            new MerchantInfo<>(VillagerProfession.CARTOGRAPHER, Items.CARTOGRAPHY_TABLE, "Cartographer"),
            new MerchantInfo<>(VillagerProfession.CLERIC, Items.BREWING_STAND, "Cleric"),
            new MerchantInfo<>(VillagerProfession.FARMER, Items.COMPOSTER, "Farmer"),
            new MerchantInfo<>(VillagerProfession.FISHERMAN, Items.BARREL, "Fisherman"),
            new MerchantInfo<>(VillagerProfession.FLETCHER, Items.FLETCHING_TABLE, "Fletcher"),
            new MerchantInfo<>(VillagerProfession.LEATHERWORKER, Items.CAULDRON, "Leatherworker"),
            new MerchantInfo<>(VillagerProfession.LIBRARIAN, Items.LECTERN, "Librarian"),
            new MerchantInfo<>(VillagerProfession.MASON, Items.STONECUTTER, "Mason"),
            new MerchantInfo<>(VillagerProfession.NITWIT, Items.PUMPKIN, "Nitwit"),
            new MerchantInfo<>(VillagerProfession.SHEPHERD, Items.LOOM, "Shepherd"),
            new MerchantInfo<>(VillagerProfession.TOOLSMITH, Items.SMITHING_TABLE, "Toolsmith"),
            new MerchantInfo<>(VillagerProfession.WEAPONSMITH, Items.GRINDSTONE, "Weaponsmith")
    );
    private static final List<MerchantInfo<VillagerType>> types = Arrays.asList(
            new MerchantInfo<>(VillagerType.PLAINS, Items.SHORT_GRASS, "Plains"),
            new MerchantInfo<>(VillagerType.DESERT, Items.TALL_DRY_GRASS, "Desert"),
            new MerchantInfo<>(VillagerType.JUNGLE, Items.JUNGLE_SAPLING, "Jungle"),
            new MerchantInfo<>(VillagerType.SAVANNA, Items.ACACIA_SAPLING, "Savanna"),
            new MerchantInfo<>(VillagerType.SNOW, Items.SNOWBALL, "Snow"),
            new MerchantInfo<>(VillagerType.SWAMP, Items.DARK_OAK_SAPLING, "Swamp"),
            new MerchantInfo<>(VillagerType.TAIGA, Items.SPRUCE_SAPLING, "Taiga")
    );

    public static void changeProfession(MerchantEntity merchant, boolean next) {
        if (!(merchant instanceof VillagerEntity villager)) {
            return;
        }
        VillagerData data = villager.getVillagerData();
        RegistryKey<VillagerProfession> current = data.profession().getKey().orElse(professions.getFirst().key);

        int index = 0;
        for (; index < professions.size(); index++) {
            if (professions.get(index).key.equals(current)) break;
        }
        int nextIndex = Math.floorMod(index + (next ? 1 : -1), professions.size());

        RegistryEntry.Reference<VillagerProfession> profession = merchant.getRegistryManager().getEntryOrThrow(professions.get(nextIndex).key);
        villager.setVillagerData(data.withProfession(profession));
    }


    public static void changeType(MerchantEntity merchant, boolean next) {
        if (!(merchant instanceof VillagerEntity villager)) {
            return;
        }
        VillagerData data = villager.getVillagerData();
        RegistryKey<VillagerType> current = data.type().getKey().orElse(types.getFirst().key);

        int index = 0;
        for (; index < types.size(); index++) {
            if (types.get(index).key.equals(current)) break;
        }
        int nextIndex = Math.floorMod(index + (next ? 1 : -1), types.size());

        RegistryEntry.Reference<VillagerType> type = merchant.getRegistryManager().getEntryOrThrow(types.get(nextIndex).key);
        villager.setVillagerData(data.withType(type));
    }

    public static void changeLevel(MerchantEntity merchant, boolean next) {
        if (!(merchant instanceof VillagerEntity villager)) {
            return;
        }
        VillagerData data = villager.getVillagerData();
        int level = Math.floorMod(data.level() + (next ? 0 : -2), 5) + 1;

        villager.setVillagerData(data.withLevel(level));
    }

    public static GuiElementBuilder getCurrentProfessionElement(MerchantEntity merchant) {

        if (!(merchant instanceof VillagerEntity villager)) {
            return new GuiElementBuilder().setItem(Items.BLACK_STAINED_GLASS_PANE).hideTooltip();
        }
        VillagerData data = villager.getVillagerData();
        RegistryKey<VillagerProfession> current = data.profession().getKey().orElse(professions.getFirst().key);

        int index = 0;
        for (; index < professions.size(); index++) {
            if (professions.get(index).key.equals(current)) break;
        }

        List<Text> lore = new ArrayList<>();
        lore.add(Text.literal("--------------------").styled(style -> style.withColor(Formatting.DARK_GRAY)));
        for (int i = 0; i < professions.size(); i++) {
            boolean selected = i == index;
            Text line = selected ? Text.literal("> " + professions.get(i).name).styled(style -> style.withColor(Formatting.GREEN).withBold(true)) : Text.literal(professions.get(i).name).styled(style -> style.withColor(Formatting.WHITE));
            lore.add(line);
        }

        return new GuiElementBuilder().setItem(professions.get(index).display).setName(Text.literal("職業")).setLore(lore);
    }


    public static GuiElementBuilder getCurrentTypeElement(MerchantEntity merchant) {
        if (!(merchant instanceof VillagerEntity villager)) {
            return new GuiElementBuilder().setItem(Items.BLACK_STAINED_GLASS_PANE).hideTooltip();
        }
        VillagerData data = villager.getVillagerData();
        RegistryKey<VillagerType> current = data.type().getKey().orElse(types.getFirst().key);

        int index = 0;
        for (; index < types.size(); index++) {
            if (types.get(index).key.equals(current)) break;
        }

        List<Text> lore = new ArrayList<>();
        lore.add(Text.literal("--------------------").styled(style -> style.withColor(Formatting.DARK_GRAY)));
        for (int i = 0; i < types.size(); i++) {
            boolean selected = i == index;
            Text line = selected ? Text.literal("> " + types.get(i).name).styled(style -> style.withColor(Formatting.GREEN).withBold(true)) : Text.literal(types.get(i).name).styled(style -> style.withColor(Formatting.WHITE));
            lore.add(line);
        }

        return new GuiElementBuilder().setItem(types.get(index).display).setName(Text.literal("生態")).setLore(lore);
    }

    public static GuiElementBuilder getCurrentLevelElement(MerchantEntity merchant) {
        if (!(merchant instanceof VillagerEntity villager)) {
            return new GuiElementBuilder().setItem(Items.BLACK_STAINED_GLASS_PANE).hideTooltip();
        }
        int level = villager.getVillagerData().level();
        List<Item> items = Arrays.asList(Items.COAL, Items.IRON_INGOT, Items.GOLD_INGOT, Items.EMERALD, Items.DIAMOND);
        List<String> names = Arrays.asList("Novice", "Apprentice", "Journeyman", "Expert", "Master");

        List<Text> lore = new ArrayList<>();
        lore.add(Text.literal("--------------------").styled(style -> style.withColor(Formatting.DARK_GRAY)));
        for (int i = 0; i < names.size(); i++) {
            boolean selected = i == (level - 1);
            Text line = selected ? Text.literal("> " + names.get(i)).styled(style -> style.withColor(Formatting.GREEN).withBold(true)) : Text.literal(names.get(i)).styled(style -> style.withColor(Formatting.WHITE));
            lore.add(line);
        }

        return new GuiElementBuilder().setItem(items.get(level - 1)).setItemName(Text.literal("等級")).setCount(level).setMaxCount(level).setLore(lore).hideDefaultTooltip();
    }
}
