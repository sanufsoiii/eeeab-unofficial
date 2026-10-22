package com.eeeab.eeeabsmobs.server.item;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.item.util.ModArmorMaterial;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorType;

//TODO 未完成
//盔甲材质路径(LOCATION/LEGS_LOCATION)在1.21.1中由客户端渲染层(ArmorTrimAPI/ModelLayer)决定，
//旧Forge的getArmorTexture(stack, entity, slot, type)钩子已被移除，贴图选择迁移到client侧，
//这两个常量保留供客户端代码引用，但物品类本身不再实现该覆写方法。
public class ItemGhostWarriorArmor extends Item implements IUnbreakableItem {
    public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/armor/ghost_warrior_armor.png");
    public static final ResourceLocation LEGS_LOCATION = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/armor/ghost_warrior_armor_legs.png");

    public ItemGhostWarriorArmor(ArmorType type, net.minecraft.world.item.Item.Properties p) {
        super(p.rarity(Rarity.EPIC).fireResistant()
                .humanoidArmor(ModArmorMaterial.GHOST_WARRIOR_MATERIAL, type));
    }

    // initializeClient removed - the custom armor model is now registered directly in ClientProxy via
    // Fabric API's ArmorRenderer (see ModArmorStackRenderProperties).

    @Override
    public boolean canBreakItem() {
        //return ModConfigHandler.COMMON.items.ghostWarriorSeriesConfig1.get();
        return false;
    }
}
