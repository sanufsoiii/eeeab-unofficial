package com.eeeab.eeeabsmobs.client.render.layer;

import com.eeeab.animate.client.render.EMRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.RenderType;
/**
 * 生物模型外图层
 *
 * @param <T> 实体
 * @param <M> 实体模型
 */
public class LayerOuter<T extends LivingEntity, M extends EntityModel<? super EMRenderState>> extends RenderLayer<EMRenderState, M> {
    protected final ResourceLocation resourceLocation;
    protected final OuterPredicate<T> predicate;
    protected final boolean overlayTexture;

    public LayerOuter(RenderLayerParent<EMRenderState, M> parent, ResourceLocation resourceLocation) {
        this(parent, resourceLocation, true, t -> true);
    }

    public LayerOuter(RenderLayerParent<EMRenderState, M> parent, ResourceLocation resourceLocation, boolean overlayTexture) {
        this(parent, resourceLocation, overlayTexture, t -> true);
    }

    public LayerOuter(RenderLayerParent<EMRenderState, M> parent, ResourceLocation resourceLocation, boolean overlayTexture, OuterPredicate<T> predicate) {
        super(parent);
        this.resourceLocation = resourceLocation;
        this.overlayTexture = overlayTexture;
        this.predicate = predicate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void submit(PoseStack stack, SubmitNodeCollector collector, int packedLightIn, EMRenderState state, float yRot, float xRot) {
        T entity = (T) state.entity;
        if (entity != null && predicate.outer(entity)) {
            int overlay = overlayTexture ? LivingEntityRenderer.getOverlayCoords(state, 0F) : OverlayTexture.NO_OVERLAY;
            float alpha = getAlpha(entity, state.partialTick);
            int color = getTintColor(entity, alpha);
            // 10-arg overload: (…, light, overlay, COLOR, sprite=null, outline=0, crumbling=null). The 8-arg
            // form forces model color to -1 (white) and treats the 7th arg as the OUTLINE color (which renders
            // through blocks) — that was the white-through-terrain bug on every LayerOuter/LayerGlow mob.
            collector.submitModel(this.getParentModel(), state, stack, this.getRenderType(), packedLightIn, overlay, color, null, 0, null);
        }
    }

    /** ARGB tint multiplied into the layer texture. Default reproduces the original (grey = alpha on all channels). */
    protected int getTintColor(T entity, float alpha) {
        return ARGB.color(ARGB.as8BitChannel(alpha), ARGB.as8BitChannel(alpha), ARGB.as8BitChannel(alpha), ARGB.as8BitChannel(alpha));
    }

    /**
     * Render type for the outer layer. Default is translucent (matches the original mod). Subclasses may
     * override — e.g. a lit, depth-writing cutout — to stop bright inner parts bleeding through the body.
     */
    protected net.minecraft.client.renderer.RenderType getRenderType() {
        return RenderType.entityTranslucent(resourceLocation);
    }

    /**
     * 获取渲染图层透明度
     *
     * @param entity 实体
     * @return 透明度
     */
    protected float getAlpha(T entity, float partialTicks) {
        return 1F;
    }

    @FunctionalInterface
    public interface OuterPredicate<T> {
        /**
         * 渲染外图层条件
         */
        boolean outer(T t);
    }
}
