package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelAbsImmortalSkeleton;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.layer.LayerGlow;
import com.eeeab.eeeabsmobs.client.render.layer.LayerVariantHolder;
import com.eeeab.eeeabsmobs.server.entity.mob.immortal.EntityAbsImmortalSkeleton;
import com.eeeab.eeeabsmobs.server.entity.mob.immortal.EntityAbsImmortalSkeleton.ClassType;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.resources.Identifier;
import java.util.Map;
public class RenderAbsImmortalSkeleton extends EMMobRenderer<EntityAbsImmortalSkeleton, ModelAbsImmortalSkeleton> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_skeleton.png");
    private static final Identifier GLOW_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_skeleton_eyes.png");
    private static final Map<ClassType, Identifier> RESOURCE_LOCATION_MAP = ImmutableMap.of(
            ClassType.ARCHER, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_skeleton/archer.png"),
            ClassType.KNIGHT, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_skeleton/knight.png"),
            ClassType.MAGE, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_skeleton/mage.png"),
            ClassType.WARRIOR, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_skeleton/warrior.png")
    );

    public RenderAbsImmortalSkeleton(EntityRendererProvider.Context context) {
        super(context, new ModelAbsImmortalSkeleton(context.bakeLayer(ModModelLayer.IMMORTAL_SKELETON)), 0.5F);
        // 1.21.11: ItemInHandLayer now requires an ArmedEntityRenderState-based renderer; EMRenderState
        // (our shared bridge state) is not one, so the held-item layer is dropped until the state
        // hierarchy is reworked (visual debt: skeleton's held item no longer renders).
        this.addLayer(new LayerGlow<>(this, GLOW_LAYER));
        this.addLayer(new LayerVariantHolder<>(this, RESOURCE_LOCATION_MAP));
    }

    @Override
    protected void scale(EMRenderState entity, PoseStack poseStack) {
        float scale = ((EntityAbsImmortalSkeleton) entity.entity).getVariant().scale;
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public Identifier getTextureLocation(EMRenderState entity) {
        return TEXTURE;
    }
}
