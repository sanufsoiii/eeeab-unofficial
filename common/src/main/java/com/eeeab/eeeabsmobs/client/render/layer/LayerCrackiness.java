package com.eeeab.eeeabsmobs.client.render.layer;

import com.eeeab.animate.client.render.EMRenderState;
import com.eeeab.eeeabsmobs.server.entity.mob.CrackinessEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import java.util.Map;
import net.minecraft.client.renderer.RenderType;

/**
 * 生物开裂纹理图层
 *
 * @param <T> 实体
 * @param <M> 模型
 * @see CrackinessEntity
 */
public class LayerCrackiness<T extends LivingEntity & CrackinessEntity<T>, M extends EntityModel<? super EMRenderState>> extends RenderLayer<EMRenderState, M> {
    private final Map<CrackinessEntity.CrackinessType, ResourceLocation> locationMap;

    public LayerCrackiness(RenderLayerParent<EMRenderState, M> renderer, Map<CrackinessEntity.CrackinessType, ResourceLocation> locationMap) {
        super(renderer);
        this.locationMap = locationMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLightIn, EMRenderState state, float yRot, float xRot) {
        T entity = (T) state.entity;
        if (entity != null && !entity.isInvisible()) {
            CrackinessEntity.CrackinessType crackiness = entity.getCrackiness(entity);
            if (crackiness != CrackinessEntity.CrackinessType.NONE) {
                ResourceLocation resourcelocation = locationMap.get(crackiness);
                if (resourcelocation == null) {
                    System.out.println("The '" + crackiness + "' phase resource location is missing");
                    return;
                }
                VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(resourcelocation));
                this.getParentModel().renderToBuffer(stack, consumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(state, 0.0F), 0xFFFFFFFF);
            }
        }
    }
}
