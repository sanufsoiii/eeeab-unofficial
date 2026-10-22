package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.capability.GeneralCapability;
import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public record ICapabilityMessage(int entityID, int capIndex, boolean flag) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ICapabilityMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "capability_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ICapabilityMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ICapabilityMessage::entityID,
            ByteBufCodecs.VAR_INT, ICapabilityMessage::capIndex,
            ByteBufCodecs.BOOL, ICapabilityMessage::flag,
            ICapabilityMessage::new
    );

    public ICapabilityMessage(LivingEntity entity, boolean flag, CapabilityHandler.CapabilityToken<?> capability) {
        this(entity.getId(), capability.getIndex(), flag);
    }

    @Override
    public CustomPacketPayload.Type<ICapabilityMessage> type() {
        return TYPE;
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
