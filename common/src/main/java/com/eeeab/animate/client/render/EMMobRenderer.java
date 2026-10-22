package com.eeeab.animate.client.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Mob;
/**
 * Base mob renderer for EEEAB mobs on the 1.21.2+ render-state pipeline. Supplies the {@link EMRenderState}
 * factory and extraction (stamping the live entity + partial tick onto the state) so subclasses stay as small
 * as they were before the split: they only pick a model/texture and add layers.
 */
public abstract class EMMobRenderer<T extends Mob, M extends EntityModel<? super EMRenderState>> extends MobRenderer<T, EMRenderState, M> {
    public EMMobRenderer(EntityRendererProvider.Context context, M model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Override
    public EMRenderState createRenderState() {
        return new EMRenderState();
    }

    @Override
    public void extractRenderState(T entity, EMRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.entity = entity;
        state.partialTick = partialTick;
    }
}
