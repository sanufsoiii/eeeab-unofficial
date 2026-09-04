package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.eeeabsmobs.client.render.layer.LayerOuter;
import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelTester;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.server.entity.mob.EntityTester;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class RenderTester extends MobRenderer<EntityTester, ModelTester> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/tester.png");
    private static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/tester_plains.png");

    public RenderTester(EntityRendererProvider.Context context) {
        super(context, new ModelTester(context.bakeLayer(ModModelLayer.TESTER)), 0.3F);
        this.addLayer(new LayerOuter<>(this, LAYER));
    }

    @Override
    public ResourceLocation getTextureLocation(EntityTester entity) {
        return TEXTURE;
    }

    @Override
    protected void renderNameTag(EntityTester pEntity, Component pDisplayName, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, float partialTick) {
        double distance = this.entityRenderDispatcher.distanceToSqr(pEntity);
        // Was ClientHooks.isNameplateInRenderDistance(pEntity, distance) - a NeoForge client convenience wrapper
        // with no Fabric equivalent. 4096.0 (64 blocks squared) is vanilla's own nameplate cutoff distance,
        // used the same way by e.g. LivingEntity's default name-tag visibility check.
        if (distance < 4096.0D) {
            pDisplayName = pEntity.getDamage();
            // EntityTester has no getNameTagOffsetY() method anywhere in this codebase (not found via grep) and
            // vanilla's own renderNameTag now sources this offset from Entity#getAttachments()/
            // EntityAttachment.NAME_TAG rather than a per-renderer method, so there's no equivalent to port from.
            // Reimplemented the evident intent - "just above the entity's head" - directly.
            float f = pEntity.getBbHeight() + 0.5F;
            pMatrixStack.pushPose();
            pMatrixStack.translate(0.0F, f, 0.0F);
            pMatrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            pMatrixStack.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = pMatrixStack.last().pose();
            float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int j = (int) (f1 * 255.0F) << 24;
            Font font = this.getFont();
            float f2 = (float) (-font.width(pDisplayName) / 2);
            font.drawInBatch(pDisplayName, f2, (float) -10, 553648127, false, matrix4f, pBuffer, Font.DisplayMode.SEE_THROUGH, j, pPackedLight);
            font.drawInBatch(pDisplayName, f2, (float) -10, -1, false, matrix4f, pBuffer, Font.DisplayMode.NORMAL, 0, pPackedLight);
            pMatrixStack.popPose();
        }
    }
}
