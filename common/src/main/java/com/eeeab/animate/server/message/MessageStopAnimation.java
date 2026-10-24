package com.eeeab.animate.server.message;

import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.animation.Animation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.stream.Stream;

public record MessageStopAnimation(int entityID, boolean onlyStopOverlapAnimation) {

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityID);
        buffer.writeBoolean(this.onlyStopOverlapAnimation);
    }

    public static MessageStopAnimation decode(FriendlyByteBuf buffer) {
        return new MessageStopAnimation(buffer.readVarInt(), buffer.readBoolean());
    }

    public static void handleClient(MessageStopAnimation message, Level level) {
        if (level != null) {
            Entity entity = level.getEntity(message.entityID);
            if (entity instanceof AnimatedEntity animationEntity && animationEntity.getAnimations() != null) {
                Stream<Animation> stream = Arrays.stream(animationEntity.getAnimations());
                if (!message.onlyStopOverlapAnimation) {
                    animationEntity.getAnimationState(AnimatedEntity.NO_ANIMATION).stop();
                }
                stream.filter(Animation::isOverlap).forEach(a -> animationEntity.getAnimationState(a).stop());
            }
        }
    }
}
