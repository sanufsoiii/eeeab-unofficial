package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.effect.ModelPulsedGrenade;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.state.PulsedGrenadeRenderState;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityPulsedGrenade;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import java.util.List;
public class RenderPulsedGrenade extends EntityRenderer<EntityPulsedGrenade, PulsedGrenadeRenderState> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/pulsed_grenade.png");
    private final ModelPulsedGrenade model;

    public RenderPulsedGrenade(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ModelPulsedGrenade(context.bakeLayer(ModModelLayer.PULSED_GRENADE));
    }

    @Override
    public void submit(PulsedGrenadeRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(0F, 0.5F, 0F);
        this.model.ball.yRot = state.yRot * ((float) Math.PI / 180F);
        this.model.ball.xRot = state.xRot * ((float) Math.PI / 180F);
        List<RenderType> list = ItemRenderer.getFoilRenderTypes(this.model.renderType(TEXTURE), false, false);
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
                state.outlineColor,
                null
            );
        }
        poseStack.popPose();
        super.submit(state, poseStack, collector, cameraRenderState);
    }

    @Override
    public PulsedGrenadeRenderState createRenderState() {
        return new PulsedGrenadeRenderState();
    }

    @Override
    public void extractRenderState(EntityPulsedGrenade entity, PulsedGrenadeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = entity.getViewYRot(partialTick);
        state.xRot = (entity.tickCount + partialTick) * Math.min(30, entity.tickCount * 2);
    }
}