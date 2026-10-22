package com.eeeab.eeeabsmobs.client.render.entity;

import com.eeeab.eeeabsmobs.client.render.layer.LayerOuter;
import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.model.entity.ModelTester;
import com.eeeab.eeeabsmobs.client.model.util.ModModelLayer;
import com.eeeab.eeeabsmobs.server.entity.mob.EntityTester;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.eeeab.animate.client.render.EMMobRenderer;
import com.eeeab.animate.client.render.EMRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
public class RenderTester extends EMMobRenderer<EntityTester, ModelTester> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/tester.png");
    private static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/entity/tester_plains.png");

    public RenderTester(EntityRendererProvider.Context context) {
        super(context, new ModelTester(context.bakeLayer(ModModelLayer.TESTER)), 0.3F);
        this.addLayer(new LayerOuter<>(this, LAYER));
    }

    @Override
    public ResourceLocation getTextureLocation(EMRenderState entity) {
        return TEXTURE;
    }

    // 1.21.11: renderNameTag was replaced by submitNameTag(state, ...); the visibility check and the
    // displayed text are now supplied via shouldShowName/getNameTag, so the old custom body (manual
    // camera-orientation billboard, height offset, background opacity) is covered by the vanilla
    // submitNameTag pipeline.
    @Override
    protected boolean shouldShowName(EntityTester entity, double distance) {
        return distance < 4096.0D && super.shouldShowName(entity, distance);
    }

    @Override
    public Component getNameTag(EntityTester entity) {
        return entity.getDamage();
    }
}
