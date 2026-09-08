package com.eeeab.eeeabsmobs.client.render.entity.effect;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityInfraredRay;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class RenderInfraredRay extends RenderAbsBeam<EntityInfraredRay> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/infrared_ray.png");
    private static final ResourceLocation FLASH_TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/effect/infrared_ray_flash.png");

    public RenderInfraredRay(EntityRendererProvider.Context mgr) {
        super(mgr, 0.15F, 0.15F);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityInfraredRay entity) {
        return entity.isBlinking() ? FLASH_TEXTURE : TEXTURE;
    }

    @Override
    protected void renderEnd(EntityInfraredRay entity, int frame, Direction side, PoseStack matrixStackIn, VertexConsumer builder, float delta, int packedLightIn) {
        side = null;
        super.renderEnd(entity, frame, side, matrixStackIn, builder, delta, packedLightIn);
    }

    @Override
    protected void drawVertex(Matrix4f matrix, PoseStack.Pose pose, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, int packedLightIn) {
        vertexBuilder.addVertex(matrix, offsetX, offsetY, offsetZ).setColor(1F, 1F, 1F, 0.75F).setUv(textureX, textureY).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLightIn).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
