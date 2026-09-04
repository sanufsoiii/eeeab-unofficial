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
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Was a NetworkEvent-based message (SimpleChannel) on old Forge, then a NeoForge CustomPacketPayload
 * registered through RegisterPayloadHandlersEvent. On Fabric the payload record/StreamCodec carries over
 * unchanged (Mojang mappings); only the handler wiring below changed to ClientPlayNetworking. The
 * AttachmentType here is Fabric API's own (net.fabricmc.fabric.api.attachment.v1.AttachmentType, from the
 * fabric-data-attachment-api-v1 module bundled in the fabric-api umbrella jar this mod already depends on) -
 * see CapabilityHandler for how CAPABILITIES/getCapability are implemented on top of it. Unlike the old
 * NeoForge Supplier<AttachmentType<?>> indirection (attachments were lazily registered behind a Supplier),
 * Fabric attachments are built eagerly at class-init time, so CapabilityHandler.CAPABILITIES holds the
 * AttachmentType instances directly and this record can reference them the same way.
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

    public ICapabilityMessage(LivingEntity entity, boolean flag, AttachmentType<?> capability) {
        this(entity.getId(), ArrayUtils.indexOf(CapabilityHandler.CAPABILITIES, capability), flag);
    }

    @Override
    public CustomPacketPayload.Type<ICapabilityMessage> type() {
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
    public static void handle(final ICapabilityMessage message, final net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context context) {
            context.client().execute(() -> {
                Level level = context.client().level;
                if (level != null) {
                    Entity entity = level.getEntity(message.entityID());
                    if (entity instanceof LivingEntity livingEntity) {
                        // AttachmentType<T> is invariant in T, so an AttachmentType<?> can't be passed to
                        // CapabilityHandler.getCapability's <T> signature directly.
                        @SuppressWarnings("unchecked")
                        AttachmentType<Object> cap = (AttachmentType<Object>) CapabilityHandler.CAPABILITIES[message.capIndex()];
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
