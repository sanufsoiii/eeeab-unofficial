package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.client.gui.TipNotificationHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PopupNotificationMessage(ResourceLocation id, int level) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PopupNotificationMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "popup_notification"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PopupNotificationMessage> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, PopupNotificationMessage::id,
            ByteBufCodecs.VAR_INT, PopupNotificationMessage::level,
            PopupNotificationMessage::new
    );

    @Override
    public CustomPacketPayload.Type<PopupNotificationMessage> type() {
        return TYPE;
    }

    public static void handle(PopupNotificationMessage msg, IPayloadContext context) {
        context.enqueueWork(() -> TipNotificationHandler.init(msg.id(), msg.level()));
    }
}
