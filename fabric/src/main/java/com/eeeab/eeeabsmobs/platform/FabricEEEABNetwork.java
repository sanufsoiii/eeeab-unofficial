package com.eeeab.eeeabsmobs.platform;

import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Fabric network implementation built on {@link NetworkChannel}.
 * Matches the proven approach in ParCool.
 */
public class FabricEEEABNetwork implements EEEABNetwork {

    private static final NetworkChannel CHANNEL =
            NetworkChannel.create(new ResourceLocation("eeeabsmobs", "main"));

    private final Set<Class<?>> registered = new HashSet<>();

    @Override
    public <T> void register(
            Class<T> messageClass,
            BiConsumer<T, FriendlyByteBuf> encoder,
            Function<FriendlyByteBuf, T> decoder,
            BiConsumer<T, Supplier<NetworkManager.PacketContext>> handler) {
        if (!this.registered.add(messageClass)) return;
        CHANNEL.register(messageClass, encoder, decoder, handler);
    }

    @Override
    public <T> void sendToServer(T message) {
        CHANNEL.sendToServer(message);
    }

    @Override
    public <T> void sendToPlayer(ServerPlayer player, T message) {
        CHANNEL.sendToPlayer(player, message);
    }

    @Override
    public <T> void sendToPlayers(Iterable<ServerPlayer> players, T message) {
        CHANNEL.sendToPlayers(players, message);
    }

    @Override
    public <T> void sendToTracking(Entity entity, T message, boolean includeSelf) {
        Collection<ServerPlayer> players = PlayerLookup.tracking(entity);
        CHANNEL.sendToPlayers(players, message);
        if (includeSelf && entity instanceof ServerPlayer player) {
            CHANNEL.sendToPlayer(player, message);
        }
    }
}
