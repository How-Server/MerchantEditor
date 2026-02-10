package ftt.merchanteditor.Helper;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import ftt.merchanteditor.MerchantInfo;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;

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
            new MerchantInfo<>(VillagerType.DESERT, Items.DRY_TALL_GRASS, "biome.minecraft.desert"),
            new MerchantInfo<>(VillagerType.JUNGLE, Items.JUNGLE_SAPLING, "biome.minecraft.jungle"),
            new MerchantInfo<>(VillagerType.SAVANNA, Items.ACACIA_SAPLING, "biome.minecraft.savanna"),
            new MerchantInfo<>(VillagerType.SNOW, Items.SNOWBALL, "biome.minecraft.snowy_plains"),
            new MerchantInfo<>(VillagerType.SWAMP, Items.DARK_OAK_SAPLING, "biome.minecraft.swamp"),
            new MerchantInfo<>(VillagerType.TAIGA, Items.SPRUCE_SAPLING, "biome.minecraft.taiga")
    );

    public static void changeProfession(AbstractVillager merchant, boolean next) {
        if (!(merchant instanceof Villager villager)) {
            return;
        }
        VillagerData data = villager.getVillagerData();
        ResourceKey<VillagerProfession> current = data.profession().unwrapKey().orElse(professions.getFirst().key);

        int index = 0;
        for (; index < professions.size(); index++) {
            if (professions.get(index).key.equals(current)) break;
        }
        int nextIndex = Math.floorMod(index + (next ? 1 : -1), professions.size());

        Holder.Reference<VillagerProfession> profession = merchant.registryAccess().getOrThrow(professions.get(nextIndex).key);
        villager.setVillagerData(data.withProfession(profession));
    }


    public static void changeType(AbstractVillager merchant, boolean next) {
        if (!(merchant instanceof Villager villager)) {
            return;
        }
        VillagerData data = villager.getVillagerData();
        ResourceKey<VillagerType> current = data.type().unwrapKey().orElse(types.getFirst().key);

        int index = 0;
        for (; index < types.size(); index++) {
            if (types.get(index).key.equals(current)) break;
        }
        int nextIndex = Math.floorMod(index + (next ? 1 : -1), types.size());

        Holder.Reference<VillagerType> type = merchant.registryAccess().getOrThrow(types.get(nextIndex).key);
        villager.setVillagerData(data.withType(type));
    }

    public static void changeLevel(AbstractVillager merchant, boolean next) {
        if (!(merchant instanceof Villager villager)) {
            return;
        }
        VillagerData data = villager.getVillagerData();
        int level = Math.floorMod(data.level() + (next ? 0 : -2), 5) + 1;

        villager.setVillagerData(data.withLevel(level));
    }

    public static GuiElementBuilder getCurrentProfessionElement(AbstractVillager merchant) {

        if (!(merchant instanceof Villager villager)) {
            return new GuiElementBuilder().setItem(Items.BLACK_STAINED_GLASS_PANE).hideTooltip();
        }
        VillagerData data = villager.getVillagerData();
        ResourceKey<VillagerProfession> current = data.profession().unwrapKey().orElse(professions.getFirst().key);

        int index = 0;
        for (; index < professions.size(); index++) {
            if (professions.get(index).key.equals(current)) break;
        }

        List<Component> lore = new ArrayList<>();
        lore.add(Component.literal("---------------").withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY)));
        for (int i = 0; i < professions.size(); i++) {
            boolean selected = i == index;
            Component line = selected ? Component.literal("> ").append(Component.translatable(professions.get(i).name)).withStyle(style -> style.withColor(ChatFormatting.GREEN).withBold(true))
                    : Component.translatable(professions.get(i).name).withStyle(style -> style.withColor(ChatFormatting.WHITE));
            lore.add(line);
        }

        return new GuiElementBuilder().setItem(professions.get(index).display).setName(Component.literal("職業")).setLore(lore);
    }


    public static GuiElementBuilder getCurrentTypeElement(AbstractVillager merchant) {
        if (!(merchant instanceof Villager villager)) {
            return new GuiElementBuilder().setItem(Items.BLACK_STAINED_GLASS_PANE).hideTooltip();
        }
        VillagerData data = villager.getVillagerData();
        ResourceKey<VillagerType> current = data.type().unwrapKey().orElse(types.getFirst().key);

        int index = 0;
        for (; index < types.size(); index++) {
            if (types.get(index).key.equals(current)) break;
        }

        List<Component> lore = new ArrayList<>();
        lore.add(Component.literal("----------").withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY)));
        for (int i = 0; i < types.size(); i++) {
            boolean selected = i == index;
            Component line = selected ? Component.literal("> ").append(Component.translatable(types.get(i).name)).withStyle(style -> style.withColor(ChatFormatting.GREEN).withBold(true))
                    : Component.translatable(types.get(i).name).withStyle(style -> style.withColor(ChatFormatting.WHITE));
            lore.add(line);
        }

        return new GuiElementBuilder().setItem(types.get(index).display).setName(Component.literal("生態")).setLore(lore);
    }

    public static GuiElementBuilder getCurrentLevelElement(AbstractVillager merchant) {
        if (!(merchant instanceof Villager villager)) {
            return new GuiElementBuilder().setItem(Items.BLACK_STAINED_GLASS_PANE).hideTooltip();
        }
        int level = Math.min(5, villager.getVillagerData().level());

        List<Component> lore = new ArrayList<>();
        lore.add(Component.literal("----------").withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY)));
        for (int i = 1; i <= 5; i++) {
            boolean selected = i == level;
            Component base = Component.translatable("merchant.level." + i);
            Component line = selected ? Component.literal("> ").append(base).withStyle(style -> style.withColor(ChatFormatting.GREEN).withBold(true)) : base.copy().withStyle(style -> style.withColor(ChatFormatting.WHITE));
            lore.add(line);
        }

        return new GuiElementBuilder().setItem(Items.EMERALD).setItemName(Component.literal("等級")).setCount(level).setMaxCount(level).setLore(lore).hideDefaultTooltip();
    }
}
