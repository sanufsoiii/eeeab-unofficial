package com.eeeab.eeeabsmobs.server.item.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

//1.21.1中ItemStack不再直接携带CompoundTag(getTag()/setTag()已移除)，
//自定义NBT数据改为存放在DataComponents.CUSTOM_DATA这个数据组件里(CustomData包装类)。
//CustomData是不可变的，每次读取都是tag的拷贝，因此getNBT不再返回可原地修改后自动生效的引用，
//调用方必须通过putNBT写回；本类的现有调用方(ItemAnimationController)只做读取+移除，未依赖原地可变引用，行为保持一致。
public class ModItemStackUtils {
    private ModItemStackUtils() {
    }

    /**
     * 判断是否存在指定的NBT标签数据
     *
     * @param itemStack 物品栈
     * @param keyName   键名
     * @return 是否存在
     */
    public static boolean hasNBT(ItemStack itemStack, String keyName) {
        return !itemStack.isEmpty() && itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains(keyName);
    }

    /**
     * 获取NBT标签数据
     *
     * @param itemStack 物品栈
     * @param keyName   键名
     * @return NBT标签数据
     */
    public static CompoundTag getNBT(ItemStack itemStack, String keyName) {
        CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(keyName)) {
            putNBT(itemStack, keyName, new CompoundTag());
            tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        }
        return tag.getCompound(keyName);
    }

    /**
     * 设置物品栈的NBT标签数据
     *
     * @param itemStack 物品栈
     * @param keyName   键名
     * @param compound  NBT标签
     */
    public static void putNBT(ItemStack itemStack, String keyName, CompoundTag compound) {
        CustomData.update(DataComponents.CUSTOM_DATA, itemStack, tag -> tag.put(keyName, compound));
    }

    /**
     * 移除物品栈中指定NBT标签数据
     *
     * @param stack   物品栈
     * @param keyName 键名
     */
    public static void removeNbt(ItemStack stack, String keyName) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(keyName));
    }
}
