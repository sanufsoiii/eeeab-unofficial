package com.eeeab.eeeabsmobs.server.item.util;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

//1.21.1中ArmorItem#getMaterial()返回Holder<ArmorMaterial>而不是ArmorMaterial本身，
//参数类型同步改为Holder<ArmorMaterial>，比较逻辑不变(Holder支持==比较，同一注册项是同一个Holder实例)
public class ModArmorUtil {
    private ModArmorUtil(){}

    //检查玩家是否穿戴全套盔甲
    public static boolean checkFullSuitOfArmor(Holder<ArmorMaterial> material, Player player) {
        ItemStack boots = player.getInventory().getArmor(0);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack chestPlate = player.getInventory().getArmor(2);
        ItemStack helmet = player.getInventory().getArmor(3);
        if (boots.isEmpty() || leggings.isEmpty() || chestPlate.isEmpty() || helmet.isEmpty()) {
            return false;
        }

        //避免出现类型转换异常
        for (ItemStack itemStack : player.getInventory().armor) {
            if (!(itemStack.getItem() instanceof ArmorItem)) {
                return false;
            }
        }

        ArmorItem bootsItem = (ArmorItem) boots.getItem();
        ArmorItem leggingsItem = (ArmorItem) leggings.getItem();
        ArmorItem chestPlateItem = (ArmorItem) chestPlate.getItem();
        ArmorItem helmetItem = (ArmorItem) helmet.getItem();

        return bootsItem.getMaterial() == material && leggingsItem.getMaterial() == material && chestPlateItem.getMaterial() == material && helmetItem.getMaterial() == material;
    }
}
