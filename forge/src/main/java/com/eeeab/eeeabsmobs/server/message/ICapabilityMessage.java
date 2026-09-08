package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.capability.GeneralCapability;
import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.BiConsumer;

/**
 * Was a NetworkEvent-based message on Forge 1.20.1, then rebuilt as a CustomPacketPayload record for
 * NeoForge's payload-registry networking. Forge 1.21.1 has neither NetworkEvent nor NeoForge's registry -
 * it registers CustomPacketPayload/StreamCodec messages through the classic SimpleChannel instead (see
 * ServerProxy#initNetwork), so the record/TYPE/STREAM_CODEC shape is kept and only the handler wiring changes.
 */
public record ICapabilityMessage(int entityID, int capIndex, boolean flag) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ICapabilityMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "capability_state"));

    public static final StreamCodec<FriendlyByteBuf, ICapabilityMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ICapabilityMessage::entityID,
            ByteBufCodecs.VAR_INT, ICapabilityMessage::capIndex,
            ByteBufCodecs.BOOL, ICapabilityMessage::flag,
            ICapabilityMessage::new
    );

    public ICapabilityMessage(LivingEntity entity, boolean flag, Capability<?> capability) {
        this(entity.getId(), ArrayUtils.indexOf(CapabilityHandler.CAPABILITIES, capability), flag);
    }

    @Override
    public CustomPacketPayload.Type<ICapabilityMessage> type() {
        return TYPE;
    }

    public static class Handler implements BiConsumer<ICapabilityMessage, CustomPayloadEvent.Context> {
        @Override
        public void accept(final ICapabilityMessage message, final CustomPayloadEvent.Context context) {
            context.enqueueWork(() -> {
                if (Minecraft.getInstance().level != null) {
                    Entity entity = Minecraft.getInstance().level.getEntity(message.entityID());
                    if (entity instanceof LivingEntity livingEntity) {
                        @SuppressWarnings("unchecked")
                        Capability<Object> cap = (Capability<Object>) CapabilityHandler.CAPABILITIES[message.capIndex()];
                        if (CapabilityHandler.getCapability(livingEntity, cap) instanceof GeneralCapability iCapability) {
                            if (message.flag()) iCapability.onStart(livingEntity);
                            else iCapability.onEnd(livingEntity);
                        }
                    }
                }
            });
        }
    }
}
