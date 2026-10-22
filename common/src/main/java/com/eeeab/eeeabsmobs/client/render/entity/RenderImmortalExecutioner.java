package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.animate.client.util.ModelPartUtils;
import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelImmortalExecutioner;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.server.entity.mob.immortal.EntityImmortalExecutioner;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
public class RenderImmortalExecutioner extends EMMobRenderer<EntityImmortalExecutioner, ModelImmortalExecutioner> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/immortal_executioner.png");
    private static final String[] FIRE = new String[]{"upper", "body", "fire"};

    public RenderImmortalExecutioner(EntityRendererProvider.Context context) {
        super(context, new ModelImmortalExecutioner(context.bakeLayer(ModModelLayer.IMMORTAL_EXECUTIONER)), 0.75F);
    }

    @Override
    protected float getFlipDegrees() {
        return 0;//获取死亡翻转角度
    }

    @Override
    protected void scale(EMRenderState entity, PoseStack poseStack) {
        poseStack.scale(1F, 1F, 1F);
    }

    @Override
    public Identifier getTextureLocation(EMRenderState entity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(EntityImmortalExecutioner entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void submit(EMRenderState state, PoseStack matrixStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        super.submit(state, matrixStack, collector, cameraRenderState);
        EntityImmortalExecutioner entity = (EntityImmortalExecutioner) state.entity;
        if (entity.fire != null && entity.fire.length > 0) {
            entity.fire[0] = ModelPartUtils.getWorldPosition(entity, entity.yBodyRot, this.model.root(), FIRE);
        }
    }
}
