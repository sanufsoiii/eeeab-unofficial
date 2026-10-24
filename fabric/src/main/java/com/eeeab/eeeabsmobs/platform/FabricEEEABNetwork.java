package com.eeeab.eeeabsmobs.platform;

import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.world.entity.Entity;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Fabric network implementation built on {@link NetworkChannel}.
 * Matches the proven approach in ParCool.
 */
public class FabricEEEABNetwork implements EEEABNetwork {

    private static final NetworkChannel CHANNEL =
            NetworkChannel.create(new ResourceLocation("eeeabsmobs", "main"));

    private final Set<Class<?>> registered = new HashSet<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CustomPacketPayload> void register(
            Class<T> payloadClass,
            CustomPacketPayload.Type<T> type,
            ResourceLocation wireId,
            StreamCodec<?, T> codec,
            boolean clientbound,
            BiConsumer<T, NetworkManager.PacketContext> handler) {
        if (!this.registered.add(payloadClass)) return;
        StreamCodec<Object, T> typed = (StreamCodec<Object, T>) codec;
        CHANNEL.register(
                payloadClass,
                (payload, buffer) -> typed.encode((FriendlyByteBuf) buffer, payload),
                buffer -> typed.decode((FriendlyByteBuf) buffer),
                (payload, context) -> context.get().queue(() -> handler.accept(payload, context.get()))
        );
    }

    @Override
    public <T extends CustomPacketPayload> void sendToServer(
            T payload, ResourceLocation wireId, StreamCodec<?, T> codec) {
        CHANNEL.sendToServer(payload);
    }

    @Override
    public <T extends CustomPacketPayload> void sendToPlayer(
            ServerPlayer player, T payload, ResourceLocation wireId, StreamCodec<?, T> codec) {
        CHANNEL.sendToPlayer(player, payload);
    }

    @Override
    public <T extends CustomPacketPayload> void sendToPlayers(
            Iterable<ServerPlayer> players, T payload, ResourceLocation wireId, StreamCodec<?, T> codec) {
        CHANNEL.sendToPlayers(players, payload);
    }

    @Override
    public <T extends CustomPacketPayload> void sendToTracking(
            Entity entity, T payload, ResourceLocation wireId, StreamCodec<?, T> codec, boolean includeSelf) {
        Collection<ServerPlayer> players = PlayerLookup.tracking(entity);
        CHANNEL.sendToPlayers(players, payload);
        if (includeSelf && entity instanceof ServerPlayer player) {
            CHANNEL.sendToPlayer(player, payload);
        }
    }
}
