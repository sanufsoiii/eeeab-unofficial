package com.eeeab.eeeabsmobs.client.render.layer;

import com.eeeab.animate.client.render.EMRenderState;
import com.eeeab.eeeabsmobs.server.entity.mob.GlowEntity;
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
 * 生物发光图层
 *
 * @param <T> 实体
 * @param <M> 实体模型
 * @see GlowEntity
 */
public class LayerGlow<T extends LivingEntity & GlowEntity, M extends EntityModel<? super EMRenderState>> extends RenderLayer<EMRenderState, M> {
    protected final ResourceLocation location;
    protected final ConditionPredicate<T> predicate;
    protected final GlowAmountSupplier<T> supplier;
    protected final boolean overlayTexture;

    public LayerGlow(RenderLayerParent<EMRenderState, M> renderLayerParent, ResourceLocation location) {
        this(renderLayerParent, location, GlowEntity::isGlow, (entity, partialTicks) -> 1, false);
    }

    public LayerGlow(RenderLayerParent<EMRenderState, M> renderLayerParent, ResourceLocation location, float brightness) {
        this(renderLayerParent, location, GlowEntity::isGlow, (entity, partialTicks) -> brightness, false);
    }

    public LayerGlow(RenderLayerParent<EMRenderState, M> renderLayerParent, ResourceLocation location, float brightness, ConditionPredicate<T> predicate) {
        this(renderLayerParent, location, predicate, (entity, partialTicks) -> brightness, false);
    }

    public LayerGlow(RenderLayerParent<EMRenderState, M> renderLayerParent, ResourceLocation location, GlowAmountSupplier<T> supplier) {
        this(renderLayerParent, location, GlowEntity::isGlow, supplier, false);
    }

    public LayerGlow(RenderLayerParent<EMRenderState, M> renderLayerParent, ResourceLocation location, ConditionPredicate<T> predicate, GlowAmountSupplier<T> supplier) {
        this(renderLayerParent, location, predicate, supplier, false);
    }

    public LayerGlow(RenderLayerParent<EMRenderState, M> renderLayerParent, ResourceLocation location, ConditionPredicate<T> predicate, GlowAmountSupplier<T> supplier, boolean overlayTexture) {
        super(renderLayerParent);
        this.location = location;
        this.predicate = predicate;
        this.overlayTexture = overlayTexture;
        this.supplier = supplier;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLightIn, EMRenderState state, float yRot, float xRot) {
        T entity = (T) state.entity;
        if (entity != null && predicate.test(entity)) {
            float brightness = this.supplier.get(entity, state.partialTick);
            int overlay = overlayTexture ? LivingEntityRenderer.getOverlayCoords(state, 0F) : OverlayTexture.NO_OVERLAY;
            int color = ARGB.color(ARGB.as8BitChannel(brightness), ARGB.as8BitChannel(brightness), ARGB.as8BitChannel(brightness), ARGB.as8BitChannel(brightness));
            VertexConsumer consumer = buffer.getBuffer(this.getRenderType(entity));
            this.getParentModel().renderToBuffer(stack, consumer, packedLightIn, overlay, color);
        }
    }

    /**
     * 获取渲染图层的类型
     *
     * @param entity 实体
     * @return 渲染类型
     */
    protected RenderType getRenderType(T entity) {
        return RenderType.eyes(this.location);
    }

    @FunctionalInterface
    public interface ConditionPredicate<T> {
        /**
         * 渲染发光图层条件
         */
        boolean test(T t);
    }

    @FunctionalInterface
    public interface GlowAmountSupplier<T> {
        /**
         * 渲染亮度提供
         */
        float get(T t, float partialTicks);
    }
}
