package com.eeeab.eeeabsmobs.client.render;

import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
/**
 * 1.21.9 removed {@code RenderStateShard}/{@code RenderType.CompositeState} and the immediate-mode shader
 * constants this class used to hand-build its glow render types with. Custom render types are now assembled
 * from {@code com.mojang.blaze3d.pipeline.RenderPipeline} objects via {@code RenderType.create(String, RenderSetup)}.
 *
 * <p>Rather than re-author bespoke pipelines/shaders (which cannot be verified without running the game), the
 * mod's custom glow types are mapped to the closest stock {@link RenderTypes} factories:
 * emissive glow -> {@code entityTranslucentEmissive}. This preserves "glows in the dark / additive-ish, translucent"
 * behaviour; the exact cull/depth/blend nuances of the old hand-built types are an approximation to verify in-game
 * (see NEXT-1.21.11.md). Signatures are kept identical so every call site compiles unchanged.
 */
public final class ModRenderType {
    private ModRenderType() {
    }

    // 1.21.11 deferred rendering: submitCustomGeometry snapshots the PoseStack; the outer PoseStack is stale
    // (already popped) by the time the deferred callback runs. Rebuild a PoseStack seeded from the callback's
    // snapshot Pose so existing push/translate/last() geometry code keeps working with the correct transform.
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

    // Old custom ParticleRenderType overrode ParticleRenderType#begin (Tesselator/TextureManager) which no longer
    // exists (1.21.9 particles render through QuadParticleRenderState). Mapped to the stock translucent single-quad
    // group; the per-particle draw is now handled by SingleQuadParticle#extract in each particle class.
    public static final ParticleRenderType PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH = ParticleRenderType.SINGLE_QUADS;
}
