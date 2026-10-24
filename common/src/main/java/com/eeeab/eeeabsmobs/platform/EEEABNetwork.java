package com.eeeab.eeeabsmobs.platform;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface EEEABNetwork {

    <T> void register(
            Class<T> messageClass,
            BiConsumer<T, FriendlyByteBuf> encoder,
            Function<FriendlyByteBuf, T> decoder,
            BiConsumer<T, Supplier<NetworkManager.PacketContext>> handler);

    <T> void sendToServer(T message);

    <T> void sendToPlayer(ServerPlayer player, T message);

    <T> void sendToPlayers(Iterable<ServerPlayer> players, T message);

    <T> void sendToTracking(Entity entity, T message, boolean includeSelf);
}
