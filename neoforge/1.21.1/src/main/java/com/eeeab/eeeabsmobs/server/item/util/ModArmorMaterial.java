package com.eeeab.eeeabsmobs.server.item.util;


import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.init.ItemInit;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;

//盔甲级别
//1.21.1中ArmorMaterial不再是可继承的枚举，而是通过BuiltInRegistries.ARMOR_MATERIAL注册的record，
//用Holder<ArmorMaterial>引用。耐久度也不再是ArmorMaterial的一部分，改为在物品构造时通过Item.Properties#durability设置，
//每个部位的耐久度基础值沿用原HEALTH_FUNCTION_FOR_TYPE，乘以原耐久度倍数(180)后由各物品自行传入。
public class ModArmorMaterial {
    private ModArmorMaterial() {
    }

    //原耐久度倍数，供物品构造时计算每个部位的耐久度：ArmorItem.Type#getDurability(multiplier)
    //(1.21.1中ArmorItem.Type自带的基础耐久值 11/16/15/13 与原mod的HEALTH_FUNCTION_FOR_TYPE完全一致)
    public static final int GHOST_WARRIOR_DURABILITY_MULTIPLIER = 180;

    public static final Holder<ArmorMaterial> GHOST_WARRIOR_MATERIAL = register(
        "ghost_warrior",
        Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
            map.put(ArmorItem.Type.BOOTS, 5);
            map.put(ArmorItem.Type.LEGGINGS, 8);
            map.put(ArmorItem.Type.CHESTPLATE, 11);
            map.put(ArmorItem.Type.HELMET, 5);
            map.put(ArmorItem.Type.BODY, 0);
        }),
        20,
        SoundEvents.ARMOR_EQUIP_IRON,
        4.5F,
        0.125F,
        () -> Ingredient.of(ItemInit.GHOST_STEEL_INGOT.get())
    );

    private static Holder<ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> defense, int enchantmentValue,
                                                    Holder<net.minecraft.sounds.SoundEvent> equipSound, float toughness, float knockbackResistance,
                                                    java.util.function.Supplier<Ingredient> repairIngredient) {
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, name)));
        ArmorMaterial material = new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, layers, toughness, knockbackResistance);
        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, name), material);
    }
}
