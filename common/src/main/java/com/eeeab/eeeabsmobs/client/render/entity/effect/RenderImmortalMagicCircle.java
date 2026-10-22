package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.render.ModRenderType;
import com.eeeab.eeeabsmobs.client.render.FlatTextureRenderer;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityImmortalMagicCircle;
import com.eeeab.animate.client.render.EMRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
public class RenderImmortalMagicCircle extends FlatTextureRenderer<EntityImmortalMagicCircle, EMRenderState> {
    private static final Identifier NONE_TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/immortal_magic_circle/none.png");
    private static final Identifier SPEED_TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/immortal_magic_circle/speed.png");
    private static final Identifier POWER_TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/immortal_magic_circle/power.png");
    private static final Identifier HARMFUL_TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/immortal_magic_circle/harmful.png");
    private static final Identifier BENEFICIAL_TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/immortal_magic_circle/beneficial.png");

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

    private Identifier getTexture(EntityImmortalMagicCircle entity) {
        return switch (entity.getMagicCircleType()) {
            case SPEED -> SPEED_TEXTURE;
            case POWER -> POWER_TEXTURE;
            case HARMFUL -> HARMFUL_TEXTURE;
            case BENEFICIAL -> BENEFICIAL_TEXTURE;
            default -> NONE_TEXTURE;
        };
    }

    @Override
    public void submit(EMRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        EntityImmortalMagicCircle entity = (EntityImmortalMagicCircle) state.entity;
        float delta = state.partialTick;
        RenderType renderType = ModRenderType.getGlowingEffect(getTexture(entity));
        matrixStackIn.pushPose();
        float speed = (entity.tickCount + delta) * entity.getSpeed();
        float scale = entity.NO ? entity.getScale() * entity.processController.getAnimationFraction(delta) : entity.getScale();
        float alpha = entity.NO ? 1F : entity.processController.getAnimationFraction(delta);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-entity.getYaw() + 180F + speed));
        matrixStackIn.translate(0.0, 0.001F, 0.0);
        matrixStackIn.scale(-scale, -scale, -scale);
        float finalAlpha = alpha;
        collector.submitCustomGeometry(matrixStackIn, renderType, (pose, consumer) ->
                renderFlatQuad(ModRenderType.seededStack(pose), consumer, state.lightCoords, finalAlpha, 0F, 0F, 1F, 1F, 1F, Quad.XZ));
        matrixStackIn.popPose();
    }
}
