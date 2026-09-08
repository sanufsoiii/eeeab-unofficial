package com.eeeab.animate.server.message;

import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.animation.Animation;
import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 停止动画数据包
 * @param entityID                 实体 ID
 * @param onlyStopOverlapAnimation 是否只停止叠加动画
 */
public record MessageStopAnimation(int entityID, boolean onlyStopOverlapAnimation) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageStopAnimation> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "stop_animation"));

    public static final StreamCodec<net.minecraft.network.FriendlyByteBuf, MessageStopAnimation> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MessageStopAnimation::entityID,
            ByteBufCodecs.BOOL, MessageStopAnimation::onlyStopOverlapAnimation,
            MessageStopAnimation::new
    );

    @Override
    public CustomPacketPayload.Type<MessageStopAnimation> type() {
        return TYPE;
    }

    /**
     * Client-only handler, kept in a nested class on purpose. The payload record itself is touched on
     * the server when its type is registered, and Fabric refuses to load a class that references
     * client-only types there ("Cannot load class ... in environment type SERVER"). A nested class is a
     * separate class file, so it is only loaded when the client actually references it.
     */
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public static final class Client {
    public static void handle(final MessageStopAnimation message, final net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context context) {
            context.client().execute(() -> {
                Level level = context.client().level;
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
            });
        }
    }
}
