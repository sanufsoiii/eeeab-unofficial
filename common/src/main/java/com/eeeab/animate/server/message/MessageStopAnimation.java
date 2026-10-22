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

import java.util.Arrays;
import java.util.stream.Stream;

public record MessageStopAnimation(int entityID, boolean onlyStopOverlapAnimation) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageStopAnimation> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "stop_animation"));

    public static final StreamCodec<net.minecraft.network.FriendlyByteBuf, MessageStopAnimation> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MessageStopAnimation::entityID,
            ByteBufCodecs.BOOL, MessageStopAnimation::onlyStopOverlapAnimation,
            MessageStopAnimation::new
    );

    @Override
    public CustomPacketPayload.Type<MessageStopAnimation> type() {
        return TYPE;
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
