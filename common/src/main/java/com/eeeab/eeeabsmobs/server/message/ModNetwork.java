package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.animate.server.message.MessageAnimation;
import com.eeeab.animate.server.message.MessagePlayAnimation;
import com.eeeab.animate.server.message.MessageStopAnimation;
import com.eeeab.eeeabsmobs.platform.PlatformServices;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class ModNetwork {

    private ModNetwork() {}

    public static void init() {
        PlatformServices.get().getNetwork().register(
                ICapabilityMessage.class,
                ICapabilityMessage::encode,
                ICapabilityMessage::decode,
                (msg, ctx) -> ctx.get().queue(() -> ICapabilityMessage.handleClient(msg, ctx.get().getPlayer().level()))
        );

        PlatformServices.get().getNetwork().register(
                UseAbilityMessage.class,
                UseAbilityMessage::encode,
                UseAbilityMessage::decode,
                (msg, ctx) -> ctx.get().queue(() -> UseAbilityMessage.handleClient(msg, ctx.get().getPlayer().level()))
        );

        PlatformServices.get().getNetwork().register(
                PlayerUseAbilityMessage.class,
                PlayerUseAbilityMessage::encode,
                PlayerUseAbilityMessage::decode,
                (msg, ctx) -> ctx.get().queue(() -> PlayerUseAbilityMessage.handleServer(msg, (ServerPlayer) ctx.get().getPlayer()))
        );

        PlatformServices.get().getNetwork().register(
                UpdateBossBarMessage.class,
                UpdateBossBarMessage::encode,
                UpdateBossBarMessage::decode,
                (msg, ctx) -> ctx.get().queue(() -> UpdateBossBarMessage.handleClient(msg))
        );

        PlatformServices.get().getNetwork().register(
                PopupNotificationMessage.class,
                PopupNotificationMessage::encode,
                PopupNotificationMessage::decode,
                (msg, ctx) -> ctx.get().queue(() -> PopupNotificationMessage.handleClient(msg))
        );

        PlatformServices.get().getNetwork().register(
                MessageAnimation.class,
                MessageAnimation::encode,
                MessageAnimation::decode,
                (msg, ctx) -> ctx.get().queue(() -> MessageAnimation.handleClient(msg, ctx.get().getPlayer().level()))
        );

        PlatformServices.get().getNetwork().register(
                MessagePlayAnimation.class,
                MessagePlayAnimation::encode,
                MessagePlayAnimation::decode,
                (msg, ctx) -> ctx.get().queue(() -> MessagePlayAnimation.handleServer(msg, (ServerPlayer) ctx.get().getPlayer()))
        );

        PlatformServices.get().getNetwork().register(
                MessageStopAnimation.class,
                MessageStopAnimation::encode,
                MessageStopAnimation::decode,
                (msg, ctx) -> ctx.get().queue(() -> MessageStopAnimation.handleClient(msg, ctx.get().getPlayer().level()))
        );
    }

    public static <T> void sendToServer(T message) {
        PlatformServices.get().getNetwork().sendToServer(message);
    }

    public static <T> void sendToPlayer(ServerPlayer player, T message) {
        PlatformServices.get().getNetwork().sendToPlayer(player, message);
    }

    public static <T> void sendToPlayers(Iterable<ServerPlayer> players, T message) {
        PlatformServices.get().getNetwork().sendToPlayers(players, message);
    }

    public static <T> void sendToTracking(Entity entity, T message, boolean includeSelf) {
        PlatformServices.get().getNetwork().sendToTracking(entity, message, includeSelf);
    }
}
