package com.eeeab.animate.server.message;

import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.handler.AnimationHandler;
import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record MessagePlayAnimation(int entityID, int animationsIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessagePlayAnimation> TYPE =
            new CustomPacketPayload.Type<>(new ResourceLocation(EEEABMobs.MOD_ID, "play_animation"));

    public static final StreamCodec<net.minecraft.network.FriendlyByteBuf, MessagePlayAnimation> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MessagePlayAnimation::entityID,
            ByteBufCodecs.VAR_INT, MessagePlayAnimation::animationsIndex,
            MessagePlayAnimation::new
    );

    @Override
    public CustomPacketPayload.Type<MessagePlayAnimation> type() {
        return TYPE;
    }

    public static <T extends Entity & AnimatedEntity> void handleServer(MessagePlayAnimation message, ServerPlayer sender) {
        Level level = sender.level();
        @SuppressWarnings("unchecked")
        T entity = (T) level.getEntity(message.entityID);
        if (entity != null && message.animationsIndex != -1) {
            AnimationHandler.INSTANCE.sendAnimationMessage(entity, entity.getAnimations()[message.animationsIndex]);
        }
    }
}
