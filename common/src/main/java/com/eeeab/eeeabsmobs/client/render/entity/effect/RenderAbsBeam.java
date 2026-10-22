package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.client.render.ModRenderType;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityAbsBeam;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
//基于自: https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/client/render/entity/RenderSolarBeam.java
public abstract class RenderAbsBeam<T extends EntityAbsBeam, S extends EntityRenderState> extends EntityRenderer<T, S> {
    private static final float TEXTURE_WIDTH = 256;
    private static final float TEXTURE_HEIGHT = 32;
    protected float quadRadius;
    protected float beamRadius;

    public RenderAbsBeam(EntityRendererProvider.Context mgr, float quadRadius, float beamRadius) {
        super(mgr);
        this.quadRadius = quadRadius;
        this.beamRadius = beamRadius;
    }

    @Override
    protected boolean affectedByCulling(T entity) {
        return false;
    }

    public abstract ResourceLocation getTextureLocation(T entity);

    @Override
    public S createRenderState() {
        return (S) new AbsBeamRenderState();
    }

    @Override
    public void extractRenderState(T laser, S state, float partialTick) {
        super.extractRenderState(laser, state, partialTick);
        AbsBeamRenderState s = (AbsBeamRenderState) state;
        s.entity = laser;
        s.partialTick = partialTick;
        s.prevCollidePosX = laser.prevCollidePosX;
        s.prevCollidePosY = laser.prevCollidePosY;
        s.prevCollidePosZ = laser.prevCollidePosZ;
        s.collidePosX = laser.collidePosX;
        s.collidePosY = laser.collidePosY;
        s.collidePosZ = laser.collidePosZ;
        s.posX = laser.xo + (laser.getX() - laser.xo) * partialTick;
        s.posY = laser.yo + (laser.getY() - laser.yo) * partialTick;
        s.posZ = laser.zo + (laser.getZ() - laser.zo) * partialTick;
        s.yaw = laser.preYaw + (laser.yaw - laser.preYaw) * partialTick;
        s.pitch = laser.prePitch + (laser.pitch - laser.prePitch) * partialTick;
        s.frame = Mth.floor((laser.displayControlled.getTimer() - 1 + partialTick) * 2);
        if (s.frame < 0) {
            s.frame = 6;
        }
        s.accumulating = laser.isAccumulating();
        s.blockSide = laser.blockSide;
        s.texture = getTextureLocation(laser);
    }

    @Override
    public void render(S state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        AbsBeamRenderState s = (AbsBeamRenderState) state;
        if (!s.accumulating) return;
        double collidePosX = s.prevCollidePosX + (s.collidePosX - s.prevCollidePosX) * s.partialTick;
        double collidePosY = s.prevCollidePosY + (s.collidePosY - s.prevCollidePosY) * s.partialTick;
        double collidePosZ = s.prevCollidePosZ + (s.collidePosZ - s.prevCollidePosZ) * s.partialTick;
        float length = (float) Math.sqrt(Math.pow(collidePosX - s.posX, 2) + Math.pow(collidePosY - s.posY, 2) + Math.pow(collidePosZ - s.posZ, 2));
        T laser = (T) s.entity;
        VertexConsumer vertexConsumer = buffer.getBuffer(ModRenderType.getGlowingEffect(s.texture));
        renderStart(laser, s.frame, poseStack, vertexConsumer, s.partialTick, packedLight);
        renderBeam(length, 180f / (float) Math.PI * s.yaw, 180f / (float) Math.PI * s.pitch, s.frame, poseStack, vertexConsumer, packedLight);
        poseStack.pushPose();
        poseStack.translate(collidePosX - s.posX, collidePosY - s.posY, collidePosZ - s.posZ);
        renderEnd(laser, s.frame, s.blockSide, poseStack, vertexConsumer, s.partialTick, packedLight);
        poseStack.popPose();
        super.render(state, poseStack, buffer, packedLight);
    }

    //渲染四边形平面
    protected void renderFlatQuad(int frame, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn, boolean inGround) {
        float minU = 0 + 16F / TEXTURE_WIDTH * frame;
        float minV = 0;
        float maxU = minU + 16F / TEXTURE_WIDTH;
        float maxV = minV + 16F / TEXTURE_HEIGHT;
        float size = this.quadRadius + (inGround ? 0.2F : 0);
        drawVertex(size, matrixStackIn, builder, packedLightIn, size, -size, minU, minV, maxV, maxU);
    }

