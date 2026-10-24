package com.eeeab.eeeabsmobs.server.item.util;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.EnumMap;

public class ModArmorMaterial {
    private ModArmorMaterial() {
    }

    public static final int GHOST_WARRIOR_DURABILITY_MULTIPLIER = 180;

    private static final TagKey<Item> GHOST_STEEL_REPAIR =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "repairs_ghost_steel"));

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
            ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "ghost_warrior")
    );
}
