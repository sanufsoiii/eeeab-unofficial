package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.animate.server.message.MessageAnimation;
import com.eeeab.animate.server.message.MessagePlayAnimation;
import com.eeeab.animate.server.message.MessageStopAnimation;
import com.eeeab.eeeabsmobs.platform.EEEABNetwork;
import com.eeeab.eeeabsmobs.platform.PlatformServices;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class ModNetwork {

    private static final Map<Class<? extends CustomPacketPayload>, Registration<?>> REGISTRATIONS = new HashMap<>();

    private record Registration<T extends CustomPacketPayload>(
            CustomPacketPayload.Type<T> type,
            ResourceLocation wireId,
            StreamCodec<?, T> codec,
            boolean clientbound
    ) {}

    private ModNetwork() {}

    private static <T extends CustomPacketPayload> void register(
            Class<T> payloadClass,
            CustomPacketPayload.Type<T> type,
            StreamCodec<?, T> codec,
            boolean clientbound,
            BiConsumer<T, NetworkManager.PacketContext> handler) {
        ResourceLocation wireId = type.id();
        REGISTRATIONS.put(payloadClass, new Registration<>(type, wireId, codec, clientbound));
        PlatformServices.get().getNetwork().register(payloadClass, type, wireId, codec, clientbound, handler);
    }

    public static void init() {
        register(ICapabilityMessage.class, ICapabilityMessage.TYPE, ICapabilityMessage.STREAM_CODEC, true,
                (msg, ctx) -> ICapabilityMessage.handleClient(msg, ctx.getPlayer().level()));

        register(UseAbilityMessage.class, UseAbilityMessage.TYPE, UseAbilityMessage.STREAM_CODEC, true,
                (msg, ctx) -> UseAbilityMessage.handleClient(msg, ctx.getPlayer().level()));

        register(PlayerUseAbilityMessage.class, PlayerUseAbilityMessage.TYPE, PlayerUseAbilityMessage.STREAM_CODEC, false,
                (msg, ctx) -> PlayerUseAbilityMessage.handleServer(msg, (ServerPlayer) ctx.getPlayer()));

        register(UpdateBossBarMessage.class, UpdateBossBarMessage.TYPE, UpdateBossBarMessage.STREAM_CODEC, true,
                (msg, ctx) -> UpdateBossBarMessage.handleClient(msg));

        register(PopupNotificationMessage.class, PopupNotificationMessage.TYPE, PopupNotificationMessage.STREAM_CODEC, true,
                (msg, ctx) -> PopupNotificationMessage.handleClient(msg));

        register(MessageAnimation.class, MessageAnimation.TYPE, MessageAnimation.STREAM_CODEC, true,
                (msg, ctx) -> MessageAnimation.handleClient(msg, ctx.getPlayer().level()));

        register(MessagePlayAnimation.class, MessagePlayAnimation.TYPE, MessagePlayAnimation.STREAM_CODEC, false,
                (msg, ctx) -> MessagePlayAnimation.handleServer(msg, (ServerPlayer) ctx.getPlayer()));

        register(MessageStopAnimation.class, MessageStopAnimation.TYPE, MessageStopAnimation.STREAM_CODEC, true,
                (msg, ctx) -> MessageStopAnimation.handleClient(msg, ctx.getPlayer().level()));
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> Registration<T> get(T payload) {
        Registration<?> reg = REGISTRATIONS.get(payload.getClass());
        if (reg == null) {
            throw new IllegalArgumentException("Unregistered packet payload: " + payload.getClass().getName());
        }
        return (Registration<T>) reg;
    }

    public static <T extends CustomPacketPayload> void sendToServer(T payload) {
        Registration<T> reg = get(payload);
        PlatformServices.get().getNetwork().sendToServer(payload, reg.wireId(), reg.codec());
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload) {
        Registration<T> reg = get(payload);
        PlatformServices.get().getNetwork().sendToPlayer(player, payload, reg.wireId(), reg.codec());
    }

    public static <T extends CustomPacketPayload> void sendToPlayers(Iterable<ServerPlayer> players, T payload) {
        Registration<T> reg = get(payload);
        PlatformServices.get().getNetwork().sendToPlayers(players, payload, reg.wireId(), reg.codec());
    }

    public static <T extends CustomPacketPayload> void sendToTracking(Entity entity, T payload, boolean includeSelf) {
        Registration<T> reg = get(payload);
        PlatformServices.get().getNetwork().sendToTracking(entity, payload, reg.wireId(), reg.codec(), includeSelf);
    }
}
