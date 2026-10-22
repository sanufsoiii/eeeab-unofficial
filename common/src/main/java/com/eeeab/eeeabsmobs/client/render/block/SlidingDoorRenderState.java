package com.eeeab.eeeabsmobs.client.render.block;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class SlidingDoorRenderState extends BlockEntityRenderState {
    public float ageInTicks;
    public AnimationState openingAnimationState = new AnimationState();
    public AnimationState closingAnimationState = new AnimationState();
}
