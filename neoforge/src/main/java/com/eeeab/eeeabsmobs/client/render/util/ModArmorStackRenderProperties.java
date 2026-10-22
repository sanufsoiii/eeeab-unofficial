package com.eeeab.eeeabsmobs.client.render.util;

import com.eeeab.eeeabsmobs.client.model.armor.ModelGhostWarriorArmor;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.server.init.ItemInit;
import com.eeeab.eeeabsmobs.server.item.ItemGhostWarriorArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

public class ModArmorStackRenderProperties implements IClientItemExtensions {
    public static boolean status;
    public static ModelGhostWarriorArmor GHOST_WARRIOR_ARMOR;
    public static ModelGhostWarriorArmor GHOST_WARRIOR_ARMOR_LEGS;

    public static void init() {
        status = true;
        var models = Minecraft.getInstance().getEntityModels();
        GHOST_WARRIOR_ARMOR = new ModelGhostWarriorArmor(models.bakeLayer(ModModelLayer.GHOST_WARRIOR_ARMOR));
        GHOST_WARRIOR_ARMOR_LEGS = new ModelGhostWarriorArmor(models.bakeLayer(ModModelLayer.GHOST_WARRIOR_ARMOR_LEGS));
    }

    public ModArmorStackRenderProperties() {
    }

    @Override
    public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
        if (!status) {
            init();
        }
        if (itemStack.is(ItemInit.GHOST_WARRIOR_LEGGINGS.get()) || layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS) {
            return GHOST_WARRIOR_ARMOR_LEGS;
        } else if (itemStack.is(ItemInit.GHOST_WARRIOR_HELMET.get()) ||
                itemStack.is(ItemInit.GHOST_WARRIOR_CHESTPLATE.get()) ||
                itemStack.is(ItemInit.GHOST_WARRIOR_BOOTS.get())) {
            return GHOST_WARRIOR_ARMOR;
        }
        return original;
    }

    @Override
    public @Nullable Identifier getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer, Identifier _default) {
        if (stack.is(ItemInit.GHOST_WARRIOR_LEGGINGS.get()) || type == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS) {
            return ItemGhostWarriorArmor.LEGS_LOCATION;
        } else if (stack.is(ItemInit.GHOST_WARRIOR_HELMET.get()) ||
                stack.is(ItemInit.GHOST_WARRIOR_CHESTPLATE.get()) ||
                stack.is(ItemInit.GHOST_WARRIOR_BOOTS.get())) {
            return ItemGhostWarriorArmor.LOCATION;
        }
        return _default;
    }
}
