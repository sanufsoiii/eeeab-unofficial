package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.effect.ModelGuardianBlade;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.ModRenderType;
import com.eeeab.eeeabsmobs.client.render.state.GuardianBladeRenderState;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityGuardianBlade;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class RenderGuardianBlade extends EntityRenderer<EntityGuardianBlade, GuardianBladeRenderState> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[6];
    private final ModelGuardianBlade model;

    public RenderGuardianBlade(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ModelGuardianBlade(context.bakeLayer(ModModelLayer.GUARDIAN_BLADE));
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/guardian_blade/index_" + (i + 1) + ".png");
        }
    }

    @Override
    public void render(GuardianBladeRenderState state, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.translate(0F, 1.5F, 0F);
        poseStack.scale(0.65F, -1.0F, -0.65F);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));
        VertexConsumer buffer = bufferIn.getBuffer(ModRenderType.getGlowingEffect(TEXTURES[state.textureIndex]));
        this.model.renderToBuffer(poseStack, buffer, packedLightIn, OverlayTexture.NO_OVERLAY, state.color);
        poseStack.popPose();
        super.render(state, poseStack, bufferIn, packedLightIn);
    }

    @Override
    protected int getBlockLightLevel(EntityGuardianBlade entity, BlockPos pos) {
        return 15;
    }

    @Override
    public GuardianBladeRenderState createRenderState() {
        return new GuardianBladeRenderState();
    }

    @Override
    public void extractRenderState(EntityGuardianBlade entity, GuardianBladeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.textureIndex = Mth.clamp((int) (entity.tickCount * 0.5 % TEXTURES.length), 0, TEXTURES.length - 1);
        float animationProgress = Math.min(entity.controlled.getAnimationFraction(partialTick) + 0.1F, 1F);
        float f = animationProgress > 0.5F ? (1.0F - (animationProgress - 0.5F) * 2.0F) : 1.0F;
        state.color = ARGB.color(ARGB.as8BitChannel(f), ARGB.as8BitChannel(f), ARGB.as8BitChannel(f), ARGB.as8BitChannel(f));
    }
}