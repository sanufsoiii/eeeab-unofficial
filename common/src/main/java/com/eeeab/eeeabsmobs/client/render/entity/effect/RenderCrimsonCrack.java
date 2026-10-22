package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.render.ModRenderType;
import com.eeeab.eeeabsmobs.client.render.FlatTextureRenderer;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityCrimsonCrack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

public class RenderCrimsonCrack extends FlatTextureRenderer<EntityCrimsonCrack, RenderCrimsonCrack.CrimsonCrackRenderState> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/crimson_crack.png");
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
    public void render(CrimsonCrackRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float alpha = state.fraction;
        VertexConsumer consumer = buffer.getBuffer(ModRenderType.getGlowingEffect(TEXTURE));
        poseStack.pushPose();
        Quaternionf quaternionf = this.entityRenderDispatcher.cameraOrientation();
        poseStack.mulPose(quaternionf);
        poseStack.scale(-1.5F, -1.5F, -1.5F);
        float minU = 0 + 32F / TEXTURE_WIDTH * state.phase;
        float minV = 0;
        float maxU = minU + 32F / TEXTURE_WIDTH;
        float maxV = minV + 32F / TEXTURE_HEIGHT;
        renderFlatQuad(poseStack, consumer, packedLight, alpha, minU, minV, maxU, maxV, 1F, Quad.XY);
        poseStack.popPose();
        super.render(state, poseStack, buffer, packedLight);
    }

    public static class CrimsonCrackRenderState extends EntityRenderState {
        public float fraction;
        public int phase;
    }
}
