package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.capability.GeneralCapability;
import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.Supplier;

/**
 * Was a NetworkEvent-based message (SimpleChannel). NeoForge's networking was redesigned around
 * CustomPacketPayload records registered through RegisterPayloadHandlersEvent - see ServerProxy#initNetwork.
 */
public record ICapabilityMessage(int entityID, int capIndex, boolean flag) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ICapabilityMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "capability_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ICapabilityMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ICapabilityMessage::entityID,
            ByteBufCodecs.VAR_INT, ICapabilityMessage::capIndex,
            ByteBufCodecs.BOOL, ICapabilityMessage::flag,
            ICapabilityMessage::new
    );

    public ICapabilityMessage(LivingEntity entity, boolean flag, Supplier<AttachmentType<?>> capability) {
        this(entity.getId(), ArrayUtils.indexOf(CapabilityHandler.CAPABILITIES, capability), flag);
    }

    @Override
    public CustomPacketPayload.Type<ICapabilityMessage> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<ICapabilityMessage> {
        @Override
        public void handle(final ICapabilityMessage message, final IPayloadContext context) {
            context.enqueueWork(() -> {
                if (Minecraft.getInstance().level != null) {
                    Entity entity = Minecraft.getInstance().level.getEntity(message.entityID());
                    if (entity instanceof LivingEntity livingEntity) {
                        // Supplier<AttachmentType<T>> is invariant in T, so a Supplier<AttachmentType<?>> can't be
                        // passed to CapabilityHandler.getCapability's <T> signature directly - see the same note
                        // in ServerEventHandler#doCapabilityEffect.
                        @SuppressWarnings("unchecked")
                        Supplier<AttachmentType<Object>> cap = (Supplier) CapabilityHandler.CAPABILITIES[message.capIndex()];
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
