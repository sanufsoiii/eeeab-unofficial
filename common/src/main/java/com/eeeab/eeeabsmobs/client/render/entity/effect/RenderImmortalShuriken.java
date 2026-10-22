package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.effect.ModelImmortalShuriken;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.state.ImmortalShurikenRenderState;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityImmortalShuriken;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import java.util.List;
public class RenderImmortalShuriken extends EntityRenderer<EntityImmortalShuriken, ImmortalShurikenRenderState> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/immortal_shuriken.png");
    private final ModelImmortalShuriken model;

    public RenderImmortalShuriken(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ModelImmortalShuriken(context.bakeLayer(ModModelLayer.IMMORTAL_SHURIKEN));
    }

    @Override
    public void submit(ImmortalShurikenRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0F, 0.93875F, 0F);
        poseStack.scale(-0.5F, -0.5F, 0.5F);
        this.model.root.yRot = state.yRot * ((float) Math.PI / 180F);
        this.model.root.xRot = state.xRot * ((float) Math.PI / 180F);
        this.model.bladeGroup.yRot += state.bladeSpin;
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
    public ImmortalShurikenRenderState createRenderState() {
        return new ImmortalShurikenRenderState();
    }

    @Override
    public void extractRenderState(EntityImmortalShuriken entity, ImmortalShurikenRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        float speed = (float) Math.min(entity.getDeltaMovement().length() / 0.55F * 0.1F, 0.15F);
        state.bladeSpin = speed > 0.001F ? speed : 0.0F;
    }
}