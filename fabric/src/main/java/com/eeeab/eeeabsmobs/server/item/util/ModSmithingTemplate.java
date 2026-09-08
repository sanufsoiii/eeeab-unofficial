package com.eeeab.eeeabsmobs.server.item.util;
import net.minecraft.util.Util;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

public class ModSmithingTemplate /*extends SmithingTemplateItem*/ {
    private static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    private static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
    private static final Identifier EMPTY_SLOT_INGOT = Identifier.withDefaultNamespace("item/empty_slot_ingot");
    private static final Identifier EMPTY_SLOT_DIAMOND = Identifier.withDefaultNamespace("item/empty_slot_diamond");
    private static final Identifier EMPTY_SLOT_HELMET = Identifier.withDefaultNamespace("item/empty_armor_slot_helmet");
    private static final Identifier EMPTY_SLOT_CHESTPLATE = Identifier.withDefaultNamespace("item/empty_armor_slot_chestplate");
    private static final Identifier EMPTY_SLOT_LEGGINGS = Identifier.withDefaultNamespace("item/empty_armor_slot_leggings");
    private static final Identifier EMPTY_SLOT_BOOTS = Identifier.withDefaultNamespace("item/empty_armor_slot_boots");
    private static final Identifier EMPTY_SLOT_HOE = Identifier.withDefaultNamespace("item/empty_slot_hoe");
    private static final Identifier EMPTY_SLOT_AXE = Identifier.withDefaultNamespace("item/empty_slot_axe");
    private static final Identifier EMPTY_SLOT_SWORD = Identifier.withDefaultNamespace("item/empty_slot_sword");
    private static final Identifier EMPTY_SLOT_SHOVEL = Identifier.withDefaultNamespace("item/empty_slot_shovel");
    private static final Identifier EMPTY_SLOT_PICKAXE = Identifier.withDefaultNamespace("item/empty_slot_pickaxe");
    private static final Component NETHERITE_UPGRADE_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "smithing_template.netherite_upgrade.applies_to"))).withStyle(DESCRIPTION_FORMAT);

    private static final Component RELICRON_UPGRADE_UPGRADE = Component.translatable(Util.makeDescriptionId("upgrade", Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "relicron"))).withStyle(TITLE_FORMAT);
    private static final Component RELICRON_UPGRADE_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "smithing_template.relicron.ingredients"))).withStyle(DESCRIPTION_FORMAT);
    private static final Component RELICRON_UPGRADE_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "smithing_template.relicron.base_slot_description")));
    private static final Component RELICRON_UPGRADE_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "smithing_template.relicron.additions_slot_description")));

    private static final Component GHOST_WARRIOR_UPGRADE = Component.translatable(Util.makeDescriptionId("upgrade", Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "ghost_warrior"))).withStyle(TITLE_FORMAT);
    private static final Component GHOST_WARRIOR_UPGRADE_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "smithing_template.ghost_warrior.ingredients"))).withStyle(DESCRIPTION_FORMAT);
    private static final Component GHOST_WARRIOR_UPGRADE_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "smithing_template.ghost_warrior.base_slot_description")));
    private static final Component GHOST_WARRIOR_UPGRADE_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "smithing_template.ghost_warrior.additions_slot_description")));

    /*
    public ModSmithingTemplate(Component pAppliesTo, Component pIngredients, Component pUpdradeDescription, Component pBaseSlotDescription, Component pAdditionsSlotDescription, List<Identifier> pBaseSlotEmptyIcons, List<Identifier> pAdditonalSlotEmptyIcons) {
        super(pAppliesTo, pIngredients, pUpdradeDescription, pBaseSlotDescription, pAdditionsSlotDescription, pBaseSlotEmptyIcons, pAdditonalSlotEmptyIcons);
    }
    */

    public static SmithingTemplateItem createRelicronUpgradeTemplate(net.minecraft.world.item.Item.Properties p) {
        return new SmithingTemplateItem(NETHERITE_UPGRADE_APPLIES_TO, RELICRON_UPGRADE_INGREDIENTS,
                RELICRON_UPGRADE_BASE_SLOT_DESCRIPTION, RELICRON_UPGRADE_ADDITIONS_SLOT_DESCRIPTION,
                List.of(EMPTY_SLOT_AXE, EMPTY_SLOT_SWORD), List.of(EMPTY_SLOT_DIAMOND), p);
    }

    public static SmithingTemplateItem createGhostWarriorUpgradeTemplate(net.minecraft.world.item.Item.Properties p) {
        return new SmithingTemplateItem(NETHERITE_UPGRADE_APPLIES_TO, GHOST_WARRIOR_UPGRADE_INGREDIENTS,
                GHOST_WARRIOR_UPGRADE_BASE_SLOT_DESCRIPTION, GHOST_WARRIOR_UPGRADE_ADDITIONS_SLOT_DESCRIPTION,
                List.of(EMPTY_SLOT_HELMET, EMPTY_SLOT_CHESTPLATE, EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_BOOTS), List.of(EMPTY_SLOT_INGOT), p);
    }

/*    @Override
    public boolean canBeHurtBy(DamageSource pDamageSource) {
        return !pDamageSource.is(DamageTypeTags.IS_FIRE) && super.canBeHurtBy(pDamageSource);
    }*/
}
