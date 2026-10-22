package com.eeeab.eeeabsmobs.client.render.util;

import com.eeeab.eeeabsmobs.client.model.armor.ModelGhostWarriorArmor;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.server.item.ItemGhostWarriorArmor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ModArmorStackRenderProperties implements ArmorRenderer {
    public static boolean status;
    public static ModelGhostWarriorArmor GHOST_WARRIOR_ARMOR_HEAD;
    public static ModelGhostWarriorArmor GHOST_WARRIOR_ARMOR_CHEST;
    public static ModelGhostWarriorArmor GHOST_WARRIOR_ARMOR_FEET;
    public static ModelGhostWarriorArmor GHOST_WARRIOR_ARMOR_LEGS;

    public static void init() {
        status = true;
        var models = Minecraft.getInstance().getEntityModels();
        GHOST_WARRIOR_ARMOR_HEAD = new ModelGhostWarriorArmor(models.bakeLayer(ModModelLayer.GHOST_WARRIOR_ARMOR));
        GHOST_WARRIOR_ARMOR_CHEST = new ModelGhostWarriorArmor(models.bakeLayer(ModModelLayer.GHOST_WARRIOR_ARMOR));
        GHOST_WARRIOR_ARMOR_FEET = new ModelGhostWarriorArmor(models.bakeLayer(ModModelLayer.GHOST_WARRIOR_ARMOR));
        GHOST_WARRIOR_ARMOR_LEGS = new ModelGhostWarriorArmor(models.bakeLayer(ModModelLayer.GHOST_WARRIOR_ARMOR_LEGS));
    }

    public ModArmorStackRenderProperties() {
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ItemStack itemStack, HumanoidRenderState renderState,
                        EquipmentSlot slot, int light, HumanoidModel<HumanoidRenderState> contextModel) {
        if (!status) {
            init();
        }
        ModelGhostWarriorArmor model = switch (slot) {
            case HEAD -> GHOST_WARRIOR_ARMOR_HEAD;
            case LEGS -> GHOST_WARRIOR_ARMOR_LEGS;
            case FEET -> GHOST_WARRIOR_ARMOR_FEET;
            default -> GHOST_WARRIOR_ARMOR_CHEST;
        };
        var texture = slot == EquipmentSlot.LEGS ? ItemGhostWarriorArmor.LEGS_LOCATION : ItemGhostWarriorArmor.LOCATION;
        model.setupAnim(renderState);
        model.setAllVisible(false);
        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = true;
            }
            case CHEST -> {
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
            }
            case LEGS -> {
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            case FEET -> {
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            default -> {
            }
        }
        ArmorRenderer.renderPart(poseStack, bufferSource, light, itemStack, model, texture);
    }
}
