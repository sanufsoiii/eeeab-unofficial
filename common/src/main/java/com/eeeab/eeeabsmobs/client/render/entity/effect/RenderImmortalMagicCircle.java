package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.render.ModRenderType;
import com.eeeab.eeeabsmobs.client.render.FlatTextureRenderer;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityImmortalMagicCircle;
import com.eeeab.animate.client.render.EMRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
public class RenderImmortalMagicCircle extends FlatTextureRenderer<EntityImmortalMagicCircle, EMRenderState> {
    private static final ResourceLocation NONE_TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/immortal_magic_circle/none.png");
    private static final ResourceLocation SPEED_TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/immortal_magic_circle/speed.png");
    private static final ResourceLocation POWER_TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/immortal_magic_circle/power.png");
    private static final ResourceLocation HARMFUL_TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/immortal_magic_circle/harmful.png");
    private static final ResourceLocation BENEFICIAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/immortal_magic_circle/beneficial.png");

    public RenderImmortalMagicCircle(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EMRenderState createRenderState() {
        return new EMRenderState();
    }

    @Override
    public void extractRenderState(EntityImmortalMagicCircle entity, EMRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        // Bridge state: the submit body still reads the live entity (like the other EMRenderState renderers).
        state.entity = entity;
        state.partialTick = partialTick;
    }

    private ResourceLocation getTexture(EntityImmortalMagicCircle entity) {
        return switch (entity.getMagicCircleType()) {
            case SPEED -> SPEED_TEXTURE;
            case POWER -> POWER_TEXTURE;
            case HARMFUL -> HARMFUL_TEXTURE;
            case BENEFICIAL -> BENEFICIAL_TEXTURE;
            default -> NONE_TEXTURE;
        };
    }

    @Override
    public void render(EMRenderState state, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        EntityImmortalMagicCircle entity = (EntityImmortalMagicCircle) state.entity;
        float delta = state.partialTick;
        RenderType renderType = ModRenderType.getGlowingEffect(getTexture(entity));
        VertexConsumer vertexconsumer = bufferIn.getBuffer(renderType);
        matrixStackIn.pushPose();
        float speed = (entity.tickCount + delta) * entity.getSpeed();
        float scale = entity.NO ? entity.getScale() * entity.processController.getAnimationFraction(delta) : entity.getScale();
        float alpha = entity.NO ? 1F : entity.processController.getAnimationFraction(delta);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-entity.getYaw() + 180F + speed));
        matrixStackIn.translate(0.0, 0.001F, 0.0);
        matrixStackIn.scale(-scale, -scale, -scale);
        renderFlatQuad(matrixStackIn, vertexconsumer, packedLightIn, alpha, 0F, 0F, 1F, 1F, 1F, Quad.XZ);
        matrixStackIn.popPose();
        super.render(state, matrixStackIn, bufferIn, packedLightIn);
    }
}
