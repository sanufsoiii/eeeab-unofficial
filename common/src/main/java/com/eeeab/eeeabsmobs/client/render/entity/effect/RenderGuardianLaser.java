package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityGuardianLaser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
public class RenderGuardianLaser extends EntityRenderer<EntityGuardianLaser, RenderGuardianLaser.GuardianLaserRenderState> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/guardian_laser.png");
    private static final float TEXTURE_WIDTH = 256;
    private static final float TEXTURE_HEIGHT = 32;

    public RenderGuardianLaser(EntityRendererProvider.Context mgr) {
        super(mgr);
    }

    @Override
    public GuardianLaserRenderState createRenderState() {
        return new GuardianLaserRenderState();
    }

    @Override
    public void extractRenderState(EntityGuardianLaser laser, GuardianLaserRenderState state, float partialTick) {
        super.extractRenderState(laser, state, partialTick);
        state.collidePosX = Mth.lerp(partialTick, laser.prevCollidePosX, laser.collidePosX);
        state.collidePosY = Mth.lerp(partialTick, laser.prevCollidePosY, laser.collidePosY);
        state.collidePosZ = Mth.lerp(partialTick, laser.prevCollidePosZ, laser.collidePosZ);
        state.posX = Mth.lerp(partialTick, laser.xo, laser.getX());
        state.posY = Mth.lerp(partialTick, laser.yo, laser.getY());
        state.posZ = Mth.lerp(partialTick, laser.zo, laser.getZ());
        state.yaw = 180F / (float) Math.PI * Mth.lerp(partialTick, laser.preYaw, laser.yaw);
        state.pitch = 180F / (float) Math.PI * Mth.lerp(partialTick, laser.prePitch, laser.pitch);
        state.length = (float) Math.sqrt(
                Math.pow(state.collidePosX - state.posX, 2)
                        + Math.pow(state.collidePosY - state.posY, 2)
                        + Math.pow(state.collidePosZ - state.posZ, 2));
        state.frame = Mth.floor((laser.displayControlled.getTimer() - 1 + partialTick) * 2);
        if (state.frame < 0) state.frame = 6;
        state.partialTick = partialTick;
        state.blockSide = laser.blockSide;
        state.shouldRender = laser.isAccumulating();
        EntityGuardianLaser.UserType type = laser.getUserType();
        if (type == EntityGuardianLaser.UserType.RELIC_ANNIHILATOR && !laser.scaleControlled.isEnd() && laser.displayControlled.isEnd()) {
            state.quadRadius = type.radius * 1.75F;
            state.beamRadius = type.beamRadius * 1.5F;
        } else {
            state.quadRadius = type.radius;
            state.beamRadius = type.beamRadius;
        }
        state.playerView = laser.getOwner() instanceof Player
                && Minecraft.getInstance().player == laser.getOwner()
                && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
    }

    @Override
    public void render(GuardianLaserRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (!state.shouldRender) return;
        RenderType renderType = RenderType.entityTranslucentEmissive(TEXTURE);
        VertexConsumer builder = buffer.getBuffer(renderType);
        renderStart(state, poseStack, builder, packedLight);
        renderBeam(state, poseStack, builder, packedLight);
        poseStack.pushPose();
        poseStack.translate(state.collidePosX - state.posX, state.collidePosY - state.posY, state.collidePosZ - state.posZ);
        renderEnd(state, poseStack, builder, packedLight);
        poseStack.popPose();
        super.render(state, poseStack, buffer, packedLight);
    }

    private void renderStart(GuardianLaserRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        if (state.playerView) return;
        matrixStackIn.pushPose();
        Quaternionf quaternionf = this.entityRenderDispatcher.camera.rotation();
        matrixStackIn.mulPose(quaternionf);
        renderFlatQuad(state, matrixStackIn, builder, packedLightIn, false);
        matrixStackIn.popPose();
    }

    private void renderEnd(GuardianLaserRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        matrixStackIn.pushPose();
        Quaternionf quat = this.entityRenderDispatcher.camera.rotation();
        matrixStackIn.mulPose(quat);
        renderFlatQuad(state, matrixStackIn, builder, packedLightIn, false);
        matrixStackIn.popPose();
        if (state.blockSide == null) return;
        matrixStackIn.pushPose();
        Quaternionf rotation = state.blockSide.getRotation();
        rotation.mul(Axis.XP.rotationDegrees(90F));
        matrixStackIn.mulPose(rotation);
        matrixStackIn.translate(0, 0, -0.01F);
        renderFlatQuad(state, matrixStackIn, builder, packedLightIn, true);
        matrixStackIn.popPose();
    }

    private void renderFlatQuad(GuardianLaserRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn, boolean inGround) {
        float minU = 0 + 16F / TEXTURE_WIDTH * state.frame;
        float minV = 0;
        float maxU = minU + 16F / TEXTURE_WIDTH;
        float maxV = minV + 16F / TEXTURE_HEIGHT;
        float size = state.quadRadius + (inGround ? state.frame % 4 * 0.2F : 0);
        drawVertex(size, matrixStackIn, builder, packedLightIn, size, -size, minU, minV, maxV, maxU);
    }

    private void renderBeam(GuardianLaserRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
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

    private void drawBeam(GuardianLaserRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        float minU = 0;
        float minV = 16F / TEXTURE_HEIGHT + 1F / TEXTURE_HEIGHT * state.frame;
        float maxU = minU + 20F / TEXTURE_WIDTH;
        float maxV = minV + 1F / TEXTURE_HEIGHT;
        float offset = state.playerView ? -1 : 0;
        drawVertex(state.length, matrixStackIn, builder, packedLightIn, state.beamRadius, offset, minU, minV, maxV, maxU);
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

    public static class GuardianLaserRenderState extends EntityRenderState {
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
        public float quadRadius;
        public float beamRadius;
        public boolean playerView;
        public net.minecraft.core.Direction blockSide;
        public boolean shouldRender;
    }
}