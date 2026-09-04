package com.eeeab.animate.server.message;

import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.animation.Animation;
import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 停止动画数据包
 * @param entityID                 实体 ID
 * @param onlyStopOverlapAnimation 是否只停止叠加动画
 */
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

    public static void handle(final MessageStopAnimation message, final CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level != null) {
                Entity entity = Minecraft.getInstance().level.getEntity(message.entityID);
                if (entity instanceof AnimatedEntity animationEntity && animationEntity.getAnimations() != null) {
                    Stream<Animation> stream = Arrays.stream(animationEntity.getAnimations());
                    if (!message.onlyStopOverlapAnimation) {
                        animationEntity.getAnimationState(AnimatedEntity.NO_ANIMATION).stop();
                    }
                    stream.filter(Animation::isOverlap).forEach(a -> animationEntity.getAnimationState(a).stop());
                }
            }
        });
    }
}
