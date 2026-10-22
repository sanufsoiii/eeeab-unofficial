package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.animate.client.util.ModelPartUtils;
import com.eeeab.animate.server.animation.Animation;
import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelImmortal;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.client.render.ModRenderType;
import com.eeeab.eeeabsmobs.client.render.layer.LayerGlow;
import com.eeeab.eeeabsmobs.client.render.layer.LayerOuter;
import com.eeeab.eeeabsmobs.server.entity.mob.immortal.EntityImmortalBoss;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
public class RenderImmortal extends EMMobRenderer<EntityImmortalBoss, ModelImmortal> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal.png");
    private static final ResourceLocation GLOW_LAYER = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_eyes.png");
    private static final ResourceLocation CORE_LAYER = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_core.png");
    private static final ResourceLocation NETHERWORLD_KATANA = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_netherworld_katana.png");
    private static final String[] RIGHT_FIST = new String[]{"upper", "body", "rightArm", "rightArmUnder", "rightFist"};
    private static final String[] LEFT_FIST = new String[]{"upper", "body", "leftArm", "leftArmUnder", "leftFist"};

    public RenderImmortal(EntityRendererProvider.Context context) {
        super(context, new ModelImmortal(context.bakeLayer(ModModelLayer.IMMORTAL)), 2F);
        this.addLayer(new LayerGlow<EntityImmortalBoss, ModelImmortal>(this, GLOW_LAYER, e -> e.isGlow() && !e.isInvisible(),
                (entity, partialTicks) -> entity.glowControlled.getAnimationFraction(partialTicks)) {
            @Override
            protected RenderType getRenderType(EntityImmortalBoss entity) {
                return RenderType.entityTranslucentEmissive(this.location);
            }
        });
        this.addLayer(new LayerOuter<EntityImmortalBoss, ModelImmortal>(this, CORE_LAYER, true, e -> !e.isInvisible()) {
            @Override
            protected float getAlpha(EntityImmortalBoss entity, float partialTicks) {
                return 1F - entity.alphaControlled.getAnimationFraction(partialTicks);
            }
        });
        LayerOuter.OuterPredicate<EntityImmortalBoss> predicate1 = e -> e.isHoldKatana() && !e.isInvisible();
        this.addLayer(new LayerOuter<EntityImmortalBoss, ModelImmortal>(this, NETHERWORLD_KATANA, true, predicate1) {
            @Override
            protected float getAlpha(EntityImmortalBoss entity, float partialTicks) {
                return 1F - entity.alphaControlled.getAnimationFraction(partialTicks);
            }
        });
    }

    @Override
    protected void scale(EMRenderState entity, PoseStack matrixStack) {
        matrixStack.scale(0.8525F, 0.8525F, 0.8525F);
    }

    @Override
    protected float getFlipDegrees() {
        return 0;//获取死亡翻转角度
    }

    // Alpha fade + hurt/spawn render-type swaps were done in the old render() override; on the 1.21.11
    // render-state pipeline they map cleanly onto the getRenderType / getModelTint hooks the base submit() calls.
    @Override
    protected RenderType getRenderType(EMRenderState state, boolean bodyVisible, boolean translucent, boolean glowing) {
        EntityImmortalBoss entity = (EntityImmortalBoss) state.entity;
        if (entity != null) {
            if (!entity.alphaControlled.isStop()) {
                return ModRenderType.getGlowingCutOutEffect(getTextureLocation(state), true);
            } else if (!entity.hurtControlled.isStop()) {
                return ModRenderType.entityTranslucent(getTextureLocation(state));
            }
        }
        return super.getRenderType(state, bodyVisible, translucent, glowing);
    }

    @Override
    protected int getModelTint(EMRenderState state) {
        EntityImmortalBoss entity = (EntityImmortalBoss) state.entity;
        float alpha = 1F;
        if (entity != null) {
            float partialTicks = state.partialTick;
            if (!entity.alphaControlled.isStop()) {
                alpha = 1F - entity.alphaControlled.getAnimationFraction(partialTicks);
            } else if (!entity.hurtControlled.isStop()) {
                alpha = 1F - entity.hurtControlled.getAnimationFraction(partialTicks) * 0.85F;
            }
        }
        return ARGB.color(ARGB.as8BitChannel(alpha), 255, 255, 255);
    }

    @Override
    public void submit(EMRenderState state, PoseStack matrixStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        EntityImmortalBoss entity = (EntityImmortalBoss) state.entity;
        if (entity != null && !entity.alphaControlled.isStop()) {
            float alpha = 1F - entity.alphaControlled.getAnimationFraction(state.partialTick);
            this.shadowRadius = 2F * alpha;
            this.shadowStrength = alpha;
        } else {
            this.shadowStrength = 1F;
            this.shadowRadius = 2F;
        }
        super.submit(state, matrixStack, collector, cameraRenderState);
        if (entity != null) {
            Animation animation = entity.getAnimation();
            if (animation == EntityImmortalBoss.SHORYUKEN_ANIMATION || animation == EntityImmortalBoss.ATTRACT_ANIMATION || animation == EntityImmortalBoss.PUNCH_RIGHT_ANIMATION
                    || animation == EntityImmortalBoss.SMASH_GROUND_ANIMATION1 || animation == EntityImmortalBoss.SMASH_GROUND_ANIMATION2 || animation == EntityImmortalBoss.SMASH_GROUND_ANIMATION3
                    || animation == EntityImmortalBoss.PUNCH_LEFT_ANIMATION || animation == EntityImmortalBoss.POUNCE_SMASH_ANIMATION || animation == EntityImmortalBoss.POUNCE_PICK_ANIMATION
                    || animation == EntityImmortalBoss.HARDPUNCH_LEFT_ANIMATION || animation == EntityImmortalBoss.HARDPUNCH_RIGHT_ANIMATION) {
                entity.hand[0] = ModelPartUtils.getWorldPosition(entity, entity.yBodyRot, this.model.root(), LEFT_FIST);
                entity.hand[1] = ModelPartUtils.getWorldPosition(entity, entity.yBodyRot, this.model.root(), RIGHT_FIST);
            }
        }
    }

    @Override
    public Vec3 getRenderOffset(EMRenderState state) {
        EntityImmortalBoss entity = (EntityImmortalBoss) state.entity;
        float partialTicks = state.partialTick;
        RandomSource random = entity.getRandom();
        if (entity.getAnimation() == EntityImmortalBoss.TELEPORT_ANIMATION && entity.getAnimationTick() > 10) {
            double d0 = 0.05D;
            return new Vec3(random.nextGaussian() * d0, 0.0D, random.nextGaussian() * d0);
        } else if (!entity.hurtControlled.isStop()) {
            double d0 = entity.hurtControlled.getAnimationFraction(partialTicks) * 0.1D;
            return new Vec3(random.nextGaussian() * d0, 0.0D, random.nextGaussian() * d0);
        }
        return super.getRenderOffset(state);
    }

    @Override
    public ResourceLocation getTextureLocation(EMRenderState immortal) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(EntityImmortalBoss entity, BlockPos pos) {
        return (int) Math.max(15 * entity.glowControlled.getAnimationFraction(), super.getBlockLightLevel(entity, pos));
    }
}
