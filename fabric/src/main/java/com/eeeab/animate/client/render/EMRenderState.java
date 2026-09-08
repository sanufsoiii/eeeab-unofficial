package com.eeeab.animate.client.render;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Bridge render state for EEEAB's custom animation framework.
 *
 * <p>1.21.2+ split entity data extraction (server/main thread) from rendering (render thread): renderers now
 * build an {@link net.minecraft.client.renderer.entity.state.EntityRenderState} snapshot each frame and models
 * animate purely from that snapshot. EEEAB's animation system ({@link com.eeeab.animate.client.model.ModHierarchicalModel}
 * and its subclasses) reads a great deal directly off the live entity (getAnimation(), getAnimationState(),
 * custom tick/emotion fields, ...). Rewriting every model to copy all of that into a flat state would be a
 * large, error-prone change, so this state instead carries the live entity reference plus the handful of
 * pose values the models expect as method parameters. Extraction always runs on the render thread with the
 * entity present, so reading it back in setupAnim is safe for this client-only mod.
 */
@Environment(EnvType.CLIENT)
public class EMRenderState extends LivingEntityRenderState {
    public Entity entity;
    public float partialTick;
    public float limbSwing;
    public float limbSwingAmount;
    public float netHeadYaw;
    public float headPitch;
}
