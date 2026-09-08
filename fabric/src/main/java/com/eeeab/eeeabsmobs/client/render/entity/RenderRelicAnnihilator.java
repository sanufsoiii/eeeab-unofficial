package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.animate.client.util.ModelPartUtils;
import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.animation.Animation;
import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelRelicAnnihilator;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.ModRenderType;
import com.eeeab.eeeabsmobs.client.render.layer.LayerGlow;
import com.eeeab.eeeabsmobs.client.render.util.EntityAfterImageHelper;
import com.eeeab.eeeabsmobs.server.entity.mob.relicron.EntityRelicAnnihilator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class RenderRelicAnnihilator extends EMMobRenderer<EntityRelicAnnihilator, ModelRelicAnnihilator> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/relic_annihilator.png");
    private static final Identifier EYE_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/relic_annihilator_eye.png");
    private static final Identifier GLOW_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/relic_annihilator_glow.png");
    private static final Identifier FLAME_LAYER = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/relic_annihilator_exhaust.png");
    private static final String[] MUZZLE = new String[]{"upper", "leftArm", "leftHand", "muzzle"};
    private final EntityAfterImageHelper<EntityRelicAnnihilator> afterImageHelper;

    public RenderRelicAnnihilator(EntityRendererProvider.Context context) {
        super(context, new ModelRelicAnnihilator(context.bakeLayer(ModModelLayer.RELIC_ANNIHILATOR)), 1.3F);
        this.addLayer(new LayerGlow<EntityRelicAnnihilator, ModelRelicAnnihilator>(this, EYE_LAYER, entity -> entity.isGlow() && !entity.isBlinded(), (entity, partialTicks) -> entity.glowControlled.getAnimationFraction(partialTicks)));
        this.addLayer(new LayerGlow<EntityRelicAnnihilator, ModelRelicAnnihilator>(this, GLOW_LAYER, entity -> !entity.glowControlled.isStop(),(annihilator, partialTicks) -> annihilator.glowControlled.getAnimationFraction(partialTicks)));
        // 1.21.11: LayerGlow no longer exposes the renderLayer(...) alpha hook, so the old fixed
        // 0.4F exhaust alpha cannot be preserved here (visual debt: exhaust layer renders at full alpha).
        this.addLayer(new LayerGlow<EntityRelicAnnihilator, ModelRelicAnnihilator>(this, FLAME_LAYER) {
            @Override
            protected RenderType getRenderType(EntityRelicAnnihilator entity) {
                return ModRenderType.getGlowingTranslucentEffect(location);
            }
        });
        afterImageHelper = new EntityAfterImageHelper<>(model.root().getAllParts(), model.root(), 5, 1, 4);
    }

    @Override
    protected float getFlipDegrees() {
        return 0;//获取死亡翻转角度
    }

    @Override
    public Identifier getTextureLocation(EMRenderState corpse) {
        return TEXTURE;
    }

    @Override
    public void submit(EMRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        super.submit(state, poseStack, collector, cameraRenderState);
        EntityRelicAnnihilator entity = (EntityRelicAnnihilator) state.entity;
        if (entity == null) return;
        Animation animation = entity.getAnimation();
        if (animation != AnimatedEntity.NO_ANIMATION) {
            ModelPart root = this.model.root();
            entity.muzzle = ModelPartUtils.getWorldPosition(entity, entity.getYRot(), root, MUZZLE);
            boolean afterImage = animation == EntityRelicAnnihilator.BACKDASH_ANIMATION;
            if (animation == EntityRelicAnnihilator.STAB_ANIMATION) {
                int tick = entity.getAnimationTick();
                afterImage = tick >= 24 && tick < 29;
            }
            if (afterImage) afterImageHelper.recordIfNeeded(entity, entity.level().getGameTime());
        }
        // 1.21.11: renderAfterImages(...) still draws into a MultiBufferSource, which the new
        // submit(...)/SubmitNodeCollector pipeline does not provide, so the after-image pass is
        // temporarily disabled (visual debt: BACKDASH/STAB after-images no longer render).
    }
}
