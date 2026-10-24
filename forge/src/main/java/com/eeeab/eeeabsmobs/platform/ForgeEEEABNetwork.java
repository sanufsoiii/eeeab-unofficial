package com.eeeab.eeeabsmobs.platform;

import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ForgeEEEABNetwork implements EEEABNetwork {

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
        Packet<?> packet = CHANNEL.toPacket(NetworkManager.Side.S2C, message);
        if (includeSelf) {
            PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity).send(packet);
        } else {
            PacketDistributor.TRACKING_ENTITY.with(() -> entity).send(packet);
        }
    }
}
