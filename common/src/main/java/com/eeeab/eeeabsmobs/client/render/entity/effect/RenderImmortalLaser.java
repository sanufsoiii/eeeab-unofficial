package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityImmortalLaser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
public class RenderImmortalLaser extends EntityRenderer<EntityImmortalLaser, RenderImmortalLaser.ImmortalLaserRenderState> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/immortal_laser.png");
    private static final float QUAD_RADIUS = 1F;
    private static final float BEAM_RADIUS = 0.6F;

    public RenderImmortalLaser(EntityRendererProvider.Context mgr) {
        super(mgr);
    }

    @Override
    public ImmortalLaserRenderState createRenderState() {
        return new ImmortalLaserRenderState();
    }

    @Override
    public void extractRenderState(EntityImmortalLaser entity, ImmortalLaserRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.collidePosX = Mth.lerp(partialTick, entity.prevCollidePosX, entity.collidePosX);
        state.collidePosY = Mth.lerp(partialTick, entity.prevCollidePosY, entity.collidePosY);
        state.collidePosZ = Mth.lerp(partialTick, entity.prevCollidePosZ, entity.collidePosZ);
        state.posX = Mth.lerp(partialTick, entity.xo, entity.getX());
        state.posY = Mth.lerp(partialTick, entity.yo, entity.getY());
        state.posZ = Mth.lerp(partialTick, entity.zo, entity.getZ());
        state.yaw = 180F / (float) Math.PI * Mth.lerp(partialTick, entity.preYaw, entity.yaw);
        state.pitch = 180F / (float) Math.PI * Mth.lerp(partialTick, entity.prePitch, entity.pitch);
        state.length = (float) Math.sqrt(
                Math.pow(state.collidePosX - state.posX, 2)
                        + Math.pow(state.collidePosY - state.posY, 2)
                        + Math.pow(state.collidePosZ - state.posZ, 2));
        state.frame = Mth.floor((entity.displayControlled.getTimer() - 1 + partialTick) * 2);
        if (state.frame < 0) state.frame = 6;
        state.partialTick = partialTick;
        state.blockSide = entity.blockSide;
        state.shouldRender = entity.isAccumulating();
    }

    @Override
    public void submit(ImmortalLaserRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        if (!state.shouldRender) return;
        RenderType renderType = RenderType.entityTranslucentEmissive(TEXTURE);
        collector.submitCustomGeometry(poseStack, renderType, (pose, builder) -> {
            com.mojang.blaze3d.vertex.PoseStack ps = com.eeeab.eeeabsmobs.client.render.ModRenderType.seededStack(pose);
            renderStart(state, ps, builder, state.lightCoords);
            renderBeam(state, ps, builder, state.lightCoords);
            ps.pushPose();
            ps.translate(state.collidePosX - state.posX, state.collidePosY - state.posY, state.collidePosZ - state.posZ);
            renderEnd(state, ps, builder, state.lightCoords);
            ps.popPose();
        });
        super.submit(state, poseStack, collector, cameraRenderState);
    }

    private void renderStart(ImmortalLaserRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        matrixStackIn.pushPose();
        Quaternionf quaternionf = this.entityRenderDispatcher.camera.rotation();
        matrixStackIn.mulPose(quaternionf);
        renderFlatQuad(state, matrixStackIn, builder, packedLightIn, false);
        matrixStackIn.popPose();
    }

    private void renderEnd(ImmortalLaserRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        matrixStackIn.pushPose();
        Quaternionf quat = this.entityRenderDispatcher.camera.rotation();
        matrixStackIn.mulPose(quat);
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(state.ageInTicks * 20F % 360F));
        if (state.blockSide != null) matrixStackIn.translate(0, state.blockSide.getStepY() * 0.25F, 0);
        renderFlatQuad(state, matrixStackIn, builder, packedLightIn, false);
        matrixStackIn.popPose();
    }

    private void renderFlatQuad(ImmortalLaserRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn, boolean inGround) {
        float minU = 0 + 16F / 256F * state.frame;
        float minV = 0;
        float maxU = minU + 16F / 256F;
        float maxV = minV + 16F / 32F;
        float size = QUAD_RADIUS + (inGround ? 0.2F : 0);
        drawVertex(size, matrixStackIn, builder, packedLightIn, size, -size, minU, minV, maxV, maxU);
    }

    private void renderBeam(ImmortalLaserRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(90F));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(state.yaw - 90F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-state.pitch));

        float cameraXRot = this.entityRenderDispatcher.camera.getXRot();

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(cameraXRot + 90F));
        drawBeam(state, matrixStackIn, builder, packedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-cameraXRot - 90F));
        drawBeam(state, matrixStackIn, builder, packedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-cameraXRot + 180F));
        drawBeam(state, matrixStackIn, builder, packedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-cameraXRot - 180F));
        drawBeam(state, matrixStackIn, builder, packedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.popPose();
    }

    private void drawBeam(ImmortalLaserRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        float minU = 0;
        float minV = 16F / 32F + 1F / 32F * state.frame;
        float maxU = minU + 20F / 256F;
        float maxV = minV + 1F / 32F;
        float offset = 0;
        drawVertex(state.length, matrixStackIn, builder, packedLightIn, BEAM_RADIUS, offset, minU, minV, maxV, maxU);
    }

    private void drawVertex(float length, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn, float size, float offset, float minU, float minV, float maxV, float maxU) {
        PoseStack.Pose entry = matrixStackIn.last();
        Matrix4f matrix4f = entry.pose();
        drawVertex(matrix4f, entry, builder, -size, offset, 0, minU, minV, packedLightIn);
        drawVertex(matrix4f, entry, builder, -size, length, 0, minU, maxV, packedLightIn);
        drawVertex(matrix4f, entry, builder, size, length, 0, maxU, maxV, packedLightIn);
        drawVertex(matrix4f, entry, builder, size, offset, 0, maxU, minV, packedLightIn);
    }

    private void drawVertex(Matrix4f matrix, PoseStack.Pose pose, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, int packedLightIn) {
        vertexBuilder.addVertex(matrix, offsetX, offsetY, offsetZ)
                .setColor(1F, 1F, 1F, 1F)
                .setUv(textureX, textureY)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLightIn)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    public static class ImmortalLaserRenderState extends EntityRenderState {
        public double posX;
        public double posY;
        public double posZ;
        public double collidePosX;
        public double collidePosY;
        public double collidePosZ;
        public float yaw;
        public float pitch;
        public float length;
        public int frame;
        public float partialTick;
        public Direction blockSide;
        public boolean shouldRender;
    }
}