package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelMagicGolem;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.server.entity.mob.immortal.EntityMagicGolem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.resources.Identifier;
public class RenderMagicGolem extends EMMobRenderer<EntityMagicGolem, ModelMagicGolem> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_golem.png");

    public RenderMagicGolem(EntityRendererProvider.Context context) {
        super(context, new ModelMagicGolem(context.bakeLayer(ModModelLayer.MAGIC_GOLEM)), 0.3f);
        // 1.21.11: ItemInHandLayer now requires an ArmedEntityRenderState-based renderer; EMRenderState
        // (our shared bridge state) is not one, so the held-item layer is dropped until the state
        // hierarchy is reworked (visual debt: golem's held item no longer renders).
    }

    @Override
    protected void scale(EMRenderState entity, PoseStack poseStack) {
        poseStack.scale(0.8F, 0.8F, 0.8F);
    }

    @Override
    public Identifier getTextureLocation(EMRenderState entity) {
        return TEXTURE;
    }
}
