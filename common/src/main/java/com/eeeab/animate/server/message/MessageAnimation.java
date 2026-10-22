package com.eeeab.animate.server.message;

import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.animation.Animation;
import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record MessageAnimation(int entityID, int animationsIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageAnimation> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "animation"));

    public static final StreamCodec<net.minecraft.network.FriendlyByteBuf, MessageAnimation> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MessageAnimation::entityID,
            ByteBufCodecs.VAR_INT, MessageAnimation::animationsIndex,
            MessageAnimation::new
    );

    @Override
    public CustomPacketPayload.Type<MessageAnimation> type() {
        return TYPE;
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
