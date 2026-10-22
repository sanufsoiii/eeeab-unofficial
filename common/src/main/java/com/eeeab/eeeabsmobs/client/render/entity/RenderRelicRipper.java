package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.animate.client.util.ModelPartUtils;
import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelRelicRipper;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.layer.LayerGlow;
import com.eeeab.eeeabsmobs.server.entity.mob.relicron.EntityRelicRipper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.resources.ResourceLocation;

public class RenderRelicRipper extends EMMobRenderer<EntityRelicRipper, ModelRelicRipper> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/relic_ripper.png");
    private static final ResourceLocation GLOW_LAYER = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/relic_ripper_glow.png");
    private static final String[] SAW = new String[]{"upper", "rightArm", "rightArmJoint", "rightHand", "sawJoint", "saw"};

    public RenderRelicRipper(EntityRendererProvider.Context context) {
        super(context, new ModelRelicRipper(context.bakeLayer(ModModelLayer.RELIC_RIPPER)), 1.4F);
        this.addLayer(new LayerGlow<EntityRelicRipper, ModelRelicRipper>(this, GLOW_LAYER, e -> !e.glowControlled.isStop(),
                (entity, partialTicks) -> entity.glowControlled.getAnimationFraction(partialTicks)));
    }

    @Override
    protected void scale(EMRenderState entity, PoseStack matrixStack) {
        matrixStack.scale(0.95F, 0.95F, 0.95F);
    }

    @Override
    protected float getFlipDegrees() {
        return 0;//获取死亡翻转角度
    }

    @Override
    public ResourceLocation getTextureLocation(EMRenderState corpse) {
        return TEXTURE;
    }

    @Override
    public void render(EMRenderState state, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        super.render(state, matrixStack, buffer, packedLight);
        EntityRelicRipper entity = (EntityRelicRipper) state.entity;
        if (entity != null && !entity.isNoAnimation()) {
            entity.saw = ModelPartUtils.getWorldPosition(entity, entity.yBodyRot, this.model.root(), SAW);
        }
    }
}
