package com.eeeab.animate.server.event;

import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.animation.Animation;
import net.minecraft.world.entity.Entity;

/**
 * Used to extend {@link net.neoforged.bus.api.Event} and implement {@code ICancellableEvent} so other mods
 * could subscribe on NeoForge's mod event bus and cancel/alter an animation before it plays. Fabric has no
 * generic mod-to-mod event bus at all. The only listener within this mod itself, CombatTriggerEvent#
 * onAnimationStart, is now called directly by AnimationHandler#updateAnimations instead of via post(); nothing
 * ever cancelled the event, so the cancellation machinery itself is dropped.
 */
public class AnimationEvent<T extends Entity & AnimatedEntity> {
    protected Animation animation;
    private final T entity;

    AnimationEvent(T entity, Animation animation) {
        this.entity = entity;
        this.animation = animation;
    }

    public T getEntity() {
        return this.entity;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public static class Start<T extends Entity & AnimatedEntity> extends AnimationEvent<T> {
        public Start(T entity, Animation animation) {
            super(entity, animation);
        }

        public void setAnimation(Animation animation) {
            this.animation = animation;
        }
    }

    public static class Tick<T extends Entity & AnimatedEntity> extends AnimationEvent<T> {
        protected int tick;

        public Tick(T entity, Animation animation, int tick) {
            super(entity, animation);
            this.tick = tick;
        }

        public int getTick() {
            return this.tick;
        }
    }
}
