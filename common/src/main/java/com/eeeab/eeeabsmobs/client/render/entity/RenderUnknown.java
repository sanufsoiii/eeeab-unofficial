package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelUnKnown;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.server.entity.EEEABMobLibrary;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.resources.Identifier;
//未知模型渲染 作为占位符使用
public class RenderUnknown<T extends EEEABMobLibrary & AnimatedEntity> extends EMMobRenderer<T, ModelUnKnown<T>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/unknown.png");
    private final float scale;

    public RenderUnknown(EntityRendererProvider.Context context, float scale, float shadowRadius) {
        super(context, new ModelUnKnown<>(context.bakeLayer(ModModelLayer.UNKNOWN)), shadowRadius);
        this.scale = scale;
    }

    @Override
    protected void scale(EMRenderState entity, PoseStack matrixStack) {
        matrixStack.scale(scale, scale, scale);
    }

    @Override
    public Identifier getTextureLocation(EMRenderState entity) {
        return TEXTURE;
    }
}
