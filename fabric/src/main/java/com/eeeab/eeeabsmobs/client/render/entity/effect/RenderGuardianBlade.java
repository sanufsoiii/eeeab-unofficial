package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.effect.ModelGuardianBlade;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.state.GuardianBladeRenderState;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityGuardianBlade;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public class RenderGuardianBlade extends EntityRenderer<EntityGuardianBlade, GuardianBladeRenderState> {
    private static final Identifier[] TEXTURES = new Identifier[6];
    private final ModelGuardianBlade model;

    public RenderGuardianBlade(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ModelGuardianBlade(context.bakeLayer(ModModelLayer.GUARDIAN_BLADE));
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/guardian_blade/index_" + (i + 1) + ".png");
        }
    }

    @Override
    public void submit(GuardianBladeRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0F, 1.5F, 0F);
        poseStack.scale(0.65F, -1.0F, -0.65F);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));
        List<RenderType> list = ItemRenderer.getFoilRenderTypes(this.model.renderType(TEXTURES[state.textureIndex]), false, false);
        for (int i = 0; i < list.size(); i++) {
            collector.order(i).submitModel(
                this.model,
                Unit.INSTANCE,
                poseStack,
                list.get(i),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                -1,
                null,
                state.color,
                null
            );
        }
        poseStack.popPose();
        super.submit(state, poseStack, collector, cameraRenderState);
    }

    @Override
    public GuardianBladeRenderState createRenderState() {
        return new GuardianBladeRenderState();
    }

    @Override
    public void extractRenderState(EntityGuardianBlade entity, GuardianBladeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = entity.getYRot();
        float animationProgress = Math.min(entity.controlled.getAnimationFraction(partialTick) + 0.1F, 1F);
        float f;
        if (animationProgress > 0.5) {
            f = 1.0F - (animationProgress - 0.5F) * 2.0F;
        } else {
            f = 1.0F;
        }
        state.color = ARGB.color(ARGB.as8BitChannel(f), ARGB.as8BitChannel(f), ARGB.as8BitChannel(f), ARGB.as8BitChannel(f));
        state.textureIndex = Mth.clamp((int) (entity.tickCount * 0.5 % TEXTURES.length), 0, TEXTURES.length - 1);
    }

    @Override
    protected int getBlockLightLevel(EntityGuardianBlade entity, BlockPos pos) {
        return 15;
    }
}