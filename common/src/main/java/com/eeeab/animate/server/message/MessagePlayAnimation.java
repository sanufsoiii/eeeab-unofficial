package com.eeeab.animate.server.message;

import com.eeeab.animate.server.animation.AnimatedEntity;
import com.eeeab.animate.server.handler.AnimationHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record MessagePlayAnimation(int entityID, int animationsIndex) {

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityID);
        buffer.writeVarInt(this.animationsIndex);
    }

    public static MessagePlayAnimation decode(FriendlyByteBuf buffer) {
        return new MessagePlayAnimation(buffer.readVarInt(), buffer.readVarInt());
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
