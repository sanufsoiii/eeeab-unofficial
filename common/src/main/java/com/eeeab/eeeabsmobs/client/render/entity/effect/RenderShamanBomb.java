package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.render.FlatTextureRenderer;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityShamanBomb;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
public class RenderShamanBomb extends FlatTextureRenderer<EntityShamanBomb, RenderShamanBomb.ShamanBombRenderState> {
    private static final Identifier N_TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/shaman_bomb.png");
    private static final Identifier D_TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/shaman_bomb_dangerous.png");
    private static final float SCALE = 2.0F;
    private final RandomSource random = RandomSource.create();

    public RenderShamanBomb(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLightLevel(EntityShamanBomb entity, BlockPos pos) {
        return 15;
    }

    @Override
    public Vec3 getRenderOffset(ShamanBombRenderState state) {
        double d0 = 0.005D;
        return new Vec3(this.random.nextGaussian() * d0, 0.0D, this.random.nextGaussian() * d0);
    }

    @Override
    public ShamanBombRenderState createRenderState() {
        return new ShamanBombRenderState();
    }

    @Override
    public void extractRenderState(EntityShamanBomb entity, ShamanBombRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.fraction = entity.scaleControlled.getAnimationFraction(partialTick);
        state.dangerous = entity.isDangerous();
    }

    @Override
    public void submit(ShamanBombRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        float fraction = state.fraction;
        float scale = SCALE * fraction;
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0F, 0.5F - 0.5F * fraction, 0F);
        poseStack.mulPose(cameraRenderState.orientation);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        RenderType renderType = RenderTypes.entityCutoutNoCull(state.dangerous ? D_TEXTURE : N_TEXTURE);
        collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) -> {
            PoseStack ps = com.eeeab.eeeabsmobs.client.render.ModRenderType.seededStack(pose);
            renderFlatQuad(ps, vertexConsumer, state.lightCoords, 1F, 0F, 0F, 1F, 1F, 1F, Quad.XY);
        });
        poseStack.popPose();
    }

    public static class ShamanBombRenderState extends EntityRenderState {
        public float fraction;
        public boolean dangerous;
    }
}
