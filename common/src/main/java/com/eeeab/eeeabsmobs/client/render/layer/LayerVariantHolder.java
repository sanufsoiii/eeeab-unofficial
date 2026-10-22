package com.eeeab.eeeabsmobs.client.render.layer;

import com.eeeab.animate.client.render.EMRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import com.eeeab.eeeabsmobs.server.entity.IVariantHolder;

import java.util.Map;

/**
 * 生物变种图层
 *
 * @param <V> 变种标记
 * @param <T> 实体
 * @param <M> 实体模型
 */
public class LayerVariantHolder<V, T extends Entity & IVariantHolder<V>, M extends EntityModel<? super EMRenderState>> extends RenderLayer<EMRenderState, M> {
    private final Map<V, ResourceLocation> locationMap;

    public LayerVariantHolder(RenderLayerParent<EMRenderState, M> renderer, Map<V, ResourceLocation> locationMap) {
        super(renderer);
        this.locationMap = locationMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void submit(PoseStack stack, SubmitNodeCollector collector, int packedLightIn, EMRenderState state, float yRot, float xRot) {
        T entity = (T) state.entity;
        if (entity != null && !entity.isInvisible()) {
            ResourceLocation resourcelocation = locationMap.get(entity.getVariant());
            if (resourcelocation == null) {
                return;
            }
            collector.submitModel(this.getParentModel(), state, stack, this.getRenderType(resourcelocation), packedLightIn, LivingEntityRenderer.getOverlayCoords(state, 0.0F), 0xFFFFFFFF, null, 0, null);
        }
    }

    protected RenderType getRenderType(ResourceLocation location) {
        return RenderType.entityTranslucent(location);
    }
}
