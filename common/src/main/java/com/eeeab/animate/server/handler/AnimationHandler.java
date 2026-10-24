package com.eeeab.animate.server.handler;

import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.animation.Animation;
import com.eeeab.animate.server.event.AnimationEvent;
import com.eeeab.animate.server.message.MessageAnimation;
import com.eeeab.animate.server.message.MessageStopAnimation;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ArrayUtils;

public enum AnimationHandler {
    INSTANCE;

    private static <T> void sendToTrackingAndSelf(Entity entity, T message) {
        com.eeeab.eeeabsmobs.server.message.ModNetwork.sendToTracking(entity, message, true);
    }

    public <T extends Entity & AnimatedEntity> void sendAnimationMessage(T entity, Animation animation) {
        if (!entity.level().isClientSide) {
            entity.setAnimation(animation);
            entity.setAnimationTick(0);
            sendToTrackingAndSelf(entity, new MessageAnimation(entity.getId(), ArrayUtils.indexOf(entity.getAnimations(), animation)));
        }
    }

    public <T extends Entity & AnimatedEntity> void sendAnimationMessage(T entity, boolean onlyStopOverlapAnimation) {
        if (!entity.level().isClientSide) {
            sendToTrackingAndSelf(entity, new MessageStopAnimation(entity.getId(), onlyStopOverlapAnimation));
        }
    }

    public <T extends Entity & AnimatedEntity> void updateAnimations(T entity) {
        Animation animation = entity.getAnimation();
        if (animation == null) {
            entity.setAnimation(AnimatedEntity.NO_ANIMATION);
        } else {
            if (animation != AnimatedEntity.NO_ANIMATION) {
                int tick = entity.getAnimationTick();
                if (tick == 0) {
                    // Was gated by NeoForge.EVENT_BUS.post(new AnimationEvent.Start<>(...)).isCanceled(); no
                    // Fabric event bus exists, so playback always proceeds now (matching the always-uncancelled
                    // case). CombatTriggerEvent was the one actual listener within this mod (used to fire combat
                    // hint popups off certain animations starting) - called directly now instead of via post().
                    if (entity instanceof com.eeeab.eeeabsmobs.server.entity.EEEABMobLibrary mobLibraryEntity) {
                        com.eeeab.eeeabsmobs.server.entity.trigger.CombatTriggerEvent.INSTANCE.onAnimationStart(
                                new AnimationEvent.Start<>(mobLibraryEntity, animation));
                    }
                    sendAnimationMessage(entity, animation);
                }
                if (tick < animation.getDuration()) {
                    entity.setAnimationTick(tick + 1);
                    // Was NeoForge.EVENT_BUS.post(new AnimationEvent.Tick<>(...)); pure notification event, no
                    // Fabric equivalent and no internal listener, so it is simply dropped.
                    if (entity.getKeyframeManager() != null) {
                        entity.getKeyframeManager().tick(entity);
                    }
                }
                if (entity.getAnimationTick() >= animation.getDuration()) {
                    if (!animation.isLooping()) {
                        entity.setAnimation(AnimatedEntity.NO_ANIMATION);
                        entity.getAnimationState(animation).stop();
                    }
                    entity.setAnimationTick(0);
                }
            }
        }
    }
}