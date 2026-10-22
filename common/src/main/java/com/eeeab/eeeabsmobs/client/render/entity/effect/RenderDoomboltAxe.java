package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.effect.ModelDoomboltAxe;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.state.DoomboltAxeRenderState;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityThrownDoomboltAxe;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderDoomboltAxe extends EntityRenderer<EntityThrownDoomboltAxe, DoomboltAxeRenderState> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/item/doombolt_battleaxe.png");
    private final ModelDoomboltAxe model;

    public RenderDoomboltAxe(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new ModelDoomboltAxe(pContext.bakeLayer(ModModelLayer.DOOMBOLT_AXE));
    }

    @Override
    public void render(DoomboltAxeRenderState state, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot + 90.0F));
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBuffer(bufferIn, this.model.renderType(TEXTURE), false, state.isFoil);
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
        super.render(state, poseStack, bufferIn, packedLightIn);
    }

    @Override
    public DoomboltAxeRenderState createRenderState() {
        return new DoomboltAxeRenderState();
    }

    @Override
    public void extractRenderState(EntityThrownDoomboltAxe entity, DoomboltAxeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        state.isFoil = entity.isFoil();
    }
}
