package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.effect.ModelBloodBall;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.ModRenderType;
import com.eeeab.eeeabsmobs.client.render.state.BloodBallRenderState;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityBloodBall;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RendererBloodBall extends EntityRenderer<EntityBloodBall, BloodBallRenderState> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/blood_ball.png");
    private final ModelBloodBall model;

    public RendererBloodBall(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ModelBloodBall(context.bakeLayer(ModModelLayer.BLOOD_BALL));
    }

    @Override
    public void render(BloodBallRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        VertexConsumer vertexconsumer = buffer.getBuffer(ModRenderType.entityTranslucent(TEXTURE));
        poseStack.translate(0, 0.5F, 0);
        float s = state.scale * state.progress;
        poseStack.scale(s, s, s);
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(state.f * 0.1F) * 180.0F));
        this.model.ball_3.render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(state.f * 0.1F) * 180.0F));
        this.model.ball_2.render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(state.f * 0.1F) * 180.0F));
        this.model.ball_1.render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(state, poseStack, buffer, packedLight);
    }

    @Override
    public BloodBallRenderState createRenderState() {
        return new BloodBallRenderState();
    }

    @Override
    public void extractRenderState(EntityBloodBall entity, BloodBallRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.scale = 2F + entity.getPower() * 0.1F;
        state.progress = entity.scaleControlled.getAnimationFraction(partialTick);
        state.f = ((float) entity.tickCount + partialTick) * Mth.clamp(2F * state.progress, 0.1F, 1F);
        state.yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
    }
}