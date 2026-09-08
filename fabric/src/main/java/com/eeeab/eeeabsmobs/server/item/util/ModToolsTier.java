package com.eeeab.eeeabsmobs.server.item.util;


import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

//工具级别
//1.21.9中 Tier 接口被移除，改为不可变 record net.minecraft.world.item.ToolMaterial：
//(incorrectBlocksForDrops:TagKey<Block>, durability:int, speed:float, attackDamageBonus:float,
// enchantmentValue:int, repairItems:TagKey<Item>)。
//挖掘等级仍用方块标签表达；修复材料从 Ingredient 变为 TagKey<Item>，对应标签文件见
//resources/data/eeeabsmobs/tags/item/repairs_*.json。
public final class ModToolsTier {
    private ModToolsTier() {
    }

    private static TagKey<Item> repairTag(String name) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "repairs_" + name));
    }

    public static final ToolMaterial RELICRON_TIER = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 4.0F, 15, repairTag("relicron"));
    public static final ToolMaterial IMMORTAL_TIER = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL, 500, 6.0F, 2.0F, 14, repairTag("immortal"));
    public static final ToolMaterial GHOST_STEEL_TIER = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 3000, 9.0F, 5.0F, 20, repairTag("ghost_steel"));
}
