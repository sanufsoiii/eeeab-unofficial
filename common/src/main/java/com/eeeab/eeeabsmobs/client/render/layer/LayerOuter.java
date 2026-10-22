package com.eeeab.eeeabsmobs.client.render.layer;

import com.eeeab.animate.client.render.EMRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;

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
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLightIn, EMRenderState state, float yRot, float xRot) {
        T entity = (T) state.entity;
        if (entity != null && predicate.outer(entity)) {
            int overlay = overlayTexture ? LivingEntityRenderer.getOverlayCoords(state, 0F) : OverlayTexture.NO_OVERLAY;
            float alpha = getAlpha(entity, state.partialTick);
            int color = getTintColor(entity, alpha);
            VertexConsumer consumer = buffer.getBuffer(this.getRenderType());
            this.getParentModel().renderToBuffer(stack, consumer, packedLightIn, overlay, color);
        }
    }

    /** ARGB tint multiplied into the layer texture. Default reproduces the original (grey = alpha on all channels). */
    protected int getTintColor(T entity, float alpha) {
        return ARGB.color(ARGB.as8BitChannel(alpha), ARGB.as8BitChannel(alpha), ARGB.as8BitChannel(alpha), ARGB.as8BitChannel(alpha));
    }

    /**
     * 透明度
     *
     * @param entity       实体
     * @param partialTicks partialTicks
     * @return 透明度
     */
    protected float getAlpha(T entity, float partialTicks) {
        return 1F;
    }

    protected RenderType getRenderType() {
        return RenderType.entityTranslucent(this.resourceLocation);
    }

    @FunctionalInterface
    public interface OuterPredicate<T> {
        /**
         * 渲染外图层条件
         */
        boolean outer(T t);
    }
}
