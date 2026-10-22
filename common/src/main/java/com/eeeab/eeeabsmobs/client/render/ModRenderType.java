package com.eeeab.eeeabsmobs.client.render;

import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class ModRenderType {
    private ModRenderType() {
    }

    public static com.mojang.blaze3d.vertex.PoseStack seededStack(com.mojang.blaze3d.vertex.PoseStack.Pose pose) {
        com.mojang.blaze3d.vertex.PoseStack ps = new com.mojang.blaze3d.vertex.PoseStack();
        ps.last().pose().set(pose.pose());
        ps.last().normal().set(pose.normal());
        return ps;
    }

    public static RenderType getGlowingEffect(ResourceLocation location) {
        return RenderType.entityTranslucentEmissive(location);
    }

    public static RenderType getGlowingTranslucentEffect(ResourceLocation location) {
        return RenderType.entityTranslucentEmissive(location);
    }

    @Deprecated
    public static RenderType getStrongGlowingEffect(ResourceLocation location) {
        return RenderType.entityTranslucentEmissive(location);
    }

    @Deprecated
    public static RenderType getGlowingCutOutEffect(ResourceLocation location) {
        return RenderType.entityTranslucentEmissive(location);
    }

    public static RenderType getGlowingCutOutEffect(ResourceLocation location, boolean backFaceCulling) {
        return RenderType.entityTranslucentEmissive(location);
    }

    public static RenderType entityTranslucent(ResourceLocation location) {
        return RenderType.entityTranslucent(location);
    }

    public static RenderType entityTranslucentCull(ResourceLocation location) {
        return RenderType.entityTranslucent(location);
    }

    public static final ParticleRenderType PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
}
