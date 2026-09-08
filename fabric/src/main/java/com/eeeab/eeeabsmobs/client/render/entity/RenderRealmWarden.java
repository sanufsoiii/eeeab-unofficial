package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.animate.client.util.ModelPartUtils;
import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.animation.Animation;
import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelRealmWarden;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.ModRenderType;
import com.eeeab.eeeabsmobs.client.render.layer.LayerGlow;
import com.eeeab.eeeabsmobs.server.entity.mob.relicron.EntityRealmWarden;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RenderRealmWarden extends EMMobRenderer<EntityRealmWarden, ModelRealmWarden> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/realm_warden.png");
    private static final Identifier GLOW_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/realm_warden_glow.png");
    private static final Identifier FLAME_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/realm_warden_exhaust.png");
    private static final String[] LEFT_GLOVE = new String[]{"upper", "leftArm", "leftArmUnder", "leftGlove", "lGloveJoint"};
    private static final String[] RIGHT_GLOVE = new String[]{"upper", "rightArm", "rightArmUnder", "rightGlove", "rGloveJoint"};
    private static final String[] LEFT_BLADE = new String[]{"upper", "mainBooster", "axe_back", "leftSocket", "leftBlade"};
    private static final String[] RIGHT_BLADE = new String[]{"upper", "mainBooster", "axe_back", "rightSocket", "rightBlade"};

    public RenderRealmWarden(EntityRendererProvider.Context context) {
        super(context, new ModelRealmWarden(context.bakeLayer(ModModelLayer.REALM_WARDEN)), 2F);
        this.addLayer(new LayerGlow<EntityRealmWarden, ModelRealmWarden>(this, GLOW_LAYER, e -> !e.glowControlled.isStop(),
                (entity, partialTicks) -> entity.glowControlled.getAnimationFraction(partialTicks)));
        // The old renderLayer override forced alpha 0.4; on the submit pipeline the layer's colour comes from
        // its brightness supplier, so a fixed 0.4 brightness reproduces that.
        this.addLayer(new LayerGlow<EntityRealmWarden, ModelRealmWarden>(this, FLAME_LAYER, 0.4F) {
            @Override
            protected RenderType getRenderType(EntityRealmWarden entity) {
                return ModRenderType.getGlowingTranslucentEffect(location);
            }
        });
    }

    @Override
    protected void scale(EMRenderState entity, PoseStack matrixStack) {
        matrixStack.scale(1.1F, 1.1F, 1.1F);
    }

    @Override
    protected float getFlipDegrees() {
        return 0;//获取死亡翻转角度
    }

    @Override
    public Identifier getTextureLocation(EMRenderState sentinelHeavy) {
        return TEXTURE;
    }

    // Alpha fade (translucent-cull) done via the getRenderType / getModelTint hooks the base submit() calls.
    @Override
    protected RenderType getRenderType(EMRenderState state, boolean bodyVisible, boolean translucent, boolean glowing) {
        EntityRealmWarden entity = (EntityRealmWarden) state.entity;
        if (entity != null && !entity.alphaControlled.isStop()) {
            return ModRenderType.entityTranslucentCull(TEXTURE);
        }
        return super.getRenderType(state, bodyVisible, translucent, glowing);
    }

    @Override
    protected int getModelTint(EMRenderState state) {
        EntityRealmWarden entity = (EntityRealmWarden) state.entity;
        if (entity != null && !entity.alphaControlled.isStop()) {
            float alpha = 1F - entity.alphaControlled.getAnimationProgressSinSqrt(state.partialTick);
            return ARGB.color(ARGB.as8BitChannel(alpha), 255, 255, 255);
        }
        return super.getModelTint(state);
    }

    @Override
    public void submit(EMRenderState state, PoseStack matrixStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        super.submit(state, matrixStack, collector, cameraRenderState);
        EntityRealmWarden entity = (EntityRealmWarden) state.entity;
        if (entity == null) return;
        Animation animation = entity.getAnimation();
        if (animation != AnimatedEntity.NO_ANIMATION) {
            ModelPart root = this.model.root();
            String[] lParts = LEFT_GLOVE;
            String[] rParts = RIGHT_GLOVE;
            if (animation == EntityRealmWarden.BACKSTEP_LANDING_ANIMATION) {
                rParts = RIGHT_BLADE;
                lParts = LEFT_BLADE;
            }
            entity.modelParts[0] = ModelPartUtils.getWorldPosition(entity, entity.getYRot(), root, lParts);
            entity.modelParts[1] = ModelPartUtils.getWorldPosition(entity, entity.getYRot(), root, rParts);
        }
    }
}
