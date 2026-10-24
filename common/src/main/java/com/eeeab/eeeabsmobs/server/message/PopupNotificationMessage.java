package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.client.gui.TipNotificationHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record PopupNotificationMessage(ResourceLocation id, int level) {

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.id);
        buffer.writeVarInt(this.level);
    }

    public static PopupNotificationMessage decode(FriendlyByteBuf buffer) {
        return new PopupNotificationMessage(buffer.readResourceLocation(), buffer.readVarInt());
    }

    public static void handleClient(PopupNotificationMessage msg) {
        TipNotificationHandler.init(msg.id(), msg.level());
    }
}
