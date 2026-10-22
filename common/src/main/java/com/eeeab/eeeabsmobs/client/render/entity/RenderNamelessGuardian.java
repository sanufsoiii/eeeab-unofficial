package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelNamelessGuardian;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.layer.LayerGlow;
import com.eeeab.eeeabsmobs.server.entity.mob.relicron.EntityNamelessGuardian;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
public class RenderNamelessGuardian extends EMMobRenderer<EntityNamelessGuardian, ModelNamelessGuardian> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/nameless_guardian.png");
    private static final Identifier SHOOT_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/nameless_guardian_shoot.png");
    private static final Identifier GLOW_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/nameless_guardian_glow.png");

    public RenderNamelessGuardian(EntityRendererProvider.Context context) {
        super(context, new ModelNamelessGuardian(context.bakeLayer(ModModelLayer.NAMELESS_GUARDIAN)), 1.5F);
        this.addLayer(new RenderLayer<>(this) {
            @Override
            public void submit(PoseStack stack, SubmitNodeCollector collector, int packedLightIn, EMRenderState state, float yRot, float xRot) {
                EntityNamelessGuardian entity = (EntityNamelessGuardian) state.entity;
                float coefficient = entity.getExplodeCoefficient(state.partialTick);
                if (coefficient > 0) {
                    collector.submitModel(this.getParentModel(), state, stack, RenderTypes.entityTranslucentEmissive(TEXTURE), packedLightIn, OverlayTexture.pack(OverlayTexture.u(Mth.clamp(coefficient, 0F, 1F)), OverlayTexture.v(false)), net.minecraft.util.ARGB.color(net.minecraft.util.ARGB.as8BitChannel(coefficient), net.minecraft.util.ARGB.as8BitChannel(coefficient), net.minecraft.util.ARGB.as8BitChannel(coefficient), net.minecraft.util.ARGB.as8BitChannel(coefficient)), null, 0, null);
                }
            }
        });
        this.addLayer(new LayerGlow<EntityNamelessGuardian, ModelNamelessGuardian>(this, SHOOT_LAYER, (entity, partialTicks) -> {
            float timer = entity.accumulationControlled.getPrevTimer();
            return timer * 0.1F;
        }));
        this.addLayer(new LayerGlow<EntityNamelessGuardian, ModelNamelessGuardian>(this, GLOW_LAYER, guardian -> !guardian.coreControlled.isStop(), (entity, partialTicks) -> {
            float timer = entity.coreControlled.getPrevTimer();
            return timer * 0.08F;
        }));
    }

    @Override
    protected float getFlipDegrees() {
        return 0;//获取死亡翻转角度
    }

    @Override
    protected void scale(EMRenderState state, PoseStack poseStack) {
        EntityNamelessGuardian entity = (EntityNamelessGuardian) state.entity;
        float f = entity.getExplodeCoefficient(state.partialTick);
        float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        f *= f;
        f *= f;
        float f2 = (1.0F + f * 0.2F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        poseStack.scale(f2, f3, f2);
    }


    @Override
    public Identifier getTextureLocation(EMRenderState entity) {
        return TEXTURE;
    }
}
