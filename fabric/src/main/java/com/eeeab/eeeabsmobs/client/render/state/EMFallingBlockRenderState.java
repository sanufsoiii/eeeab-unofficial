package com.eeeab.eeeabsmobs.client.render.state;

import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Quaternionf;

/**
 * Render state for {@link com.eeeab.eeeabsmobs.server.entity.effect.EntityFallingBlock}.
 * Extends the vanilla falling-block state (which carries the MovingBlockRenderState used by
 * {@code SubmitNodeCollector#submitMovingBlock}) with the mod's extra animation fields.
 */
@Environment(EnvType.CLIENT)
public class EMFallingBlockRenderState extends FallingBlockRenderState {
    public float partialTick;
    public float prevAnimY;
    public float animY;
    public boolean overallMove;
    public boolean applyRuptureRotation;
    public Quaternionf ruptureRotation;
}
