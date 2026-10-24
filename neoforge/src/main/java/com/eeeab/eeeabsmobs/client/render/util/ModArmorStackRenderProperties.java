package com.eeeab.eeeabsmobs.client.render.util;

import com.eeeab.eeeabsmobs.client.model.armor.ModelGhostWarriorArmor;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.server.init.ItemInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class ModArmorStackRenderProperties implements IClientItemExtensions {
    public static boolean status;
    public static ModelGhostWarriorArmor GHOST_WARRIOR_ARMOR;
    public static ModelGhostWarriorArmor GHOST_WARRIOR_ARMOR_LEGS;

    public static void init() {
        status = true;
        GHOST_WARRIOR_ARMOR = new ModelGhostWarriorArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayer.GHOST_WARRIOR_ARMOR));
        GHOST_WARRIOR_ARMOR_LEGS = new ModelGhostWarriorArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayer.GHOST_WARRIOR_ARMOR_LEGS));
    }

    public ModArmorStackRenderProperties() {
    }

    @Override
    public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentModel.LayerType layerType, Model original) {
        if (!status) {
            init();
        }
        if (itemStack.is(ItemInit.GHOST_WARRIOR_LEGGINGS.get()) || layerType == EquipmentModel.LayerType.HUMANOID_LEGGINGS) {
            return GHOST_WARRIOR_ARMOR_LEGS;
        } else if (itemStack.is(ItemInit.GHOST_WARRIOR_HELMET.get())
                || itemStack.is(ItemInit.GHOST_WARRIOR_CHESTPLATE.get())
                || itemStack.is(ItemInit.GHOST_WARRIOR_BOOTS.get())) {
            return GHOST_WARRIOR_ARMOR;
        }
        return original;
    }
}
