package com.eeeab.eeeabsmobs.server.item;

import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * 可配置物品接口
 * 1.21.1中属性修饰符集合从Multimap&lt;Attribute,AttributeModifier&gt;改为不可变的ItemAttributeModifiers数据组件，
 * 每个entry自带EquipmentSlotGroup，替代了原先按EquipmentSlot分别查询的方式。
 */
public interface ConfigurableItem {

    /**
     * 用于初始化、覆盖原有物品属性
     *
     * @return 属性集合
     */
    ItemAttributeModifiers creatAttributesFromConfig();

    /**
     * 用于提供外部重置
     */
    void refreshAttributesFromConfig();
}
