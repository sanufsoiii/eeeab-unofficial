package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.effect.ModelPulsedGrenade;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.state.PulsedGrenadeRenderState;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityPulsedGrenade;
import com.eeeab.eeeabsmobs.client.render.ModRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class RenderPulsedGrenade extends EntityRenderer<EntityPulsedGrenade, PulsedGrenadeRenderState> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/pulsed_grenade.png");
    private final ModelPulsedGrenade model;

    public RenderPulsedGrenade(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ModelPulsedGrenade(context.bakeLayer(ModModelLayer.PULSED_GRENADE));
    }

    @Override
    public void render(PulsedGrenadeRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(0F, 0.5F, 0F);
        this.model.ball.yRot = state.yRot * ((float) Math.PI / 180F);
        this.model.ball.xRot = state.xRot * ((float) Math.PI / 180F);
        this.model.renderToBuffer(poseStack, buffer.getBuffer(ModRenderType.getGlowingEffect(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
        super.render(state, poseStack, buffer, packedLight);
    }

    @Override
    public PulsedGrenadeRenderState createRenderState() {
        return new PulsedGrenadeRenderState();
    }

    @Override
    public void extractRenderState(EntityPulsedGrenade entity, PulsedGrenadeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = entity.getViewYRot(partialTick);
        state.xRot = (entity.tickCount + partialTick) * Math.min(30, entity.tickCount * 2);
    }
}