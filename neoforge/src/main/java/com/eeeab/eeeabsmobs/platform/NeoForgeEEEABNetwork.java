package com.eeeab.eeeabsmobs.platform;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.function.BiConsumer;

/**
 * NeoForge network implementation built on raw NetworkManager.
 */
public class NeoForgeEEEABNetwork implements EEEABNetwork {

    @Override
    public <T extends CustomPacketPayload> void register(
            Class<T> payloadClass,
            CustomPacketPayload.Type<T> type,
            ResourceLocation wireId,
            StreamCodec<?, T> codec,
            boolean clientbound,
            BiConsumer<T, NetworkManager.PacketContext> handler) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        StreamCodec<? super RegistryFriendlyByteBuf, T> erased = (StreamCodec) codec;
        NetworkManager.registerReceiver(
                clientbound ? NetworkManager.Side.S2C : NetworkManager.Side.C2S,
                wireId,
                (buf, context) -> {
                    T payload = erased.decode((RegistryFriendlyByteBuf) buf);
                    context.queue(() -> handler.accept(payload, context));
                }
        );
    }

    @Override
    public <T extends CustomPacketPayload> void sendToServer(
            T payload, ResourceLocation wireId, StreamCodec<?, T> codec) {
        NetworkManager.sendToServer(wireId, EEEABNetwork.encode(codec, payload));
    }

    @Override
    public <T extends CustomPacketPayload> void sendToPlayer(
            ServerPlayer player, T payload, ResourceLocation wireId, StreamCodec<?, T> codec) {
        NetworkManager.sendToPlayer(player, wireId, EEEABNetwork.encode(codec, payload));
    }

    @Override
    public <T extends CustomPacketPayload> void sendToPlayers(
            Iterable<ServerPlayer> players, T payload, ResourceLocation wireId, StreamCodec<?, T> codec) {
        NetworkManager.sendToPlayers(players, wireId, EEEABNetwork.encode(codec, payload));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> void sendToTracking(
            Entity entity, T payload, ResourceLocation wireId, StreamCodec<?, T> codec, boolean includeSelf) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            Packet<ClientGamePacketListener> packet = (Packet<ClientGamePacketListener>) (Packet<?>) NetworkManager.toPacket(NetworkManager.Side.S2C, wireId, EEEABNetwork.encode(codec, payload));
            if (includeSelf) {
                serverLevel.getChunkSource().sendToTrackingPlayersAndSelf(entity, packet);
            } else {
                serverLevel.getChunkSource().sendToTrackingPlayers(entity, packet);
            }
        }
    }
}
