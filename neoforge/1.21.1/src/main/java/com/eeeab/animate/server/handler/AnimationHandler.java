package com.eeeab.animate.server.handler;

import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.animation.Animation;
import com.eeeab.animate.server.event.AnimationEvent;
import com.eeeab.animate.server.message.MessageAnimation;
import com.eeeab.animate.server.message.MessageStopAnimation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.ArrayUtils;

public enum AnimationHandler {
    INSTANCE;

    // NeoForge 1.21's PacketDistributor exposes plain static send methods keyed by CustomPacketPayload -
    // there is no more SimpleChannel to route through (see PacketDistributor#sendToPlayersTrackingEntityAndSelf).
    public <T extends Entity & AnimatedEntity> void sendAnimationMessage(T entity, Animation animation) {
        if (!entity.level().isClientSide) {
            entity.setAnimation(animation);
            entity.setAnimationTick(0);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new MessageAnimation(entity.getId(), ArrayUtils.indexOf(entity.getAnimations(), animation)));
        }
    }

    public <T extends Entity & AnimatedEntity> void sendAnimationMessage(T entity, boolean onlyStopOverlapAnimation) {
        if (!entity.level().isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new MessageStopAnimation(entity.getId(), onlyStopOverlapAnimation));
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
                    //可以通过事件影响播放动画
                    AnimationEvent.Start<T> event = new AnimationEvent.Start<>(entity, animation);
                    if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
                        entity.getAnimationState(animation).stop();
                        entity.setAnimation(AnimatedEntity.NO_ANIMATION);
                        return;
                    } else {
                        sendAnimationMessage(entity, animation);
                    }
                }
                if (tick < animation.getDuration()) {
                    entity.setAnimationTick(tick + 1);
                    NeoForge.EVENT_BUS.post(new AnimationEvent.Tick<>(entity, animation, entity.getAnimationTick()));
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