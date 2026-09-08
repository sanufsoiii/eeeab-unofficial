package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.effect.ModelBloodBall;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.state.BloodBallRenderState;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityBloodBall;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public class RendererBloodBall extends EntityRenderer<EntityBloodBall, BloodBallRenderState> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/blood_ball.png");
    private final ModelBloodBall model;

    public RendererBloodBall(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ModelBloodBall(context.bakeLayer(ModModelLayer.BLOOD_BALL));
    }

    @Override
    public void submit(BloodBallRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0, 0.5F, 0);
        float s = state.scale * state.progress;
        poseStack.scale(s, s, s);
        float sinRad = Mth.sin(state.f * 0.1F) * (float) Math.PI;
        this.model.ball_1.xRot = sinRad;
        this.model.ball_1.yRot = sinRad;
        this.model.ball_1.zRot = sinRad;
        this.model.ball_2.xRot = sinRad;
        this.model.ball_2.yRot = sinRad;
        this.model.ball_3.xRot = sinRad;
        List<RenderType> list = ItemRenderer.getFoilRenderTypes(this.model.renderType(TEXTURE), false, false);
        for (int i = 0; i < list.size(); i++) {
            collector.order(i).submitModel(
                this.model,
                Unit.INSTANCE,
                poseStack,
                list.get(i),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                -1,
                null,
                state.outlineColor,
                null
            );
        }
        poseStack.popPose();
        super.submit(state, poseStack, collector, cameraRenderState);
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