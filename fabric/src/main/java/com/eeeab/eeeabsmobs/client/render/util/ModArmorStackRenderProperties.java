package com.eeeab.eeeabsmobs.client.render.util;

import com.eeeab.eeeabsmobs.client.model.armor.ModelGhostWarriorArmor;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.server.item.ItemGhostWarriorArmor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Was IClientItemExtensions#getHumanoidArmorModel (NeoForge, model-swap only, texture picked separately by
 * vanilla). Fabric API's ArmorRenderer (fabric-rendering-v1, verified with javap against the jar) instead
 * hands back full render control per equipment slot: we call its static renderPart(...) helper - the same
 * vanilla-texture-binding/render-type logic NeoForge's own default armor rendering used - with our custom
 * model and one of the two textures ItemGhostWarriorArmor already declared (LOCATION/LEGS_LOCATION).
 */
public class ModArmorStackRenderProperties implements ArmorRenderer {
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
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ItemStack itemStack, LivingEntity entity,
                        EquipmentSlot slot, int light, HumanoidModel<LivingEntity> contextModel) {
        if (!status) {
            init();
        }
        ModelGhostWarriorArmor model = slot == EquipmentSlot.LEGS ? GHOST_WARRIOR_ARMOR_LEGS : GHOST_WARRIOR_ARMOR;
        var texture = slot == EquipmentSlot.LEGS ? ItemGhostWarriorArmor.LEGS_LOCATION : ItemGhostWarriorArmor.LOCATION;
        contextModel.copyPropertiesTo(model);
        // Which parts a piece of armour shows is per-slot, and on NeoForge vanilla did it for us because the
        // mod only swapped the model. Taking full render control on Fabric means doing it here too: hiding
        // everything and never turning anything back on drew nothing at all, which looks exactly like a
        // missing texture. Mirrors vanilla HumanoidArmorLayer's own slot-to-part mapping.
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
