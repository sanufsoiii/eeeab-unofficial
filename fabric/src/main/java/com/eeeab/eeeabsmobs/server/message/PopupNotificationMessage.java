package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.gui.TipNotificationHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PopupNotificationMessage(Identifier id, int level) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PopupNotificationMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "popup_notification"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PopupNotificationMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, PopupNotificationMessage::id,
            ByteBufCodecs.VAR_INT, PopupNotificationMessage::level,
            PopupNotificationMessage::new
    );

    @Override
    public CustomPacketPayload.Type<PopupNotificationMessage> type() {
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
    public static void handle(PopupNotificationMessage msg, net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context context) {
            context.client().execute(() -> TipNotificationHandler.init(msg.id(), msg.level()));
        }
    }
}
