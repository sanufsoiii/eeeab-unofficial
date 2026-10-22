package com.eeeab.eeeabsmobs.client.render.util;

import com.eeeab.eeeabsmobs.client.model.armor.ModelGhostWarriorArmor;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.server.item.ItemGhostWarriorArmor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.RenderType;

/**
 * Was IClientItemExtensions#getHumanoidArmorModel (NeoForge, model-swap only, texture picked separately by
 * vanilla). Fabric API's ArmorRenderer (fabric-rendering-v1, verified with javap against the jar) instead
 * hands back full render control per equipment slot: we call its static renderPart(...) helper - the same
 * vanilla-texture-binding/render-type logic NeoForge's own default armor rendering used - with our custom
 * model and one of the two textures ItemGhostWarriorArmor already declared (LOCATION/LEGS_LOCATION).
 */
public class ModArmorStackRenderProperties implements ArmorRenderer {
    public static boolean status;
    // One model instance PER SLOT. submitModel is deferred: the collector reads a model's per-part `visible`
    // flags at draw time, not at submit time. Sharing one instance across HEAD/CHEST/FEET meant every deferred
    // submit rendered whatever the last slot configured — so only one worn piece showed. Distinct instances
    // (head/chest/feet from the outer layer, legs from the legs layer) keep each slot's visibility intact.
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
    public void render(PoseStack poseStack, SubmitNodeCollector queue, ItemStack itemStack, HumanoidRenderState renderState,
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
        // Replaces the old contextModel.copyPropertiesTo(model): with render states the shared
        // HumanoidRenderState carries the pose, and setupAnim(renderState) applies it to our model
        // (submitModel below would call setupAnim with this state anyway).
        model.setupAnim(renderState);
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
        // ArmorRenderer.renderPart was removed; submit the model directly through the ordered
        // collector the same way vanilla's HumanoidArmorLayer submits its armor models.
        queue.submitModel(model, renderState, poseStack, RenderType.armorCutoutNoCull(texture), light, OverlayTexture.NO_OVERLAY, -1, null, 0, null);
    }
}
