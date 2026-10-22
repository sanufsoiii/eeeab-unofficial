package com.eeeab.eeeabsmobs.platform;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.function.BiConsumer;

public interface EEEABNetwork {

    <T extends CustomPacketPayload> void register(
            Class<T> payloadClass,
            CustomPacketPayload.Type<T> type,
            Identifier wireId,
            StreamCodec<?, T> codec,
            boolean clientbound,
            BiConsumer<T, NetworkManager.PacketContext> handler);

    <T extends CustomPacketPayload> void sendToServer(
            T payload, Identifier wireId, StreamCodec<?, T> codec);

    <T extends CustomPacketPayload> void sendToPlayer(
            ServerPlayer player, T payload, Identifier wireId, StreamCodec<?, T> codec);

    <T extends CustomPacketPayload> void sendToPlayers(
            Iterable<ServerPlayer> players, T payload, Identifier wireId, StreamCodec<?, T> codec);

    <T extends CustomPacketPayload> void sendToTracking(
            Entity entity, T payload, Identifier wireId, StreamCodec<?, T> codec, boolean includeSelf);

    static RegistryFriendlyByteBuf encode(StreamCodec<?, ?> codec, CustomPacketPayload payload) {
        io.netty.buffer.ByteBuf raw = io.netty.buffer.Unpooled.buffer();
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(raw, net.minecraft.core.RegistryAccess.EMPTY);
        @SuppressWarnings({"unchecked", "rawtypes"})
        StreamCodec typed = (StreamCodec) codec;
        typed.encode(buffer, payload);
        return buffer;
    }
}
