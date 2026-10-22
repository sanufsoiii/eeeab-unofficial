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

    public static void handleClient(PopupNotificationMessage msg) {
        TipNotificationHandler.init(msg.id(), msg.level());
    }
}
