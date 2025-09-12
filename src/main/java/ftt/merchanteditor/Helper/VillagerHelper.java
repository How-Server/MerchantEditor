package ftt.merchanteditor.Helper;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import ftt.merchanteditor.MerchantInfo;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
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
            new MerchantInfo<>(VillagerProfession.NONE, Items.GLASS, "entity.minecraft.villager.none"),
            new MerchantInfo<>(VillagerProfession.ARMORER, Items.BLAST_FURNACE, "entity.minecraft.villager.armorer"),
            new MerchantInfo<>(VillagerProfession.BUTCHER, Items.SMOKER, "entity.minecraft.villager.butcher"),
            new MerchantInfo<>(VillagerProfession.CARTOGRAPHER, Items.CARTOGRAPHY_TABLE, "entity.minecraft.villager.cartographer"),
            new MerchantInfo<>(VillagerProfession.CLERIC, Items.BREWING_STAND, "entity.minecraft.villager.cleric"),
            new MerchantInfo<>(VillagerProfession.FARMER, Items.COMPOSTER, "entity.minecraft.villager.farmer"),
            new MerchantInfo<>(VillagerProfession.FISHERMAN, Items.BARREL, "entity.minecraft.villager.fisherman"),
            new MerchantInfo<>(VillagerProfession.FLETCHER, Items.FLETCHING_TABLE, "entity.minecraft.villager.fletcher"),
            new MerchantInfo<>(VillagerProfession.LEATHERWORKER, Items.CAULDRON, "entity.minecraft.villager.leatherworker"),
            new MerchantInfo<>(VillagerProfession.LIBRARIAN, Items.LECTERN, "entity.minecraft.villager.librarian"),
            new MerchantInfo<>(VillagerProfession.MASON, Items.STONECUTTER, "entity.minecraft.villager.mason"),
            new MerchantInfo<>(VillagerProfession.NITWIT, Items.PUMPKIN, "entity.minecraft.villager.nitwit"),
            new MerchantInfo<>(VillagerProfession.SHEPHERD, Items.LOOM, "entity.minecraft.villager.shepherd"),
            new MerchantInfo<>(VillagerProfession.TOOLSMITH, Items.SMITHING_TABLE, "entity.minecraft.villager.toolsmith"),
            new MerchantInfo<>(VillagerProfession.WEAPONSMITH, Items.GRINDSTONE, "entity.minecraft.villager.weaponsmith")
    );
    private static final List<MerchantInfo<VillagerType>> types = Arrays.asList(
            new MerchantInfo<>(VillagerType.PLAINS, Items.SHORT_GRASS, "biome.minecraft.plains"),
            new MerchantInfo<>(VillagerType.DESERT, Items.TALL_DRY_GRASS, "biome.minecraft.desert"),
            new MerchantInfo<>(VillagerType.JUNGLE, Items.JUNGLE_SAPLING, "biome.minecraft.jungle"),
            new MerchantInfo<>(VillagerType.SAVANNA, Items.ACACIA_SAPLING, "biome.minecraft.savanna"),
            new MerchantInfo<>(VillagerType.SNOW, Items.SNOWBALL, "biome.minecraft.snowy_plains"),
            new MerchantInfo<>(VillagerType.SWAMP, Items.DARK_OAK_SAPLING, "biome.minecraft.swamp"),
            new MerchantInfo<>(VillagerType.TAIGA, Items.SPRUCE_SAPLING, "biome.minecraft.taiga")
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
        lore.add(Text.literal("---------------").styled(style -> style.withColor(Formatting.DARK_GRAY)));
        for (int i = 0; i < professions.size(); i++) {
            boolean selected = i == index;
            Text line = selected ? Text.literal("> ").append(Text.translatable(professions.get(i).name)).styled(style -> style.withColor(Formatting.GREEN).withBold(true))
                    : Text.translatable(professions.get(i).name).styled(style -> style.withColor(Formatting.WHITE));
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
        lore.add(Text.literal("----------").styled(style -> style.withColor(Formatting.DARK_GRAY)));
        for (int i = 0; i < types.size(); i++) {
            boolean selected = i == index;
            Text line = selected ? Text.literal("> ").append(Text.translatable(types.get(i).name)).styled(style -> style.withColor(Formatting.GREEN).withBold(true))
                    : Text.translatable(types.get(i).name).styled(style -> style.withColor(Formatting.WHITE));
            lore.add(line);
        }

        return new GuiElementBuilder().setItem(types.get(index).display).setName(Text.literal("生態")).setLore(lore);
    }

    public static GuiElementBuilder getCurrentLevelElement(MerchantEntity merchant) {
        if (!(merchant instanceof VillagerEntity villager)) {
            return new GuiElementBuilder().setItem(Items.BLACK_STAINED_GLASS_PANE).hideTooltip();
        }
        int level = Math.min(5, villager.getVillagerData().level());

        List<Text> lore = new ArrayList<>();
        lore.add(Text.literal("----------").styled(style -> style.withColor(Formatting.DARK_GRAY)));
        for (int i = 1; i <= 5; i++) {
            boolean selected = i == level;
            Text base = Text.translatable("merchant.level." + i);
            Text line = selected ? Text.literal("> ").append(base).styled(style -> style.withColor(Formatting.GREEN).withBold(true)) : base.copy().styled(style -> style.withColor(Formatting.WHITE));
            lore.add(line);
        }

        return new GuiElementBuilder().setItem(Items.EMERALD).setItemName(Text.literal("等級")).setCount(level).setMaxCount(level).setLore(lore).hideDefaultTooltip();
    }
}
