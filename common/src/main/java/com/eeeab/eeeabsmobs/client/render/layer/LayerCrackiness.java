package com.eeeab.eeeabsmobs.client.render.layer;

import com.eeeab.animate.client.render.EMRenderState;
import com.eeeab.eeeabsmobs.server.entity.mob.CrackinessEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import java.util.Map;

/**
 * 生物开裂纹理图层
 *
 * @param <T> 实体
 * @param <M> 模型
 * @see CrackinessEntity
 */
public class LayerCrackiness<T extends LivingEntity & CrackinessEntity<T>, M extends EntityModel<? super EMRenderState>> extends RenderLayer<EMRenderState, M> {
    private final Map<CrackinessEntity.CrackinessType, Identifier> locationMap;

    public LayerCrackiness(RenderLayerParent<EMRenderState, M> renderer, Map<CrackinessEntity.CrackinessType, Identifier> locationMap) {
        super(renderer);
        this.locationMap = locationMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void submit(PoseStack stack, SubmitNodeCollector collector, int packedLightIn, EMRenderState state, float yRot, float xRot) {
        T entity = (T) state.entity;
        if (entity != null && !entity.isInvisible()) {
            CrackinessEntity.CrackinessType crackiness = entity.getCrackiness(entity);
            if (crackiness != CrackinessEntity.CrackinessType.NONE) {
                Identifier resourcelocation = locationMap.get(crackiness);
                if (resourcelocation == null) {
                    System.out.println("The '" + crackiness + "' phase resource location is missing");
                    return;
                }
                collector.submitModel(this.getParentModel(), state, stack, RenderTypes.entityTranslucent(resourcelocation), packedLightIn, LivingEntityRenderer.getOverlayCoords(state, 0.0F), 0xFFFFFFFF, null, 0, null);
            }
        }
    }
}
