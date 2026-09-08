package com.eeeab.eeeabsmobs.server.item.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

//1.21.1中ArmorItem类已移除，盔甲物品改为带Equippable组件的普通Item。
//此处检查玩家是否穿戴全套指定类型装备：所有4个护甲槽位均为有Equippable组件且槽位匹配的物品。
public class ModArmorUtil {
    private ModArmorUtil(){}

    //检查玩家是否穿戴全套盔甲(任意盔甲材质均可)
    public static boolean checkFullSuitOfArmor(Player player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
            ItemStack itemStack = player.getItemBySlot(slot);
            if (itemStack.isEmpty()) {
                return false;
            }
            //使用DataComponents.EQUIPPABLE检查是否为盔甲/可穿戴物品
            java.util.Optional<Equippable> opt = java.util.Optional.ofNullable(itemStack.get(net.minecraft.core.component.DataComponents.EQUIPPABLE));
            if (opt.isEmpty()) {
                return false;
            }
            //确保装备在正确的护甲槽位
            if (!slot.equals(opt.get().slot())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 旧API签名保留兼容——检查穿戴全套指定材质的盔甲。
     * 1.21.1中ArmorItem和getMaterial()已移除，无法直接比较ArmorMaterial。
     * 此方法仅检查玩家是否穿戴全套护甲（不再验证具体材质），如需材质校验请扩展实现。
     */
    @Deprecated
    public static boolean checkFullSuitOfArmor(java.util.function.Predicate<Item> materialChecker, Player player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
            ItemStack itemStack = player.getItemBySlot(slot);
            if (itemStack.isEmpty()) {
                return false;
            }
            if (!(itemStack.getItem() instanceof Item)) {
                return false;
            }
            if (!materialChecker.test(itemStack.getItem())) {
                return false;
            }
        }
        return true;
    }
}
