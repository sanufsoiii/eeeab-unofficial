package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.effect.ModelAnnihilatorMissile;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.state.AnnihilatorMissileRenderState;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityAnnihilatorMissile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderAnnihilatorMissile extends EntityRenderer<EntityAnnihilatorMissile, AnnihilatorMissileRenderState> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/annihilator_missile.png");
    private final ModelAnnihilatorMissile model;

    public RenderAnnihilatorMissile(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ModelAnnihilatorMissile(context.bakeLayer(ModModelLayer.ANNIHILATOR_MISSILE));
    }

    @Override
    protected int getBlockLightLevel(EntityAnnihilatorMissile entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(AnnihilatorMissileRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        this.model.root.yRot = state.yRot * ((float) Math.PI / 180F);
        this.model.root.xRot = state.xRot * ((float) Math.PI / 180F);
        this.model.missile.zRot = state.zRot * ((float) Math.PI / 180F);
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
        super.render(state, poseStack, buffer, packedLight);
    }

    @Override
    public AnnihilatorMissileRenderState createRenderState() {
        return new AnnihilatorMissileRenderState();
    }

    @Override
    public void extractRenderState(EntityAnnihilatorMissile entity, AnnihilatorMissileRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        state.zRot = (entity.tickCount + partialTick) * 20F;
    }
}