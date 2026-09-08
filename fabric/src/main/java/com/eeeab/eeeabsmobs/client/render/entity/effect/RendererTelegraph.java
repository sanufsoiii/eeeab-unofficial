package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.server.entity.effect.EntityTelegraph;
import com.eeeab.animate.client.render.EMRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class RendererTelegraph extends EntityRenderer<EntityTelegraph, EMRenderState> {
    private static final Identifier WHITE_TEXTURE = Identifier.parse("minecraft:textures/block/white_concrete.png");
    private static final float GROUND_LEVEL = 0.05F;
    private static final float OUTER_BOTTOM_ALPHA = 0.4F;
    private static final float INNER_BOTTOM_ALPHA = 0.3F;
    private static final float WALL_BASE_HEIGHT = 0.3F;
    private static final float WALL_MAX_HEIGHT = 1F;
    private static final float WALL_BASE_ALPHA = 0.2F;
    private static final float WALL_MAX_ALPHA = 0.6F;

    public RendererTelegraph(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected boolean affectedByCulling(EntityTelegraph entity) {
        return false;
    }

    @Override
    public EMRenderState createRenderState() {
        return new EMRenderState();
    }

    @Override
    public void extractRenderState(EntityTelegraph entity, EMRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        // Bridge state: the submit body still reads the live entity (like the other EMRenderState renderers).
        state.entity = entity;
        state.partialTick = partialTick;
    }

    @Override
    public void submit(EMRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        EntityTelegraph entity = (EntityTelegraph) renderState.entity;
        float partialTick = renderState.partialTick;
        if (entity == null || entity.controlled == null) return;
        RenderType renderType = RenderTypes.entityTranslucentEmissive(WHITE_TEXTURE);
        int packedLight = renderState.lightCoords;
        collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) ->
            renderGeometry(pose, consumer, packedLight, entity, partialTick));
    }

    private void renderGeometry(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, EntityTelegraph entity, float partialTick) {
        float progress = entity.controlled.getAnimationProgressSinSqrt(partialTick);
        float interimProgress = entity.interimControlled.getAnimationProgressSinSqrt(partialTick);
        float radius = entity.getRadius();
        int color = entity.getColor();

        float[] rgb = extractRgb(color);
        float red = rgb[0];
        float green = rgb[1];
        float blue = rgb[2];

        if (interimProgress > 0) {
            red = Math.min(1f, red + interimProgress);
            green = Math.min(1f, green + interimProgress);
            blue = Math.min(1f, blue + interimProgress);
        }

        float controlledFraction = entity.controlled.getAnimationFraction(partialTick);
        float controlledTimer = controlledFraction * entity.controlled.getDuration();
        float controlledFadeIn = Math.min(controlledTimer / 3.0f, 1.0f);
        float interimFraction = entity.interimControlled.getAnimationFraction(partialTick);
        float interimTimer = interimFraction * entity.interimControlled.getDuration();
        float remaining = entity.interimControlled.getDuration() - interimTimer;
        float interimFadeOut = Math.min(remaining / 3.0f, 1.0f);
        float fadeFactor = controlledFadeIn * interimFadeOut;

        //外圈
        float outerBottomAlpha = OUTER_BOTTOM_ALPHA * fadeFactor;
        renderSquare(pose, consumer, packedLight, radius, GROUND_LEVEL, red, green, blue, outerBottomAlpha);

        //外圈垂直面
        float wallHeight = WALL_BASE_HEIGHT + (WALL_MAX_HEIGHT - WALL_BASE_HEIGHT) * progress;
        float wallAlphaBottom = WALL_BASE_ALPHA + (WALL_MAX_ALPHA - WALL_BASE_ALPHA) * progress;
        float wallAlpha = wallAlphaBottom * fadeFactor;
        renderOuterWalls(pose, consumer, packedLight, radius, GROUND_LEVEL, wallHeight, wallAlpha, red, green, blue);

        //内圈
        float innerRadius = radius * progress;
        if (innerRadius > 0.01F) {
            renderSquare(pose, consumer, packedLight, innerRadius, GROUND_LEVEL, red, green, blue, INNER_BOTTOM_ALPHA);
        }
    }

    private static float[] extractRgb(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return new float[]{r / 255.0f, g / 255.0f, b / 255.0f};
    }


    //渲染平面的方形底面
    private static void renderSquare(PoseStack.Pose pose, VertexConsumer consumer, int packedLight,
                                     float size, float y, float red, float green, float blue, float alpha) {
        Matrix4f matrix = pose.pose();
        float nx = 0, ny = 1, nz = 0;
        consumer.addVertex(matrix, -size, y, -size).setColor(red, green, blue, alpha).setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, nx, ny, nz);
        consumer.addVertex(matrix, -size, y, size).setColor(red, green, blue, alpha).setUv(0, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, nx, ny, nz);
        consumer.addVertex(matrix, size, y, size).setColor(red, green, blue, alpha).setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, nx, ny, nz);
        consumer.addVertex(matrix, size, y, -size).setColor(red, green, blue, alpha).setUv(1, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, nx, ny, nz);
    }


    //渲染四个垂直面
    private static void renderOuterWalls(PoseStack.Pose pose, VertexConsumer consumer, int packedLight,
                                         float radius, float yBase, float height, float alphaBottom,
                                         float red, float green, float blue) {
        float[][] faceNormals = {{-1, 0, 0}, {1, 0, 0}, {0, 0, -1}, {0, 0, 1}};
        for (int i = 0; i < 4; i++) {
            float nx = faceNormals[i][0];
            float ny = faceNormals[i][1];
            float nz = faceNormals[i][2];
            float x1, z1, x2, z2;
            switch (i) {
                case 0:
                    x1 = radius;
                    z1 = -radius;
                    x2 = radius;
                    z2 = radius;
                    break;
                case 1:
                    x1 = -radius;
                    z1 = -radius;
                    x2 = -radius;
                    z2 = radius;
                    break;
                case 2:
                    x1 = -radius;
                    z1 = radius;
                    x2 = radius;
                    z2 = radius;
                    break;
                case 3:
                    x1 = -radius;
                    z1 = -radius;
                    x2 = radius;
                    z2 = -radius;
                    break;
                default:
                    continue;
            }
            renderVerticalFace(pose, consumer, packedLight, x1, z1, x2, z2, nx, ny, nz, yBase, height, alphaBottom, red, green, blue);
        }
    }

    //渲染单个垂直矩形面
    private static void renderVerticalFace(PoseStack.Pose pose, VertexConsumer consumer, int packedLight,
                                           float x1, float z1, float x2, float z2,
                                           float nx, float ny, float nz,
                                           float yBase, float height, float alphaBottom,
                                           float red, float green, float blue) {
        Matrix4f matrix = pose.pose();
        float alphaTop = 0F;
        consumer.addVertex(matrix, x1, yBase, z1).setColor(red, green, blue, alphaBottom).setUv(0, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, nx, ny, nz);
        consumer.addVertex(matrix, x2, yBase, z2).setColor(red, green, blue, alphaBottom).setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, nx, ny, nz);
        consumer.addVertex(matrix, x2, yBase + height, z2).setColor(red, green, blue, alphaTop).setUv(1, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, nx, ny, nz);
        consumer.addVertex(matrix, x1, yBase + height, z1).setColor(red, green, blue, alphaTop).setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, nx, ny, nz);
    }

}
