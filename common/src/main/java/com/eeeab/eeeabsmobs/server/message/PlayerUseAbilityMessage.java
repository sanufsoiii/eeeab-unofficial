package com.eeeab.eeeabsmobs.server.message;

import com.eeeab.eeeabsmobs.server.ability.AbilityHandler;
import com.eeeab.eeeabsmobs.server.capability.AbilityCapability;
import com.eeeab.eeeabsmobs.server.handler.CapabilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record PlayerUseAbilityMessage(int index) {

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.index);
    }

    public static PlayerUseAbilityMessage decode(FriendlyByteBuf buffer) {
        return new PlayerUseAbilityMessage(buffer.readVarInt());
    }

    public static void handleServer(PlayerUseAbilityMessage message, ServerPlayer player) {
        AbilityCapability.IAbilityCapability abilityCapability = CapabilityHandler.getCapability(player, CapabilityHandler.ABILITY_CAPABILITY);
        if (abilityCapability != null) {
            AbilityHandler.INSTANCE.sendAbilityMessage(player, abilityCapability.getAbilityTypeByEntity(player)[message.index()]);
        }
    }
}
