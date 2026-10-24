package com.eeeab.eeeabsmobs.client.render.util;

import com.eeeab.eeeabsmobs.client.model.armor.ModelGhostWarriorArmor;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ModArmorStackRenderProperties implements IClientItemExtensions {
    private static ModelGhostWarriorArmor GHOST_WARRIOR_ARMOR;
    private static ModelGhostWarriorArmor GHOST_WARRIOR_ARMOR_LEGS;

    @Override
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
        if (GHOST_WARRIOR_ARMOR == null) {
            GHOST_WARRIOR_ARMOR = new ModelGhostWarriorArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayer.GHOST_WARRIOR_ARMOR));
            GHOST_WARRIOR_ARMOR_LEGS = new ModelGhostWarriorArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayer.GHOST_WARRIOR_ARMOR_LEGS));
        }
        return slot == EquipmentSlot.LEGS ? GHOST_WARRIOR_ARMOR_LEGS : GHOST_WARRIOR_ARMOR;
    }
}
