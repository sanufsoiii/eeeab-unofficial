package com.eeeab.animate.server.message;

import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.animation.Animation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record MessageAnimation(int entityID, int animationsIndex) {

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityID);
        buffer.writeVarInt(this.animationsIndex);
    }

    public static MessageAnimation decode(FriendlyByteBuf buffer) {
        return new MessageAnimation(buffer.readVarInt(), buffer.readVarInt());
    }

    public static void handleClient(MessageAnimation message, Level level) {
        if (level != null) {
            Entity entity = level.getEntity(message.entityID);
            if (entity instanceof AnimatedEntity animatedEntity) {
                animatedEntity.getAnimationState(AnimatedEntity.NO_ANIMATION).stop();
                if (message.animationsIndex == -1) {
                    animatedEntity.setAnimation(AnimatedEntity.NO_ANIMATION);
                } else {
                    Animation animation = animatedEntity.getAnimations()[message.animationsIndex];
                    animatedEntity.getAnimationState(animation).start(entity.tickCount);
                    animatedEntity.setAnimation(animation);
                }
                animatedEntity.setAnimationTick(0);
            }
        }
    }
}
