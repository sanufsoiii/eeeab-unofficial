package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.render.ModRenderType;
import com.eeeab.eeeabsmobs.client.render.FlatTextureRenderer;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityAlienPortal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RenderAlienPortal extends FlatTextureRenderer<EntityAlienPortal, RenderAlienPortal.AlienPortalRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/alien_portal.png");
    private static final float TEXTURE_WIDTH = 320;
    private static final float TEXTURE_HEIGHT = 64;
    private static final float SIZE = 1.9F;

    public RenderAlienPortal(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public AlienPortalRenderState createRenderState() {
        return new AlienPortalRenderState();
    }

    @Override
    public void extractRenderState(EntityAlienPortal entity, AlienPortalRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = entity.getYRot();
        state.phase = entity.getPhase();
    }

    @Override
    public void submit(AlienPortalRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F - state.yRot));
        poseStack.translate(0.0, 0.001F, 0.0);
        poseStack.scale(-SIZE, -SIZE, -SIZE);
        float minU = 0 + 64F / TEXTURE_WIDTH * state.phase;
        float minV = 0;
        float maxU = minU + 64F / TEXTURE_WIDTH;
        float maxV = minV + 64F / TEXTURE_HEIGHT;
        collector.submitCustomGeometry(poseStack, ModRenderType.getGlowingEffect(TEXTURE), (pose, vertexConsumer) -> {
            PoseStack ps = ModRenderType.seededStack(pose);
            renderFlatQuad(ps, vertexConsumer, state.lightCoords, 1F, minU, minV, maxU, maxV, SIZE, Quad.XZ);
        });
        poseStack.popPose();
    }

    public static class AlienPortalRenderState extends EntityRenderState {
        public float yRot;
        public int phase;
    }
}
