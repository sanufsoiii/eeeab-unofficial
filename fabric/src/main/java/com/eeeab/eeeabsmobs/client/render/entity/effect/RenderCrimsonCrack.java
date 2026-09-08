package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.render.ModRenderType;
import com.eeeab.eeeabsmobs.client.render.FlatTextureRenderer;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityCrimsonCrack;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RenderCrimsonCrack extends FlatTextureRenderer<EntityCrimsonCrack, RenderCrimsonCrack.CrimsonCrackRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/crimson_crack.png");
    private static final float TEXTURE_WIDTH = 256;
    private static final float TEXTURE_HEIGHT = 32;

    public RenderCrimsonCrack(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public CrimsonCrackRenderState createRenderState() {
        return new CrimsonCrackRenderState();
    }

    @Override
    public void extractRenderState(EntityCrimsonCrack entity, CrimsonCrackRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.fraction = entity.displayControlled.getAnimationFraction();
        state.phase = entity.getPhase();
    }

    @Override
    public void submit(CrimsonCrackRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        float alpha = state.fraction;
        poseStack.pushPose();
        poseStack.mulPose(cameraRenderState.orientation);
        poseStack.scale(-1.5F, -1.5F, -1.5F);
        float minU = 0 + 32F / TEXTURE_WIDTH * state.phase;
        float minV = 0;
        float maxU = minU + 32F / TEXTURE_WIDTH;
        float maxV = minV + 32F / TEXTURE_HEIGHT;
        collector.submitCustomGeometry(poseStack, ModRenderType.getGlowingEffect(TEXTURE), (pose, vertexConsumer) -> {
            PoseStack ps = ModRenderType.seededStack(pose);
            renderFlatQuad(ps, vertexConsumer, state.lightCoords, alpha, minU, minV, maxU, maxV, 1F, Quad.XY);
        });
        poseStack.popPose();
    }

    public static class CrimsonCrackRenderState extends EntityRenderState {
        public float fraction;
        public int phase;
    }
}