    //渲染激光起始位置
    protected void renderStart(T entity, int frame, PoseStack matrixStackIn, VertexConsumer builder, float delta, int packedLightIn) {
        matrixStackIn.pushPose();
        Quaternionf quaternionf = this.entityRenderDispatcher.camera.rotation();
        matrixStackIn.mulPose(quaternionf);
        renderFlatQuad(frame, matrixStackIn, builder, packedLightIn, false);
        matrixStackIn.popPose();
    }

    //渲染激光后半部分
    protected void renderEnd(T entity, int frame, Direction side, PoseStack matrixStackIn, VertexConsumer builder, float delta, int packedLightIn) {
        matrixStackIn.pushPose();
        Quaternionf quat = this.entityRenderDispatcher.camera.rotation();
        matrixStackIn.mulPose(quat);
        renderFlatQuad(frame, matrixStackIn, builder, packedLightIn, false);
        matrixStackIn.popPose();
        //判断有没有接触地面
        if (side == null) {
            return;
        }
        //渲染激光照射在地面的纹理
        matrixStackIn.pushPose();
        Quaternionf rotation = side.getRotation();
        rotation.mul(Axis.XP.rotationDegrees(90F));
        matrixStackIn.mulPose(rotation);
        matrixStackIn.translate(0, 0, -0.01f);
        renderFlatQuad(frame, matrixStackIn, builder, packedLightIn, true);
        matrixStackIn.popPose();
    }

    //渲染光束
    protected void renderBeam(float length, float yaw, float pitch, int frame, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        matrixStackIn.pushPose();
        //旋转角度
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(90F));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(yaw - 90F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-pitch));

        float cameraXRot = this.entityRenderDispatcher.camera.getXRot();

        //绘制一根射线
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(cameraXRot + 90F));
        drawBeam(length, frame, matrixStackIn, builder, packedLightIn);
        matrixStackIn.popPose();

        //绘制一根射线
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-cameraXRot - 90F));
        drawBeam(length, frame, matrixStackIn, builder, packedLightIn);
        matrixStackIn.popPose();

        //绘制一根射线
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-cameraXRot + 180F));
        drawBeam(length, frame, matrixStackIn, builder, packedLightIn);
        matrixStackIn.popPose();

        //绘制一根射线
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-cameraXRot - 180F));
        drawBeam(length, frame, matrixStackIn, builder, packedLightIn);
        matrixStackIn.popPose();

        matrixStackIn.popPose();
    }

    //绘制光束
    protected void drawBeam(float length, int frame, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        float minU = 0;
        //通过控制帧数,来渲染二维UV的最高位置
        float minV = 16 / TEXTURE_HEIGHT + 1 / TEXTURE_HEIGHT * frame;
        float maxU = minU + 20 / TEXTURE_WIDTH;
        float maxV = minV + 1 / TEXTURE_HEIGHT;
        float offset = 0;
        drawVertex(length, matrixStackIn, builder, packedLightIn, this.beamRadius, offset, minU, minV, maxV, maxU);
    }

    protected void drawVertex(float length, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn, float size, float offset, float minU, float minV, float maxV, float maxU) {
        PoseStack.Pose matrix$stack$entry = matrixStackIn.last();
        Matrix4f matrix4f = matrix$stack$entry.pose();
        drawVertex(matrix4f, matrix$stack$entry, builder, -size, offset, 0, minU, minV, packedLightIn);
        drawVertex(matrix4f, matrix$stack$entry, builder, -size, length, 0, minU, maxV, packedLightIn);
        drawVertex(matrix4f, matrix$stack$entry, builder, size, length, 0, maxU, maxV, packedLightIn);
        drawVertex(matrix4f, matrix$stack$entry, builder, size, offset, 0, maxU, minV, packedLightIn);
    }

    protected void drawVertex(Matrix4f matrix, PoseStack.Pose pose, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, int packedLightIn) {
        vertexBuilder.addVertex(matrix, offsetX, offsetY, offsetZ).setColor(1F, 1F, 1F, 1F).setUv(textureX, textureY).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    public static class AbsBeamRenderState extends EntityRenderState {
        public net.minecraft.world.entity.Entity entity;
        public float partialTick;
        public double prevCollidePosX;
        public double prevCollidePosY;
        public double prevCollidePosZ;
        public double collidePosX;
        public double collidePosY;
        public double collidePosZ;
        public double posX;
        public double posY;
        public double posZ;
        public float yaw;
        public float pitch;
        public int frame;
        public boolean accumulating;
        public Direction blockSide;
        public ResourceLocation texture;
    }
}
