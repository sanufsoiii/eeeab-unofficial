package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.server.capability.GeneralCapability;
import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public record ICapabilityMessage(int entityID, int capIndex, boolean flag) {

    public ICapabilityMessage(LivingEntity entity, boolean flag, CapabilityHandler.CapabilityToken<?> capability) {
        this(entity.getId(), capability.getIndex(), flag);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityID);
        buffer.writeVarInt(this.capIndex);
        buffer.writeBoolean(this.flag);
    }

    public static ICapabilityMessage decode(FriendlyByteBuf buffer) {
        return new ICapabilityMessage(buffer.readVarInt(), buffer.readVarInt(), buffer.readBoolean());
    }

    public static void handleClient(ICapabilityMessage message, Level level) {
        if (level != null) {
            Entity entity = level.getEntity(message.entityID());
            if (entity instanceof LivingEntity livingEntity) {
                if (message.capIndex() >= 0 && message.capIndex() < CapabilityHandler.CAPABILITIES.length) {
                    CapabilityHandler.CapabilityToken<?> cap = CapabilityHandler.CAPABILITIES[message.capIndex()];
                    Object raw = CapabilityHandler.getCapability(livingEntity, cap);
                    if (raw instanceof GeneralCapability iCapability) {
                        if (message.flag()) iCapability.onStart(livingEntity);
                        else iCapability.onEnd(livingEntity);
                    }
                }
            }
        }
    }
}
