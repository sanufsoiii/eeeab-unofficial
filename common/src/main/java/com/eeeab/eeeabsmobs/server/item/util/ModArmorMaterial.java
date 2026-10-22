package com.eeeab.eeeabsmobs.server.item.util;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;

//盔甲级别
//1.21.9中 ArmorMaterial 变为不注册的 record：
//(durability:int, defense:Map<ArmorType,Integer>, enchantmentValue:int, equipSound:Holder<SoundEvent>,
// toughness:float, knockbackResistance:float, repairIngredient:TagKey<Item>, assetId:ResourceKey<EquipmentAsset>)。
//耐久度倍数由材料自带；贴图/模型由 assetId 指向的装备资产(assets/eeeabsmobs/equipment/ghost_warrior.json)决定。
public class ModArmorMaterial {
    private ModArmorMaterial() {
    }

    public static final int GHOST_WARRIOR_DURABILITY_MULTIPLIER = 180;

    public static final ResourceKey<EquipmentAsset> GHOST_WARRIOR_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "ghost_warrior"));

    private static final TagKey<Item> GHOST_STEEL_REPAIR =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "repairs_ghost_steel"));

    //net.minecraft.Util已移除，Util.make替换为直接构造EnumMap
    private static EnumMap<ArmorType, Integer> buildDefenseMap() {
        EnumMap<ArmorType, Integer> map = new EnumMap<>(ArmorType.class);
        map.put(ArmorType.BOOTS, 5);
        map.put(ArmorType.LEGGINGS, 8);
        map.put(ArmorType.CHESTPLATE, 11);
        map.put(ArmorType.HELMET, 5);
        map.put(ArmorType.BODY, 0);
        return map;
    }

    public static final ArmorMaterial GHOST_WARRIOR_MATERIAL = new ArmorMaterial(
            GHOST_WARRIOR_DURABILITY_MULTIPLIER,
            buildDefenseMap(),
            20,
            SoundEvents.ARMOR_EQUIP_IRON,
            4.5F,
            0.125F,
            GHOST_STEEL_REPAIR,
            GHOST_WARRIOR_ASSET
    );
}
