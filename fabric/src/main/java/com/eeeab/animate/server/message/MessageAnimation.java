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

/**
 * 动画数据包
 */
public record MessageAnimation(int entityID, int animationsIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageAnimation> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "animation"));

    public static final StreamCodec<net.minecraft.network.FriendlyByteBuf, MessageAnimation> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MessageAnimation::entityID,
            ByteBufCodecs.VAR_INT, MessageAnimation::animationsIndex,
            MessageAnimation::new
    );

    @Override
    public CustomPacketPayload.Type<MessageAnimation> type() {
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
    public static void handle(final MessageAnimation message, final net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context context) {
            context.client().execute(() -> {
                Level level = context.client().level;
                if (level != null) {
                    Entity entity = level.getEntity(message.entityID);
                    if (entity instanceof AnimatedEntity animatedEntity) {
                        //可堆叠则由客户端单独计算结束时间并停止动画
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
            });
        }
    }
}
