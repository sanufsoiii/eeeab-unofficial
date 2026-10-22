package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityCrimsonRay;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
public class RenderCrimsonRay extends EntityRenderer<EntityCrimsonRay, RenderCrimsonRay.CrimsonRayRenderState> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/crimson_ray.png");
    private static final float TEXTURE_WIDTH = 256;
    private static final float TEXTURE_HEIGHT = 32;
    private static final float START_RADIUS = 1.2f;
    private static final float BEAM_RADIUS = 0.8f;

    public RenderCrimsonRay(EntityRendererProvider.Context mgr) {
        super(mgr);
    }

    @Override
    public CrimsonRayRenderState createRenderState() {
        return new CrimsonRayRenderState();
    }

    @Override
    public void extractRenderState(EntityCrimsonRay entity, CrimsonRayRenderState state, float partialTick) {
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
        state.yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        state.blockSide = entity.blockSide;
        state.shouldRender = entity.isAccumulating();
    }

    @Override
    public void render(CrimsonRayRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
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

    private void renderStart(CrimsonRayRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        matrixStackIn.pushPose();
        float speed = state.ageInTicks * 3F;
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F - state.yRot + speed));
        matrixStackIn.translate(0.0, 0.001F, 0.0);
        renderFlatQuad(state, matrixStackIn, builder, packedLightIn, false);
        matrixStackIn.popPose();
    }

    private void renderEnd(CrimsonRayRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        matrixStackIn.pushPose();
        Quaternionf quat = this.entityRenderDispatcher.camera.rotation();
        matrixStackIn.mulPose(quat);
        matrixStackIn.translate(0, 0.1F, 0);
        renderFlatQuad(state, matrixStackIn, builder, packedLightIn, true);
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

    private void renderFlatQuad(CrimsonRayRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn, boolean faceCamera) {
        float minU = 0 + 16F / TEXTURE_WIDTH * state.frame;
        float minV = 0;
        float maxU = minU + 16F / TEXTURE_WIDTH;
        float maxV = minV + 16F / TEXTURE_HEIGHT;
        float SIZE = START_RADIUS;

        PoseStack.Pose entry = matrixStackIn.last();
        Matrix4f matrix4f = entry.pose();
        if (faceCamera) {
            drawVertex(matrix4f, entry, builder, -SIZE, -SIZE, 0, minU, minV, packedLightIn);
            drawVertex(matrix4f, entry, builder, -SIZE, SIZE, 0, minU, maxV, packedLightIn);
            drawVertex(matrix4f, entry, builder, SIZE, SIZE, 0, maxU, maxV, packedLightIn);
            drawVertex(matrix4f, entry, builder, SIZE, -SIZE, 0, maxU, minV, packedLightIn);
        } else {
            drawVertex(matrix4f, entry, builder, -SIZE, 0, -SIZE, minU, minV, packedLightIn);
            drawVertex(matrix4f, entry, builder, -SIZE, 0, SIZE, minU, maxV, packedLightIn);
            drawVertex(matrix4f, entry, builder, SIZE, 0, SIZE, maxU, maxV, packedLightIn);
            drawVertex(matrix4f, entry, builder, SIZE, 0, -SIZE, maxU, minV, packedLightIn);
        }
    }

    private void renderBeam(CrimsonRayRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
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

    private void drawBeam(CrimsonRayRenderState state, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        float minU = 0;
        float minV = 16F / TEXTURE_HEIGHT + 1F / TEXTURE_HEIGHT * state.frame;
        float maxU = minU + 20F / TEXTURE_WIDTH;
        float maxV = minV + 1F / TEXTURE_HEIGHT;
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
                .setNormal(pose, 1F, 0F, 1F);
    }

    public static class CrimsonRayRenderState extends EntityRenderState {
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
        public float yRot;
        public Direction blockSide;
        public boolean shouldRender;
    }

    public static class RenderPreAttack extends EntityRenderer<EntityCrimsonRay.PreAttack, RenderPreAttack.PreAttackRenderState> {
        private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/crimson_ray_pre.png");
        private static final float TEXTURE_WIDTH = 256;
        private static final float TEXTURE_HEIGHT = 32;
        private static final float SIZE = 1.2F;

        public RenderPreAttack(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public PreAttackRenderState createRenderState() {
            return new PreAttackRenderState();
        }

        @Override
        public void extractRenderState(EntityCrimsonRay.PreAttack entity, PreAttackRenderState state, float partialTick) {
            super.extractRenderState(entity, state, partialTick);
            state.yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
            state.phase = entity.getPhase();
        }

        @Override
        public void render(PreAttackRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
            RenderType renderType = RenderType.entityTranslucentEmissive(TEXTURE);
            VertexConsumer builder = buffer.getBuffer(renderType);
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F - state.yRot));
            poseStack.translate(0.0, 0.001F, 0.0);
            poseStack.scale(-SIZE, -SIZE, -SIZE);
            float minU = 0 + 32F / TEXTURE_WIDTH * state.phase;
            float minV = 0;
            float maxU = minU + 32F / TEXTURE_WIDTH;
            float maxV = minV + 32F / TEXTURE_HEIGHT;
            PoseStack.Pose entry = poseStack.last();
            Matrix4f matrix4f = entry.pose();
            drawVertex(matrix4f, entry, builder, -SIZE, 0, -SIZE, minU, minV, packedLight);
            drawVertex(matrix4f, entry, builder, -SIZE, 0, SIZE, minU, maxV, packedLight);
            drawVertex(matrix4f, entry, builder, SIZE, 0, SIZE, maxU, maxV, packedLight);
            drawVertex(matrix4f, entry, builder, SIZE, 0, -SIZE, maxU, minV, packedLight);
            poseStack.popPose();
            super.render(state, poseStack, buffer, packedLight);
        }

        private void drawVertex(Matrix4f matrix, PoseStack.Pose pose, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, int packedLightIn) {
            vertexBuilder.addVertex(matrix, offsetX, offsetY, offsetZ)
                    .setColor(1F, 1F, 1F, 1F)
                    .setUv(textureX, textureY)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(packedLightIn)
                    .setNormal(pose, 0F, 1F, 0F);
        }

        public static class PreAttackRenderState extends EntityRenderState {
            public float yRot;
            public int phase;
        }
    }
}